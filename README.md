# DexCareSDK.Android.SampleApp
An Android sample app using the DexCare SDK.

To get started, open the config.xml files and replace the values with the configs provided to you by the DexCare team.

___

In addition, you will also need to set two environment variables in order to have access to the DexCare SDK downloads:

`DEXCARE_MAVEN_ACCOUNT` - Your authorized GitHub username or email

`DEXCARE_MAVEN_TOKEN` - Your GitHub personal access token or password

For info on how to set up environment variables on MacOS, see [this article](https://medium.com/@himanshuagarwal1395/setting-up-environment-variables-in-macos-sierra-f5978369b255).  For Windows, see [this article](https://www.architectryan.com/2018/08/31/how-to-change-environment-variables-on-windows-10/).

After these are set, restart Android Studio and do a Gradle sync.  The DexCare SDK dependency should then download successfully.
