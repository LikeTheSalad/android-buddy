# Android Buddy

What it is
---
Android Buddy is a plugin that allows transforming Android projects' classes using [Byte Buddy](https://bytebuddy.net/), at compile time.

Why to use Android Buddy
---
Usually, Byte Buddy takes care of transforming classes (also creating new ones) at runtime within standard Java environments, however, due to Android's custom environment, it is not possible to transform (redefine or rebase) existing classes during runtime. So essentially, if you want to **transform** your own classes using Byte Buddy for Android, that's when Android Buddy comes in handy as it makes it possible, at compile time. If you don't want to transform classes and rather just creating new ones using Byte Buddy, you should consider using the official Byte Buddy library for android: https://github.com/raphw/byte-buddy/tree/master/byte-buddy-android.

As an extra, Android Buddy allows you not only to create your own project's transformations, but also to **produce** transformations for other projects, e.g. create libraries that make use of Android Buddy. This is an example of an Android Buddy library: [INSERT LINK]

Usage
--


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