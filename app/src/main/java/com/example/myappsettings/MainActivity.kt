package com.example.myappsettings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.myappsettings.ui.theme.MyappsettingsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyappsettingsTheme {
                // --- Force Left-to-Right (LTR) Layout ---
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        DevToggleScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun DevToggleScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val clipboardManager = LocalClipboardManager.current

    // Helper functions to check actual system state
    fun isUsbDebuggingEnabled(): Boolean =
        Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) == 1

    fun isDeveloperOptionsEnabled(): Boolean =
        Settings.Global.getInt(contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1

    // State variables
    var usbDebuggingEnabled by remember { mutableStateOf(isUsbDebuggingEnabled()) }
    var developerOptionsEnabled by remember { mutableStateOf(isDeveloperOptionsEnabled()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Developer Tools",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Developer Options Toggle
        SettingsRow(
            label = "Developer Options",
            subtitle = if (developerOptionsEnabled) "Enabled" else "Disabled",
            checked = developerOptionsEnabled,
            onCheckedChange = { checked ->
                try {
                    Settings.Global.putInt(contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, if (checked) 1 else 0)
                    developerOptionsEnabled = checked
                } catch (_: SecurityException) {
                    showPermissionError(context)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // USB Debugging Toggle
        SettingsRow(
            label = "USB Debugging",
            subtitle = if (usbDebuggingEnabled) "Enabled" else "Disabled",
            checked = usbDebuggingEnabled,
            onCheckedChange = { checked ->
                try {
                    Settings.Global.putInt(contentResolver, Settings.Global.ADB_ENABLED, if (checked) 1 else 0)
                    usbDebuggingEnabled = checked
                } catch (_: SecurityException) {
                    showPermissionError(context)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // MODIFIED: Developer Options Button
        Button(
            onClick = {
                try {
                    // Try to open general Developer Options Directly
                    val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Fallback to general settings or show error
                    try {
                        val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
                        context.startActivity(fallbackIntent)
                        Toast.makeText(context, "Could not open specific settings, opening general settings", Toast.LENGTH_SHORT).show()
                    } catch (e2: Exception) {
                        Toast.makeText(context, "Could not open settings", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Developer Options")
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- Instruction Card with Copy Button ---
        val adbCommand = "adb -s <your_device_id> shell pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS"

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚠️ First-Time Setup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "1. Plug phone into computer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "2. Run 'adb devices' in terminal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "3. Replace <your_device_id> in the command below and run it:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // The Command Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = adbCommand,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(adbCommand))
                            Toast.makeText(context, "Command copied!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy command",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun showPermissionError(context: Context) {
    Toast.makeText(
        context,
        "Permission Denied! Use ADB to grant WRITE_SECURE_SETTINGS.",
        Toast.LENGTH_LONG
    ).show()
}