package com.artificient.gpstracking.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun rememberLocationPermissionState(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val permissions = remember {
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }.toTypedArray()
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = permissions.all { perm -> result[perm] == true }
        onPermissionResult(granted)
    }
    // The returned lambda must NOT use any @Composable calls
    return remember(launcher, context, permissions) {
        {
            val allGranted = permissions.all { perm ->
                ContextCompat.checkSelfPermission(context, perm) == PermissionChecker.PERMISSION_GRANTED
            }
            if (allGranted) {
                onPermissionResult(true)
            } else {
                launcher.launch(permissions)
            }
        }
    }
} 