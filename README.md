# bluetoothscanning
Scanning nearby bluetooth devices

[![](https://jitpack.io/v/manishkummar21/bluetoothscanning.svg)](https://jitpack.io/#manishkummar21/bluetoothscanning)

This app helps us to find the nearby discovered device continously and it will listed.

**To do this follow the steps to initialize**

```
BluetoothConfig.with(this)
                .setBackgroundColor(Color.parseColor("#1E90FF"))
                .setPulseColor(Color.parseColor("#ffffff"))
                .setListener(this)
                .start();
```		


![Screenshot](scanning_image.png)


To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

        Add it in your root build.gradle at the end of repositories:

          allprojects {
            repositories {
              ...
              maven { url 'https://jitpack.io' }
            }
          }

Step 2. Add the dependency
        
        dependencies {
	        implementation 'com.github.manishkummar21:bluetoothscanning:Tag'
	      }
	      
Step 3. Enable the databinding in your project

       ```
       dataBinding {
        enabled = true
       }
       ```
	      





