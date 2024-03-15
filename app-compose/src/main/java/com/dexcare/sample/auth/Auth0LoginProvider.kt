package com.dexcare.sample.auth

import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.lock.AuthenticationCallback
import com.auth0.android.lock.InitialScreen
import com.auth0.android.lock.Lock
import com.auth0.android.lock.UsernameStyle
import com.auth0.android.result.Credentials
import dagger.hilt.android.qualifiers.ActivityContext
import com.dexcare.acme.android.R
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Auth0LoginProvider @Inject constructor(@ActivityContext private val context: Context) :
    AuthProvider {

    override suspend fun login(): LoginResult {
        val auth0 = Auth0(
            context.getString(R.string.auth0_client_id),
            context.getString(R.string.auth0_domain)
        )
        return suspendCoroutine {
            val builder = Lock.newBuilder(
                auth0,
                object : AuthenticationCallback() {
                    override fun onError(error: AuthenticationException) {
                        it.resume(LoginResult.Error(message = error.message ?: "Login Error"))
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
                withAudience("https://${context.getString(R.string.auth0_domain)}/api/v2/")
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
