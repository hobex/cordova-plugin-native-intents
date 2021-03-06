# cordova-plugin-native-intents

=========================================================

### This plugin is provided without guarantee or warranty!

# Overview
This Cordova plugin provides a general purpose layer for the Android intent mechanism, exposing a way to handle intents and setting their result.

## Credits
This project uses code released under the following MIT projects. Special thanks to their contributors.
- https://github.com/napolitano/cordova-plugin-intent (marked as no longer maintained)
- https://github.com/Initsogar/cordova-webintent.git (no longer available on github but the project is forked here: https://github.com/darryncampbell/cordova-webintent)
This project is also released under MIT.  Credit is given in the code where appropriate

## Installation

    cordova plugin add https://github.com/hobex/cordova-plugin-native-intents.git

  Intent-Filter has to be set in AndroidManifest.xml on desired Activity of main App:


```xml
<intent-filter>
  <action android:name="at.hobex.smart.MainActivity.TRANSACTION" />
  <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```

### Attention!

Apparently ``android:launchMode `` has to be set to ``singleTop``. Otherwise startActivityForResult() in triggering app gets resultCode 0 immediately! (At least on older API versions < 18)

**AndroidManifest.xml**

```xml
<activity android:launchMode="singleTop" android:name="MainActivity" ... >
```

 **config.xml**

 ```xml
<preference name="AndroidLaunchMode" value="singleTop"/>
```

## Supported Platforms
- Android

## Examples

### Handling Intents - onIntent()

Usually you would place this handler in deviceReady() listener in your app.js file to have a central entry point:

``` javascript
document.addEventListener('deviceReady', function() {
  // window.plugins.intent.onIntent(function (Intent) { ... }); ...
}
```

```javascript
window.plugins.intent.onIntent(function (intent) {
  // Success Callback for new Intent
  // ...
}, function () {
  // Error Callback
  console.log('Error');
});
```

### Setting Result - setResult()

```javascript
window.plugins.intent.setResult({
    // intent
    intent: 'at.hobex.smart.MainActivity.TRANSACTION'
    // result extra data
    successful: true
  },
  function(intent) {
    console.log("setResult() callback");
  }
);
```
