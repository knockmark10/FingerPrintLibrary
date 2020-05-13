# Welcome to Jade!

Jade is a beautifull, lightweight library to authenticate you rapp with biometrics, using Google's latest BiometricPrompt to provide a consistent and unified experience across all Android devices.

# **1. Setup**

[![](https://jitpack.io/v/knockmark10/FingerPrintLibrary.svg)](https://jitpack.io/#knockmark10/FingerPrintLibrary)

This library is available _via_ [Jitpack](https://jitpack.io/#knockmark10/FingerPrintLibrary)

 1. Add it in your root build.gradle at the end of repositories:

```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

 2. Add the dependency:
```java
dependencies {
    implementation 'com.github.knockmark10:FingerPrintLibrary:$version'

}
```

# 2. How to use?

Use Jade's builder to customize your desired funcionality.

## Builder

|Method|Arguments|Description|
|:-:|:-:|:-:|
|setSecurityLevel|SecutiryLevel|Sets the security level for biometric authentication. Can be either **Strong** or **Weak**. This output can vary depending on the biometrics provided by vendors. **Weak** level may display facil or iris recognition, if supported by user's device.|
|setConfirmationRequired|Boolean|Flag to display a button after authentication succeeded to require explicit user action. |
|setTitle|String|Set title to authentication dialog.|
|setSubtitle|String|Set subtitle to authentication dialog.|
|setDescription|String|Set description to authentication dialog.|
|setNegativeText|String|Set text for button that will dismiss dialog.|
|build|Context|Builds manager to begin with authentication process.|

## Methods

Public methods available are listed below, with their respective description.

|Name|Arguments|Return Type|Description|
|:-:|:-:|:-:|:-:|
|validate|-|Unit|Validates if the device have the capabilities required by biometric authentication.|
|authenticate|FragmentActivity, Fragment|Unit|Show **BiometricPrompt** to authenticate the user.|
|encryptData|String, Cipher|ByteArrat|Encrypts user's data (such as passwords) to save it in preferences, to name an example. Returns the data after being processed by cipher.|
|decryptData|ByteArray|String|Decrypts provided data, and returns the decrypted data.|
|registerValidationListener|BiometricValidationCallback|Unit|Sets listener for validation output.|
|registerAuthListener|BiometricAuthenticationCallback|Unit|Sets listener for authentication output.|

## Interfaces

Public interfaces with their definitions.

### BiometricValidationCallback

|       Method      |    Arguments   | Description                                |
|:----:|:---------:|------------------------------------|
| onBiometricResult | BiometricState | Commuinicate the result of the validation. |

### BiometricAuthenticationCallback

|           Method          |               Arguments              |                        Description                        |
|:-------------------------:|:------------------------------------:|:---------------------------------------------------------:|
| onAuthenticationSucceeded | BiometricPrompt.AuthenticationResult | Communicates the result of the successful authentication. |
|   onAuthenticationFailed  |              AuthReason              |   Communicates the result of the failed authentication.   |

# 3. Usage

```kotlin
val jade = Jade.Builder  
			 //Set WEAK for facial recognition 
			 .setSecurityLevel(SecurityLevel.WEAK)
			 //Set STRONG for fingerprint  
			 .setSecurityLevel(SecurityLevel.STRONG)
			 .setTitle("Title")  
			 .setSubtitle("Subtitle")  
			 .setDescription("Description")  
			 .build(this)
			 
//Let's validate if user's device can use biometric authentication
jade.registerValidationListener(this)
jade.validate()

override fun onBiometricResult(state: BiometricState) {  
	  when (state) {  
		  BiometricState.BIOMETRIC_SUCCESS -> .. 
		  BiometricState.BIOMETRIC_ERROR_NO_HARDWARE -> .. 
		  BiometricState.BIOMETRIC_ERROR_HW_UNAVAILABLE -> ..
		  BiometricState.BIOMETRIC_ERROR_NONE_ENROLLED -> .. 
	 }
 }

//Show biometric prompt.
jade.authenticate(this)

override fun onAuthenticationFailed(reason: AuthReason) {  
	//Authentication failed. Reason object contains actual reason.
}  
  
override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { 
	//Authentication succeeded. Further operations here. 
}
```

# MIT License

```
The MIT License (MIT)

Copyright (c) 2020

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
