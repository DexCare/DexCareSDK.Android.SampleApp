package com.dexcare.sample.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppEnvironment(
    /**
     * Unique id to identify each config.
     * */
    val configId: String,
    /**
     * Human friendly name for the config, so that
     * it's easy to select between different options.
     * */
    val configName: String,
    /**
     * Short description about what this config supports.
     * e.g if some integrations are not available, it's useful to mention here.
     * */
    val configDescription: String,
    /**
     * tenant name as provided by DexCare.
     * */
    val tenant: String,
    /**
     * This can be used with the SDK to identify different brands for your application.
     * */
    val brand: String,

    /**
     * Client Id for auth0 application.
     * */
    val auth0ClientId: String,
    /**
     * Domain for auth0 application.
     * */
    val auth0Domain: String,
    /**
     * Base url as provided by DexCare for FHIR ORCH services.
     * */
    val fhirOrchUrl: String,
    /**
     * Base url as provided by DexCare for virtual services.
     * */
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
    /**
     * Stripe key to collect payment information.
     * This app supports Stripe SDK integration.
     * */
    val stripePublishableKey: String,
)
