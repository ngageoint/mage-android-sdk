# Change Log
All notable changes to this project will be documented in this file.
Adheres to [Semantic Versioning](http://semver.org/).

## 5.3.1 (TBD)

##### Features
* Observation geometry support for lines and polygons

##### Bug Fixes

---
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
