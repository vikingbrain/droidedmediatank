# Droided Media Tank for Android [![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.vikingbrain.dmt) [<img src="http://static.vikingbrain.com/dmt/images/apk.png" height="45px" />](http://static.vikingbrain.com/dmt/releases/)
[![PayPal donate button](http://img.shields.io/paypal/donate.png?color=blue)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=9UAHFFF7B2BLG "Donate once-off to this project using Paypal")  
[![Bitcoin donation](http://static.vikingbrain.com/images/bitcoin-small.png)](https://blockchain.info/address/13t5W36tcrirhwwMG8WkDDc5aa89acmJe5 "Bitcoin donation") BTC: 13t5W36tcrirhwwMG8WkDDc5aa89acmJe5  
[![Ethereum donation](http://static.vikingbrain.com/images/ethereum-small.png)](https://www.etherchain.org/account/0x7fC401f9ca6c707177Bc082d193A1C7120659C51 "Ethereum donation") ETH: 0x7fC401f9ca6c707177Bc082d193A1C7120659C51

Droided Media Tank is an Android application that allows remote control and management of Networked Media Tank media streamers. Designed for Popcorn Hour A100, A110, A200, A210, A300, A400, B110, C200, C300, PopBox 3D, PopBox V8, eGreat, Digitek HDX, Kaiboer and others based in NMT system.

![Dashboard](http://static.vikingbrain.com/dmt/screenshots/screenshot_2.10_1.png)
![Remote control](http://static.vikingbrain.com/dmt/screenshots/screenshot_2.9_6.png)
![File browser](http://static.vikingbrain.com/dmt/screenshots/screenshot_2.9_4.png)
![NMT services](http://static.vikingbrain.com/dmt/screenshots/screenshot_2.9_8.png)

## Requirements

Java 1.6+, Android 2.2.X+

## Building and installing

The build requires [Android Studio and SDK Tools](http://developer.android.com/studio)
to be installed in your development environment.

In addition you'll need to create a file `local.properties` at project root indicating your local Android SDK home path:

```bash
sdk.dir=/Users/Rafa/dev/android-sdk-macosx
```

## Compiling

* Run `./gradlew build` from the `root` directory to build the APK

## Problems?

If you find any issues please [report them](https://github.com/vikingbrain/droidedmediatank/issues) or better,
send a [pull request](https://github.com/vikingbrain/droidedmediatank/pulls).

## Author:
* Rafael Iñigo (vikingBrain)

## Libraries and resources used in the project

* [TheDavidBox client for Java](https://www.github.com/vikingbrain/thedavidbox-client4j), a Java HTTP client for consuming TheDavidBox Service API of Networked Media Tank devices.
    Apache License, Version 2.0
* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock), an extension of the support library designed to facilitate the use of the action bar design pattern across all versions of Android with a single API.
    Apache License 2.0
* [OI File Manager](https://github.com/openintents/filemanager), an Android file manager.
    Apache License, Version 2.0
* [EdtFTPj/Free](http://enterprisedt.com/products/edtftpj/), an open source FTP client library for use in Java applications licensed under the LGPL.
* [AChartEngine](http://www.achartengine.org), a charting software library for Android applications.
    Apache License 2.0
* [c200remote project](http://code.google.com/p/c200remote/), web remote control for Popcorn Hour A/C-200 released under the New BSD License, original images has been made by SmashD.

## License

Copyright (c) 2014 Rafael Iñigo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Other licenses reference:

This application reuses sources from the [c200remote project](http://code.google.com/p/c200remote/) released under the New BSD License:

Copyright (c) 2009,  Daniel Cervera, dc11ab+c200remote@gmail.com
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## Disclaimer third parties

All product and company names are trademarks™ or registered® trademarks of their respective holders. Use of them does not imply any affiliation with or endorsement by them.
All specifications are subject to change without notice.
Popcorn Hour, Popbox, Networked Media Tank and SayaTv are trademarks of Syabas Technology Inc., Registered in the U.S.
