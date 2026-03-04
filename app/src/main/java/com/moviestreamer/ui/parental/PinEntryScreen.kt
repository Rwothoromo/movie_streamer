package com.moviestreamer.ui.parental

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * PIN entry screen for parental controls.
 *
 * Shows a PIN entry form. On correct PIN entry calls [onUnlocked];
 * on cancel calls [onCancel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinEntryScreen(
    manager: ParentalControlsManager,
    mode: PinMode,
    onUnlocked: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(48.dp)
        ) {
            Text(
                text = when (mode) {
                    PinMode.SET -> "Set Parental PIN"
                    PinMode.VERIFY -> "Enter Parental PIN"
                    PinMode.CLEAR -> "Enter PIN to Disable Controls"
                },
                color = Color.White,
                fontSize = 28.sp
            )

            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 8) pin = it.filter { c -> c.isDigit() } },
                label = { Text("PIN", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth(0.5f)
            )

            if (mode == PinMode.SET) {
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 8) confirmPin = it.filter { c -> c.isDigit() } },
                    label = { Text("Confirm PIN", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
            }

            error?.let {
                Text(it, color = Color.Red, fontSize = 16.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        error = null
                        when (mode) {
                            PinMode.SET -> {
                                when {
                                    pin.length < ParentalControlsManager.MIN_PIN_LENGTH ->
                                        error = "PIN must be at least ${ParentalControlsManager.MIN_PIN_LENGTH} digits"
                                    pin != confirmPin ->
                                        error = "PINs do not match"
                                    else -> {
                                        manager.setPin(pin)
                                        manager.isEnabled = true
                                        onUnlocked()
                                    }
                                }
                            }
                            PinMode.VERIFY, PinMode.CLEAR -> {
                                if (manager.verifyPin(pin)) {
                                    if (mode == PinMode.CLEAR) {
                                        manager.clearPin()
                                        manager.isEnabled = false
                                    }
                                    onUnlocked()
                                } else {
                                    error = "Incorrect PIN"
                                    pin = ""
                                }
                            }
                        }
                    }
                ) {
                    Text(if (mode == PinMode.SET) "Set PIN" else "Confirm")
                }
                TextButton(onClick = onCancel) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    }
}

enum class PinMode { SET, VERIFY, CLEAR }
