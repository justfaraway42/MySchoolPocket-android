MySchoolPocket Android App
==========================

## Summary and Screenshots

This repository contains the source code for the MySchoolPocket Android app. An app with which you can display news, timetables and representation plans from a school and save them offline. Also you can easily call, mail and navigate to the school. This app is localized in English, German and Simplified Chinese.

![MySchoolPocket_news](https://raw.githubusercontent.com/justfaraway42/MySchoolPocket-android/master/screenshots/1.png)
![MySchoolPocket_timetables](https://raw.githubusercontent.com/justfaraway42/MySchoolPocket-android/master/screenshots/2.png)
![MySchoolPocket_representation](https://raw.githubusercontent.com/justfaraway42/MySchoolPocket-android/master/screenshots/3.png)
![MySchoolPocket_info](https://raw.githubusercontent.com/justfaraway42/MySchoolPocket-android/master/screenshots/4.png)

## License

This app is released under the MIT license, with all related content.

## Working Example App
[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=de.justproductions.egw)

## Building
The build requires [Android Studio](http://tools.android.com/recent) and the [Android SDK](http://developer.android.com/sdk/index.html) to be installed in your development environment.

## What you have to change
### App-Icon and Start-Image
Change this images with an image or logo of your School.

### data.xml
This file contains all necessary information about the school(s). At the first start, the user can choose a school and a class (from 5-12 - you can change the `classes_values` in the _arrays.xml_) - this writes the Information about the chosen school in SharedPreferences.

The structure of this _data.xml_ is completely identical with that of the iOS App.

#### Replace * in
`<school name="*">` with the name of the School

`<feed><![CDATA[*]]></feed>` with the URL to the News-RSS-Feed

`<timetables_names><![CDATA[*;**;***;…]]></timetables_names>` with the names of the Timetables, separated with a semicolon

`<timetables_classes><![CDATA[*;**;***;…]]></timetables_classes>` with the classes from 5-12 where the timetable belong to (same order)

`<timetables_urls><![CDATA[*;**;***;…]]></timetables_urls>` with the URLs to the Timetable-PDFs (same order)

`<representation_urls><![CDATA[*;**;***;****;*****]]></representation_urls>` with the URLs to the Representation-PDFs (from Monday to Friday)

`<phone_number><![CDATA[*]]></phone_number>`, `<website><![CDATA[*]]></website>`, `<email_address><![CDATA[*]]></email_address>`, `<address><![CDATA[*]]></address>` with the Phone-Number, the URL to the website, the Mail-Address and the Adress of the School
                

#### Schools in other communities and/or states?
If you have schools in other communities and/or states, you have to write the name(s) of them in the `<state name="">` and `<community name="">` tags.
Also, you have to **comment one** and to **uncomment two** blocks of code in the _SchoolsActivity.java_ file.

## Make an iOS-Counterpart of the School App
You can easily make an iOS-Counterpart - the _data.xml_ is completely identical.

![MySchoolWallpaper](https://raw.githubusercontent.com/justfaraway42/MySchoolPocket-android/master/MSP_wallpaper.png)
