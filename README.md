### android-super-bar

A full configurable value bar with an optional overlay bar and control widget slider to adjust the value.

### Installation

[![Download](https://api.bintray.com/packages/mrkcsc/maven/com.miguelgaeta.super-bar/images/download.svg)](https://bintray.com/mrkcsc/maven/com.miguelgaeta.super-bar/_latestVersion)

```groovy

compile 'com.miguelgaeta.android-super-bar:super-bar:1.3.4'

```

### Usage

```java

<com.miguelgaeta.super_bar.SuperBar
    android:id="@+id/super_bar"
    android:layout_width="320dp"
    android:layout_height="26dp"
    android:layout_centerInParent="true"
    android:layout_gravity="center"
    android:background="#ddd"
    app:sb_barBackgroundColor="#666"
    app:sb_barColor="#111"
    app:sb_barControlColor="#fff"
    app:sb_barControlRadius="8dp"
    app:sb_barControlShadowRadius="2dp"
    app:sb_barHeight="5dp"
    app:sb_barInterval="1"
    app:sb_barControlShadowColor="#000"
    app:sb_barOverlayColor="#333"
    app:sb_barOverlayValue="90"
    app:sb_barTouchEnabled="true"
    app:sb_barValue="60"
    app:sb_barValueMax="100"
    app:sb_barValueMin="0"/>
```

### License

*Copyright 2015 Miguel Gaeta*

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
