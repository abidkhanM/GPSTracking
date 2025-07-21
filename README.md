# GPS Tracking & Data Collection App

A modern Android app for real-time GPS tracking, trip history, and settings management, built with Jetpack Compose and MVVM architecture.

---

## 🚀 Features
- Real time GPS tracking with map view
- Trip history with duration and distance
- Configurable location update interval and background tracking
- Modern Material 3 UI with Jetpack Compose
- MVVM architecture, Room, DataStore, Koin DIv

---

## 🏗️ Architecture
- **MVVM** (Model-View-ViewModel)
- **Repository pattern** for data abstraction
- **Room** for local trip and location storage
- **DataStore** for settings persistence
- **Koin** for dependency injection
- **Kotlin Coroutines/Flow** for async and reactive data
- **Jetpack Compose** for all UI

---

## 🛠️ Tech Stack / Libraries
- **Kotlin** (100%)
- **Jetpack Compose** (Material 3)
- **Google Maps Compose**
- **Room** (androidx.room)
- **DataStore** (androidx.datastore)
- **Koin** (io.insert-koin)
- **Kotlin Coroutines**
- **Play Services Location**

---

## 📝 Setup & Running

### 1. Clone the repository
```sh
git clone <your-repo-url>
cd ArtificientGPSTracking
```

### 2. Add your Google Maps API Key
- Open `local.properties` (create if missing) in the project root.
- Add your API key:
  ```
  MAPS_API_KEY=your_actual_api_key_here
  ```
- [How to get an API key?](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
- **Billing must be enabled** on your Google Cloud project.

### 3. Build and run
- Open in Android Studio.
- Sync Gradle.
- Run on a device or emulator (with Google Play Services).

---

## 📁 Project Structure
```
app/
 └─ src/main/java/com/artificient/gpstracking/
      ├─ data/           # Repository, Room, DataStore
      ├─ di/             # Koin DI modules
      ├─ location/       # Location update helpers
      ├─ navigation/     # Compose navigation
      ├─ ui/screens/     # Compose screens
      ├─ ui/theme/       # Compose theme
      ├─ viewmodel/      # ViewModels (Tracking, History, Settings)
      └─ App.kt          # Koin application class
```

---

## 📚 Main Libraries
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Google Maps Compose](https://github.com/googlemaps/android-maps-compose)
- [Room](https://developer.android.com/jetpack/androidx/releases/room)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Koin](https://insert-koin.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

## 🧑‍💻 Usage
- **Tracking:** Tap Start to begin tracking your location. Pause/Resume/Stop as needed.
- **Trip History:** View all recorded trips with start time, duration, and distance.
- **Settings:** Configure location update interval and background tracking.

---

## 📝 Notes
- The app requires location permissions and a valid Google Maps API key.
- Billing must be enabled on your Google Cloud project for Maps SDK.
- All sensitive keys are kept out of version control via `local.properties`.

---

## 📄 License
MIT License

Copyright (c) [2025] [Abid Khan]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.