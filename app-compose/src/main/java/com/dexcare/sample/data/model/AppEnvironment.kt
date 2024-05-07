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
    val nationProviderId: String,
    val virtualPracticeId: String,
    val pushNotificationIdentifier: String,
    val stripePublishableKey: String,
)
