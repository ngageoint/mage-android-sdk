 Change Log
All notable changes to this project will be documented in this file.
Adheres to [Semantic Versioning](http://semver.org/).

---
## 6.2.6 (TBD)

##### Features

##### Bug Fixes

## [6.2.5](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.2.5)

##### Features
* Update from support library to androidx
* geopackage-android version 3.4.0
* Add ability to fetch imagery layers 
* Modified fetching of static features into 2 steps (fetch, then load)

##### Bug Fixes
* Media Utility handling of additional types of download document URIs

## [6.2.4](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.2.4) (08-15-2019)

##### Features

##### Bug Fixes
* Update OkHttp3 manager to handle gzip transparently.
* Determine audio file extension based on file provided by audio recorder, not by ContentResolver's content type as audio/mp4 is not a content type
  available in MimeTypeMap which was causing crashes on some samsung devices.
* Account for zip path traversal vulnerability.

## [6.2.3](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.2.3) (06-06-2019)

##### Features

##### Bug Fixes
* Don't throw exception if we cannot pull recent events form the database based on id. Log the problem and return an empty list.

## [6.2.2](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.2.2) (01-22-2019)

##### Features
* Added 5 and 30 second user location pull frequencies.

##### Bug Fixes

## [6.2.1](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.2.1) (10-22-2018)

##### Features
* Remove some logging

##### Bug Fixes

## [6.2.0](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.2.0) (09-18-2018)

##### Features
* Login api returns response object so that application client can check server response in error case.
* Login api separates authentication from authorization.

##### Bug Fixes
* Logout api now explicitly passes token in header.  Http interceptor was unreliable as the token may have been removed before Authorization header was added.

## [6.1.1](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.1.1) (06-19-2018)

##### Features
* Beta GeoPackage download feature.

##### Bug Fixes
* Fixed a bug where user create error stream was parsed twice, resulting in an invalid error be reported to client.

## [6.1.0](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.1.0) (04-16-2018)

##### Features
* New task to pull teams, users and icons for a specific event.
* Store login/auth strategies coming from server in shared preferences.

##### Bug Fixes

## [6.0.3](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.0.3) (02-14-2018)

##### Features
* Added api method to get non archived forms.

##### Bug Fixes
* Add appVersion and user-agent on device registration request

## [6.0.2](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.0.2) (01-25-2017)

##### Features

##### Bug Fixes
* Query for events that user is a part of.  This will differentiate between read only and write events.
* Move event fetch back into intial fetch and perform before we show events

## [6.0.1](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.0.1) (01-23-2017)

##### Features

##### Bug Fixes
* Query for all events when querying for current users events.  This is ok because the server will only ever return the events that
a given user can see.

## [6.0.0](https://github.com/ngageoint/mage-android-sdk/releases/tag/6.0.0) (01-15-2017)

##### Features
* Observation geometry support for lines and polygons
* Support for multiple forms per event
* Event and Team Access control list support
* Delete observation api

##### Bug Fixes

## [5.3.2](https://github.com/ngageoint/mage-android-sdk/releases/tag/5.3.2) (10-05-2017)

##### Features

##### Bug Fixes
* Parse mulitple select choices with spaces correctly

## [5.3.1](https://github.com/ngageoint/mage-android-sdk/releases/tag/5.3.1) (09-01-2017)

##### Features
* Allow description to be null in teams and events

##### Bug Fixes

## [5.3.0](https://github.com/ngageoint/mage-android-sdk/releases/tag/5.3.0) (03-28-2017)

##### Features
* Updates preferences to appcompat to support material design
* Remove current event preference key.  Clients should Use current event in stored in database
* Added method in EventHelper to remove current event
* Added method in EventHelper to get most recent event for a user
* Consolidate OAuth and FormAuth usernames
* Added refresh intent services for observations and locations.
* DateFormatFactory changed to ISO8601DateFormatFactory

##### Bug Fixes
* Only send token expired notification once to listeners
* Fix a bug in the user deserializer when deserializing a single user

## [5.2.0](https://github.com/ngageoint/mage-android-sdk/releases/tag/5.2.0) (11-04-2016)

##### Features
* Added favorite and important to the observation model and services
* Optimize static layer delete by batch deleting.

##### Bug Fixes
* Delete icon file if static feature icon fails to download

## [5.1.1](https://github.com/ngageoint/mage-android-sdk/releases/tag/5.1.1) (11-10-2016)

##### Features

##### Bug Fixes
* Store media picked from gallery with correct extension.

## [5.1.0](https://github.com/ngageoint/mage-android-sdk/releases/tag/5.1.0) (08-11-2016)

##### Features
* Multi select support.

##### Bug Fixes
