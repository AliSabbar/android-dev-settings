# My App Settings (Developer Tools)

A simple Android utility to quickly toggle system-level developer settings directly from your device.

## Features

- **Toggle Developer Options:** Enable or disable Developer Options with a single switch.
- **Toggle USB Debugging:** Quickly turn ADB on or off.
- **Direct Shortcuts:** Jump straight to the system's Developer Options page.

## Important: Permission Setup

This app requires the `WRITE_SECURE_SETTINGS` permission, which is a protected Android permission. To use the toggles, you must grant this permission once via ADB after installing the app.

### Instructions:

1.  **Enable USB Debugging** on your phone (usually by tapping "Build Number" 7 times in Settings > About Phone).
2.  **Connect your phone** to your computer via USB.
3.  Open a terminal and verify your device is connected:
    ```bash
    adb devices
    ```
4.  Run the following command to grant the necessary permission (replace `<your_device_id>` if multiple devices are connected):
    ```bash
    adb -s <your_device_id> shell pm grant com.example.myappsettings android.permission.WRITE_SECURE_SETTINGS
    ```

## Installation

### "Package appears to be invalid" Error
If you receive this error when manually installing the debug APK, it is likely because the APK is marked as `testOnly`. 

**Fix:**
Install via ADB using the `-t` flag:
```bash
adb install -t app-debug.apk
```

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Minimum SDK:** 26 (Android 8.0)
- **Target SDK:** 35
