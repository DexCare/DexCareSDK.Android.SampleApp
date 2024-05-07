> **NOTE**
>
> The default app module supports SDK version 8.5.0.
> For samples and updates for 9.0.0, please refer to `app-compose` module. The compose UI based
> module
> will be default implementation module going forward.

# DexCareSDK.Android.SampleApp

An Android sample app using the DexCare SDK.

## Set up sample app

### Install dependency
DexCare Android SDK is hosted with Github Packages. So you need to be authenticated before you can
install the SDK dependency.

Set up these two environment variables in your computer to have access to the DexCare SDK downloads:

1. `DEXCARE_MAVEN_ACCOUNT` :  Your authorized GitHub username or email
2. `DEXCARE_MAVEN_TOKEN` : Your GitHub personal access token.  (tip: a personal access token (
   classic) with at least `read:packages` scope is required to install
   packages) . [Learn more](https://docs.github.com/en/packages/learn-github-packages/installing-a-package)

For info on how to set up environment variables on MacOS,
see [this article](https://medium.com/@himanshuagarwal1395/setting-up-environment-variables-in-macos-sierra-f5978369b255).
For Windows,
see [this article](https://www.architectryan.com/2018/08/31/how-to-change-environment-variables-on-windows-10/).

After these are set, restart Android Studio and do a Gradle sync. The DexCare SDK dependency should
then download successfully.


### Set up config
The sample app supports configuration to multiple environments from the single application. You can 
define the required configuration in a JSON file. Create a file named `dexcare_sdk_config.dexconfig`
in `res/raw` directory. 

Here is a sample for JSON that shows the supported fields;

```

[
  {
    "configId": "",
    "configName": "",
    "configDescription": "",
    "auth0ClientId": "",
    "auth0Domain": "",
    "brand": "",
    "tenant": "",
    "dexcareApiKey": "",
    "nationProviderId": "",
    "dexcareDomain": "",
    "fhirOrchUrl": "",
    "virtualVisitUrl": "",
    "virtualPracticeId": "",
    "stripePublishableKey": "",
    "pushNotificationIdentifier":""
  }
]

```

These JSON objects are mapped to list of `com.dexcare.sample.data.model.AppEnvironment` data class. 

Once the config is set up, run the build and you should be able to select environment on the first run.
Note: if there is only one environment configured in the JSON, the app will use the environment
by default.
