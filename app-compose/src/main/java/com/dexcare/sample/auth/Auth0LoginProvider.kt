package com.dexcare.sample.auth

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.lock.AuthenticationCallback
import com.auth0.android.lock.InitialScreen
import com.auth0.android.lock.Lock
import com.auth0.android.lock.UsernameStyle
import com.auth0.android.result.Credentials
import com.dexcare.sample.data.ErrorResult
import com.dexcare.sample.data.model.AppEnvironment
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Auth0LoginProvider @Inject constructor(@ActivityContext private val context: Context) :
    AuthProvider {

    override suspend fun login(appEnvironment: AppEnvironment): LoginResult {
        val auth0 = Auth0(
            appEnvironment.auth0ClientId,
            appEnvironment.auth0Domain
        )
        return suspendCoroutine {
            val builder = Lock.newBuilder(
                auth0,
                object : AuthenticationCallback() {
                    override fun onError(error: AuthenticationException) {
                        it.resume(
                            LoginResult.Error(
                                ErrorResult(
                                    title = "Login Error",
                                    message = error.message ?: "Failed to authenticate with Auth0."
                                )
                            )
                        )
                    }

                    override fun onAuthentication(credentials: Credentials) {
                        val accessToken: String = credentials.accessToken
                        val refreshToken: String = credentials.refreshToken.orEmpty()
                        Timber.d("accessToken:$accessToken refreshToken:$refreshToken")
                        it.resume(
                            LoginResult.Success(
                                accessToken = accessToken,
                                refreshToken = refreshToken,
                                expiresAt = credentials.expiresAt.toInstant()
                            )
                        )
                    }

                }
            ).apply {
                withAudience("https://${appEnvironment.auth0Domain}/api/v2/")
                withScope("openid offline_access")
                closable(true)
                useLabeledSubmitButton(true)
                loginAfterSignUp(true)
                withUsernameStyle(UsernameStyle.EMAIL)
                allowLogIn(true)
                allowSignUp(true)
                allowForgotPassword(true)
                initialScreen(InitialScreen.LOG_IN)
                allowedConnections(
                    listOf(
                        "Username-Password-Authentication",
                        "mfa-connection",
                        "with-strength"
                    )
                )
                hideMainScreenTitle(true)
            }

            context.startActivity(
                builder.build(context.applicationContext).newIntent(context.applicationContext)
            )
        }
    }

}
