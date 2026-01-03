/*
 * Copyright (c) 2025 Alexander Yaburov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.impa.knockonports.service.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.impa.knockonports.extension.findFragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton helper class for managing biometric authentication within the application.
 *
 * This class provides functionalities to check for the availability of biometric authentication
 * (including strong biometrics and device credentials) and to launch the biometric prompt.
 * It is designed to be a lifecycle-aware component by implementing [DefaultLifecycleObserver],
 * which allows it to automatically refresh the biometric availability status when the app is resumed.
 *
 * The availability is exposed as a [Flow] so that UI components can reactively update
 * based on whether a biometric/credential lock can be used.
 *
 * @property appContext The application context, injected by Hilt.
 */
@Singleton
class BiometricHelper @Inject constructor(
    @ApplicationContext private val appContext: Context
) : DefaultLifecycleObserver {

    private val authenticators = BIOMETRIC_WEAK or DEVICE_CREDENTIAL

    private val biometricManager by lazy { BiometricManager.from(appContext) }

    private val _state = MutableStateFlow(value = isBiometricAuthAvailable())

    val state: Flow<Boolean> = _state

    private fun isBiometricAuthAvailable() =
        biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        // We need to check the authentication availability every time the app is activated.
        _state.value = isBiometricAuthAvailable()
    }

    fun launchBiometricPrompt(
        context: Context,
        title: String,
        subtitle: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onUnavailable: () -> Unit = {}
    ) {
        val activity = context.findFragmentActivity()
        if (!isBiometricAuthAvailable() || activity == null) {
            onUnavailable()
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(authenticators)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError()
                }
            })
        biometricPrompt.authenticate(promptInfo)
    }
}
