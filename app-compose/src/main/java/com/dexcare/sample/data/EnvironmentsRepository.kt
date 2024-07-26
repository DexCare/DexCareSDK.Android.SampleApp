package com.dexcare.sample.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dexcare.sample.data.model.AppEnvironment
import com.stripe.android.PaymentConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.dexcare.DexCareSDK
import org.dexcare.Environment
import timber.log.Timber

class EnvironmentsRepository(
    private val context: Context,
    private val jsonParser: Json,
    private val sharedPreferences: SharedPreferences,
) {
    private val errorResult = ErrorResult(
        title = "Configuration error",
        message = "Please make sure you have a valid JSON in a file named 'dexcare_sdk.dexconfig' in 'raw' resource directory."
    )

    private val results =
        MutableStateFlow<ResultState<List<AppEnvironment>>>(ResultState.UnInitialized)

    private var selectedEnvironment: AppEnvironment? = null

    @SuppressLint("DiscouragedApi")
    fun loadConfigFile() {
        CoroutineScope(Dispatchers.IO).launch {
            val configFileResId =
                context.resources.getIdentifier(CONFIG_FILE, "raw", context.packageName)
            if (configFileResId == 0) {
                Timber.w("No config file found for DexCare SDK.")
                results.value = ResultState.Error(errorResult)
            } else {
                val configRawJson = context.resources.openRawResource(configFileResId)
                    .bufferedReader().use { it.readText() }

                Timber.d("Config environment output:$configRawJson")

                val environments = try {
                    jsonParser.decodeFromString<List<AppEnvironment>>(configRawJson)
                } catch (ex: SerializationException) {
                    Timber.e(ex)
                    null
                } catch (ex: IllegalArgumentException) {
                    Timber.e(ex)
                    null
                }

                if (environments.isNullOrEmpty()) {
                    results.value = ResultState.Error(errorResult)
                } else {
                    results.value = ResultState.Complete(environments)
                }
            }
        }
    }

    fun getAllEnvironments(): MutableStateFlow<ResultState<List<AppEnvironment>>> = results

    /**
     * Updates the environment for DexCare SDK
     * and also saves the selected environment for next run.
     * */
    fun selectEnvironment(environment: AppEnvironment) {
        DexCareSDK.init(context,
            object : Environment {
                override val isProd: Boolean = false
                override val fhirOrchUrl: String = environment.fhirOrchUrl
                override val virtualVisitUrl: String = environment.virtualVisitUrl
            }
        )

        val environmentString = jsonParser.encodeToString(environment)
        sharedPreferences.edit {
            putString(KEY_ENVIRONMENT, environmentString)
        }
        PaymentConfiguration.init(context, environment.stripePublishableKey)
    }

    fun findSelectedEnvironment(): AppEnvironment? {
        if (selectedEnvironment != null) {
            return selectedEnvironment
        }
        val rawString = sharedPreferences.getString(KEY_ENVIRONMENT, "").orEmpty()
        selectedEnvironment = try {
            jsonParser.decodeFromString<AppEnvironment>(rawString)
        } catch (ex: SerializationException) {
            null
        } catch (ex: IllegalArgumentException) {
            null
        }
        return selectedEnvironment
    }

    fun clearEnvironment() {
        selectedEnvironment = null
        sharedPreferences.edit {
            remove(KEY_ENVIRONMENT)
        }
    }

    companion object {
        private const val CONFIG_FILE = "dexcare_sdk"
        private const val KEY_ENVIRONMENT = "selected_app_environment"
    }

}
