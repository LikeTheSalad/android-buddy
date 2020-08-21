# Android Buddy

What it is
---
Android Buddy is a plugin that allows transforming Android projects' classes using [Byte Buddy](https://bytebuddy.net/), at compile time. It supports both Java and Kotlin transformations.

Why to use Android Buddy
---
Usually, Byte Buddy takes care of transforming classes (also creating new ones) at runtime within standard Java environments, however, due to Android's custom environment, it is not possible to transform (redefine or rebase) existing classes during runtime. So essentially, if you want to **transform** your own classes using Byte Buddy for Android, that's when Android Buddy comes in handy as it makes it possible, at compile time. If you don't want to transform classes and rather just creating new ones using Byte Buddy, you should consider using the official Byte Buddy library for android: https://github.com/raphw/byte-buddy/tree/master/byte-buddy-android.

As an extra, Android Buddy allows you not only to create your own project's transformations, but also to **produce** transformations for other projects, e.g. create libraries that make use of Android Buddy. This is an example of an Android library that uses Android Buddy under the hood: [INSERT LINK]

Usage
--
As mentioned above, Android Buddy allows not only to create your own project's transformations, but also to produce transformations for other projects (create libraries). Depending on what it is that you need to do, for the former, you should take a look at `Consumer usage`, and for the latter (libraries), you should take a look at `Producer usage`.

### Consumer usage
This is for when you want to apply Byte Buddy transformations into your project's classes, either from Android Buddy libraries, or from your own local transformations.

#### Using your own transformations
In order to use your own transformations you'd first have to create them by creating a class that extends from `net.bytebuddy.build.Plugin` and then, for it to be found later by Android Buddy, you'd have to annotate your class with `com.likethesalad.android.buddy.tools.Transformation`.

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
        Log.d("AndroidBuddy", "Hello World!")
    }
}
```
In this example, we created a transformation that intercepts all of the project's classes that extend from `Activity` and then change their `onCreate` method in order to print an Android log after running the original code from the intercepted `onCreate` method.

As an optional operation, we're also printing a Gradle log before adding our interceptor to an Activity. The only type of argument that currently Android Buddy supports within a `Plugin` constructor is a Gradle logger (`org.gradle.api.logging.Logger`). It is optional, you can also have an empty constructor in the case that you don't want to print any logs during compile time.

Android Buddy only takes care here of connecting Android compilation to Byte Buddy's API. You can lean more about all of the possible transformations that Byte Buddy allows by looking at its official documentation page: https://bytebuddy.net/#/tutorial

#### Using Android Buddy library transformations
You can use transformations provided by an Android Buddy library, here is an example of an Android Buddy library: [INSERT LINK].

In order to use them, by default you simply have to include this library into your AndroidBuddy-consumer project as any regular dependency, e.g:

```groovy
dependencies {
    implementation "the.android.buddy:library:x.y.z"
}
```
That's it by default, when you compile your project, Android Buddy will apply the exposed transformations of that library.

#### Configuration for consumer's dependencies
As mentioned above, by default your consumer project will take all the exposed transformations from any Android Buddy dependency it has, however, sometimes that won't be what you'd want for your project, and you'd rather prefer to explicitly select those libraries you'd like to get their transformations from, or even you'd rather to just ignore all dependencies' transformations altogether. For these mentioned cases, there are configuration parameters that you can change whenever you like to modify the default behavior.

You can do the following in order to configure the way your consumer project uses transformations from its dependencies:

```groovy
// Your consumer's build.gradle file
dependencies {
    // Some dependencies where there might be AndroidBuddy libraries
}
//...
androidBuddy {
    dependenciesConfig {
        disableAllTransformations = false /* If TRUE, your consumer will ignore all dependencies'
        transformations. Default is FALSE.*/
        alwaysLogTransformationNames = true /* If TRUE, it will always log the names of the
        dependencies' transformations being applied into your project at compile time.
        If false, it will show them only if you compile your project using Gradle's '--debug' flag.
        Default is TRUE.*/
        strictMode {
            enabled = false /* If FALSE, it will take into account all AndroidBuddy libraries added
            into your consumer project. If true, it will ONLY take into account the transformations from
            AndroidBuddy libraries that are defined explicitly using the configuration
            `androidBuddyImplementation` and/or `androidBuddyApi`. e.g:
            dependencies {
                // With strictMode enabled:
                androidBuddyImplementation "the.android.buddy:library:x.y.z"
            }
            With strict mode enabled, any AndroidBuddy dependency that is not defined with such
            configuration will be ignored.

            strictMode.enabled is FALSE by default.
            */
        }
    }
}
```

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
    SOFTWARE