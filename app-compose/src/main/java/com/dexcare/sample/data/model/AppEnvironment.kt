package com.dexcare.sample.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppEnvironment(
    val configId: String,
    val configName: String,
    val configDescription: String,
    val tenant: String,
    val brand: String,
    val auth0ClientId: String,
    val auth0Domain: String,
    val fhirOrchUrl: String,
    val virtualVisitUrl: String,
    /**
     * The NPIs used for Provider scheduling need to be retrieved from a source outside DexCare.
     * Ideally your health system would have a database of all of its Providers and could retrieve NPIs from that.
     * This value is defined here for a minimal use case of the SDK's ProviderService.
     * */
    val nationProviderId: String,
    /**This will vary per-environment, but can be hardcoded for each Virtual Practice you want to support
     * **/
    val virtualPracticeId: String,
    /**
     * Needs to be in the format: [stage|prod].FCM.[brand].HealthConnect
     * */
    val pushNotificationIdentifier: String,
    val stripePublishableKey: String,
)
