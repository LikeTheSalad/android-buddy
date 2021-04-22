
![version](https://img.shields.io/maven-central/v/com.likethesalad.android/android-buddy-plugin)

# Android Buddy

Table of Contents
=================

* [What it is](#what-it-is)
* [Why to use Android Buddy](#why-to-use-android-buddy)
* [Usage](#usage)
  * [Consumer usage](#consumer-usage)
    * [Using your own transformations](#using-your-own-transformations)
    * [Using Android Buddy libraries transformations](#using-android-buddy-libraries-transformations)
  * [Producer usage](#producer-usage)
    * [What's the Android Buddy library ID for?](#whats-the-android-buddy-library-id-for)
* [Adding it into your project](#adding-it-into-your-project)
  * [Changes into your root build gradle file](#changes-into-your-root-build-gradle-file)
  * [Setting up a consumer project](#setting-up-a-consumer-project)
  * [Setting up a producer project](#setting-up-a-producer-project)
* [License](#license)

What it is
---
Android Buddy is a plugin that allows transforming Android projects' classes using [Byte Buddy](https://bytebuddy.net/), at compile time. It supports both Java and Kotlin transformations.

Why to use Android Buddy
---
Usually, Byte Buddy takes care of transforming classes (also creating new ones) at runtime within standard Java environments, however, due to Android's custom environment, it is not possible to transform (redefine or rebase) existing classes during runtime. So essentially, if you want to **transform** your own classes using Byte Buddy for Android, that's when Android Buddy comes in handy as it makes it possible, at compile time. If you don't want to transform classes and rather just creating new ones using Byte Buddy, you should consider using the official Byte Buddy library for android: https://github.com/raphw/byte-buddy/tree/master/byte-buddy-android.

As an extra, Android Buddy allows you not only to create your own project's transformations, but also to **produce** transformations for other projects, e.g. create libraries that make use of Android Buddy. This is an example of an Android library that uses Android Buddy under the hood: [Aaper](https://github.com/LikeTheSalad/aaper)

Usage
--
As mentioned above, Android Buddy allows not only to create your own project's transformations, but also to produce transformations for other projects (create libraries). Depending on what it is that you need to do, for the former, you should take a look at `Consumer usage`, and for the latter (libraries), you should take a look at `Producer usage`.

### Consumer usage
This is for when you want to apply Byte Buddy transformations into your project's classes, either from Android Buddy libraries, or from your own local transformations. In order to use these transformations, you must first set up your consumer project by applying the `android-buddy` plugin to it on its `build.gradle` file, as explained below under `Setting up a consumer project`.

#### Using your own transformations
In order to use your own transformations you'd first have to create them by making a class that extends from `net.bytebuddy.build.Plugin`. Then, for it to be found later by Android Buddy, you'd have to annotate your class with `com.likethesalad.android.buddy.tools.Transformation`.

**Example**
```kotlin
@Transformation
class MyTransformation(private val logger: Logger/*Optional Logger, you can remove it if not needed*/) :
    Plugin {

    override fun apply(
        builder: DynamicType.Builder<*>,
        typeDescription: TypeDescription,
        classFileLocator: ClassFileLocator
    ): DynamicType.Builder<*> {
        logger.lifecycle("Transforming ${typeDescription.name} with MyTransformation")

        return builder.method(ElementMatchers.named("onCreate"))
            .intercept(MethodDelegation.to(MyOnCreateInterceptor::class.java))
    }

    override fun matches(target: TypeDescription): Boolean {
        return target.isAssignableTo(Activity::class.java)
    }

    override fun close() {
        // Nothing to close
    }
}
```

```kotlin
object MyOnCreateInterceptor {

    @JvmStatic
    fun intercept(@SuperCall originalMethodCall: Runnable) {
        originalMethodCall.run()
        Log.d("myTag", "Hello World!")
    }
}
```
In this example, we created a transformation that intercepts all of the project's classes that extend from `Activity` and then change their `onCreate` method in order to print an Android log after running the original code from the intercepted `onCreate` method.

As an optional operation, we're also printing a Gradle log before adding our interceptor to an Activity. The only type of argument that currently Android Buddy supports within a `Plugin` constructor is a Gradle logger (`org.gradle.api.logging.Logger`). It is optional, you can also have an empty constructor in the case that you don't want to print any logs during compile time.

Android Buddy only takes care of connecting Android compilation to Byte Buddy's API. You can lean more about all of the possible transformations that Byte Buddy allows by looking at its official documentation page: https://bytebuddy.net/#/tutorial

#### Using Android Buddy libraries transformations
You can use transformations provided by an Android Buddy library, here is an example of an Android Buddy library: [Aaper](https://github.com/LikeTheSalad/aaper).

In order to use them, by default you simply have to include those libraries into your Android Buddy-consumer project as any regular dependency, e.g:

```groovy
dependencies {
    implementation "the.android.buddy:library:x.y.z"
}
```
That's it by default, when you compile your project, Android Buddy will apply to it the exposed transformations from that library.

#### Configuration for consumer's dependencies
As mentioned above, by default your consumer project will take all the exposed transformations from any Android Buddy library it has, however, sometimes that won't be what you'd want for your project, and you'd rather prefer to explicitly select those libraries you'd like to get their transformations from, or even you'd rather to just ignore all dependencies' transformations altogether. For these mentioned cases, there are configuration parameters that you can change whenever you like to modify the default behavior.

The way you can choose what Android Buddy libraries will be used by your project is by setting up a libraries **scope**. This can be done within your project's `build.gradle` file under the `androidBuddy -> librariesPolicy` block like so:

```groovy
apply plugin: 'com.android.application' // OR 'com.android.library'
apply plugin: 'android-buddy'

///...

androidBuddy {
    librariesPolicy {
        scope {
            // Here you can define the scope of libraries you'd like to use.
            // Setting up a scope is optional as there is one already defined
            // by default (more details below).
            type = // The name of the scope.
            args = // (Optional) Arguments to be used along with the scope type.
		           // More details below.
        }
}
```

**Available scopes**
- **UseAll**: This is the default scope, if you don't define any scope within your build.gradle file then what will happen is that your project will use this scope which basically means that **any** Android Buddy library that your project has in it will be taken into account for Byte Buddy transformations on your project classes at compile time.
- **IgnoreAll**: If you set up this scope, it'd mean that all Android Buddy libraries will be ignored for the Byte Buddy transformation process. Only your local transformations (defined by yourself within your project) will make changes to your project's classes.
- **UseOnly**: This scope lets you choose which Android Buddy libraries you want your project to use for the Byte Buddy transformation process. You would have to provide a list of Android Buddy libraries IDs along with this scope, which will be the libraries that your project will use.

**Example of how to set up any scope:**

```groovy
// Your consumer's build.gradle file
apply plugin: 'com.android.application' // OR 'com.android.library'
apply plugin: 'android-buddy'

// ...
dependencies {
    // Some dependencies where there might be Android Buddy libraries
}
//...
androidBuddy {
    librariesPolicy {
        scope {
            type = "UseAll" // Default type
        }
        // Other options are:
        // scope {
        //     type = "IgnoreAll" - It won't use any library that has
        //                          Android Buddy transformations.
        // }
        // 
        // scope {
        //     type = "UseOnly" - It will only use Android Buddy 
        //                        transformations from the libraries specified
        //                        by their IDs in the "args" list.
        //     args = ["some-lib-id", "some.other.lib.id"] - These are the
        //.                              libraries which transformations
        //                               will be used on the consumer project.
        // }
    }
}
```

### Producer usage
You can make Android Buddy libraries, which will expose their Byte Buddy transformations for an Android Buddy consumer project. In order to do so, you'd have to create an [Android Library](https://developer.android.com/studio/projects/android-library) project and then apply the `android-buddy-library` plugin to it on its `build.gradle` file, as explained below under `Setting up a producer project`.

After setting up your Android Library as an Android Buddy producer, you can start creating as many Byte Buddy Plugin classes (`net.bytebuddy.build.Plugin`) as you like, and then you'd have to explicitly define the ones you'd like to expose for consumers in your library's `build.gradle` file, e.g:

**Example**
```kotlin
package com.my.transformation.package
// ...

class MyExposedTransformation : Plugin {
    override fun apply(
        builder: DynamicType.Builder<*>,
        typeDescription: TypeDescription,
        classFileLocator: ClassFileLocator
    ): DynamicType.Builder<*> {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun matches(target: TypeDescription): Boolean {
        TODO("Not yet implemented")
    }
}
```
And then, for you to expose your transformation to consumers, you have to add its full name into the exposed transformations name config parameter on your library's `build.gradle` file, like so:

```groovy
apply plugin: 'com.android.library'
apply plugin: 'android-buddy-library'

// ...
androidBuddyLibrary {
    id = "my-library-id" // It is mandatory to set a unique ID 
    // for your library (details below). This will help consumers
    // to select it for using it with the "UseOnly" scope (explained above 
    // under "Configuration for consumer's dependencies").
    exposedTransformationNames = ["com.my.transformation.package.MyExposedTransformation"]
}
```
As you might have noticed, you must also set a **unique ID** for your library. This could be used further by consumers that would want to explicitly select your library to run.

The that ID you choose for your Android Buddy library must meet the following criteria:
- Only lowercase letters, numbers, `.` and `-` are allowed
- It must start with a letter.
- It cannot end neither with `-` nor with `.`
- There cannot be a consecutive `-` or `.` after a `-` and/or `.`

>#### What's the Android Buddy library ID for?
>The Android Buddy library ID is a unique identifier for Android Buddy libraries (producers), which provides a way to consumers of such libraries to select (if they want to, since by default all Android Buddy libraries are enabled) which libraries to allow making transformations to the consumer's classes. In other words, a consumer can select which libraries to "enable" using their IDs. The way it works is by setting up in the consumer's project a scope of type `UseOnly` along with the list of Android Buddy libraries IDs that said consumer wants to enable. More details above under `Configuration for consumer’s dependencies`.

And that's it, when you add this Android Library as dependency for an Android Buddy consumer project, your library's transformation `MyExposedTransformation` will be available right away for the consumer to use.

For reference purposes, you can take a look at this Android Buddy library: [Aaper](https://github.com/LikeTheSalad/aaper).

Adding it into your project
---
Whether you're planning to set up a producer or consumer project, or both, you'd first have to add Android Buddy's Gradle plugin into your `root build.gradle` file, and then you can proceed to set up your producers and/or consumers.

### Changes into your root build gradle file
As a first step for both producers and consumers, you'd have to add Android Buddy as a Gradle plugin of your Android project by adding the following line into your `root` `build.gradle`'s buildscript' dependencies:

```groovy
classpath "com.likethesalad.android:android-buddy-plugin:1.0.3"
```

**Example**
```groovy
// root build.gradle file
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.android.tools.build:gradle:3.5.+' // Requires Android build plugin version 3.5.4 or higher.
    classpath "com.likethesalad.android:android-buddy-plugin:1.0.3"
  }
}
```

### Setting up a consumer project
After adding the required changes into your `root` `build.gradle` file [as explained above](#changes-into-your-root-build-gradle-file), then you can go into your project's `build.gradle` file, for example `app/build.gradle`, and then add the following to it:

```groovy
// Your consumer's build.gradle file
apply plugin: 'com.android.application' // OR 'com.android.library'
apply plugin: 'android-buddy'
```
Both, Android applications and Android libraries can be Android Buddy consumers.

And that's it, after adding `android-buddy` as a plugin for your project, you can now make it consume Byte Buddy transformations as explained above under `Consumer usage`.

### Setting up a producer project
After adding the required changes into your `root` `build.gradle` file [as explained above](#changes-into-your-root-build-gradle-file), then you can go into your project's `build.gradle` file, for example `myLibrary/build.gradle`, and then add the following to it:

```groovy
// Your producer's build.gradle file
apply plugin: 'com.android.library'
apply plugin: 'android-buddy-library'
```
Only Android libraries can be Android Buddy producers.

And that's it, after adding `android-buddy-library` as a plugin for your project, you can now make it expose Byte Buddy transformations as explained above under `Producer usage`.

License
---
    MIT License

    Copyright (c) 2020 LikeTheSalad.

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
