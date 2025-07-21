package com.artificient.gpstracking.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.artificient.gpstracking.location.LocationUpdatesManager
import com.artificient.gpstracking.navigation.LocalSnackbarHostState
import com.artificient.gpstracking.viewmodel.TrackingState
import com.artificient.gpstracking.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import org.koin.androidx.compose.koinViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen() {
    val viewModel: TrackingViewModel = koinViewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val trackingState by viewModel.trackingState.collectAsState()
    val metrics by viewModel.metrics.collectAsState()
    var hasLocationPermission by remember { mutableStateOf(false) }
    var lastLatLng by remember { mutableStateOf<LatLng?>(null) }
    val snackbarHostState = LocalSnackbarHostState.current
    var showLoading by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showGoToSettingsDialog by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }

    val foregroundPermissions = remember {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun checkForegroundLocationPermission(): Boolean {
        return foregroundPermissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = foregroundPermissions.all { perm -> result[perm] == true }
        hasLocationPermission = granted
        if (granted) {
            viewModel.startTracking()
        } else {
            // If any permission denied, check if we should show rationale
            val shouldShowRationale = foregroundPermissions.any { perm ->
                ActivityCompat.shouldShowRequestPermissionRationale(
                    (context as? android.app.Activity) ?: return@any false, perm
                )
            }
            if (shouldShowRationale) {
                showRationaleDialog = true
            } else {
                showGoToSettingsDialog = true
            }
        }
    }

    // On launch, fetch last known location
    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                lastLatLng = LatLng(location.latitude, location.longitude)
            }
        } catch (_: Exception) {}
    }

    // Show loading indicator if waiting for location
    LaunchedEffect(trackingState, hasLocationPermission) {
        showLoading = trackingState == TrackingState.Tracking && hasLocationPermission && lastLatLng == null
    }

    // Show Snackbar for tracking started/stopped
    LaunchedEffect(trackingState) {
        when (trackingState) {
            TrackingState.Tracking -> snackbarHostState.showSnackbar("Tracking started")
            TrackingState.Finished -> snackbarHostState.showSnackbar("Tracking stopped")
            else -> {}
        }
    }

    // Location updates manager
    val locationManager = remember {
        mutableStateOf<LocationUpdatesManager?>(null)
    }

    // Start/stop location updates based on tracking state
    LaunchedEffect(trackingState, hasLocationPermission) {
        if (trackingState == TrackingState.Tracking && hasLocationPermission) {
            val manager = LocationUpdatesManager(context, 1000L) { lat, lng, speed ->
                lastLatLng = LatLng(lat, lng)
                viewModel.onLocationUpdate(lat, lng, speed)
            }
            manager.start()
            locationManager.value = manager
        } else {
            locationManager.value?.stop()
            locationManager.value = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(bottom = 8.dp)
        ) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(lastLatLng ?: LatLng(0.0, 0.0), 16f)
            }
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState
            ) {
                lastLatLng?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "You"
                    )
                }
            }
            if (showLoading) {
                Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        // Metrics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(label = "Speed", value = "${metrics.speed} m/s")
                MetricItem(label = "Distance", value = "${"%.2f".format(metrics.distance / 1000)} km")
                MetricItem(label = "Time", value = formatElapsed(metrics.elapsedMillis))
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when (trackingState) {
                TrackingState.Idle, TrackingState.Finished -> {
                    Button(
                        onClick = {
                            if (checkForegroundLocationPermission()) {
                                hasLocationPermission = true
                                viewModel.startTracking()
                            } else {
                                permissionLauncher.launch(foregroundPermissions)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Start")
                    }
                }
                TrackingState.Tracking -> {
                    Button(
                        onClick = { viewModel.pauseTracking() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Pause")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.stopTracking() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Stop")
                    }
                }
                TrackingState.Paused -> {
                    Button(
                        onClick = { viewModel.resumeTracking() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Resume")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.stopTracking() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Stop")
                    }
                }
            }
        }
    }

    // Rationale Dialog
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("Location Permission Required") },
            text = { Text("This app needs location permission to track your trips. Please grant location permission.") },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog = false
                    permissionLauncher.launch(foregroundPermissions)
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Go to Settings Dialog
    if (showGoToSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showGoToSettingsDialog = false },
            title = { Text("Permission Denied Permanently") },
            text = { Text("Location permission was denied permanently. Please enable it in app settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showGoToSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoToSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatElapsed(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
} 