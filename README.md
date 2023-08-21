# RunTrack

RunTrack is a Fitness Tracking app utilizing modern Android technologies, including
Jetpack Compose, MVVM architecture, and Google Maps API. The app allows users to
track their running activities, displaying real-time routes on an interactive map
while storing essential statistics using Room database.

## Features
1. Live tracking of running activity using GPS.
2. Tracking of user's running path in Map using Google Map Compose library.
3. Using Foreground Service, even the user closed the app and remove
   from the background, this app stills continue to track user running stats.
4. Room database to store and manage running statistics.
5. Handling nested navigation, Deep linking, conditional navigation to on
   boarding screen using Jetpack Navigation Component.
6. New Jetpack Compose image picker - helps to pick image
   without any permission.
7. Paging3 integration.
8. Dynamic color support in dark and light theme.

## Screenshot

|                                                                                                                          |                                                                                                                           |
|--------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| ![runtrack_home_ss](https://github.com/sDevPrem/run-track/assets/130966261/1e7828dc-555f-49a6-92aa-c7caea944cb2)         | ![runtrack_live_tracking_ss](https://github.com/sDevPrem/run-track/assets/130966261/4cd52d04-7745-484b-b5be-f6b2c0c72f71) |
| ![runtrack_running_info_ss](https://github.com/sDevPrem/run-track/assets/130966261/b684d4a5-e443-4413-a832-0f63992fe89c) | ![runtrack_profile_ss](https://github.com/sDevPrem/run-track/assets/130966261/bfa0db6b-7ad6-4b22-abcf-d6f6393178f1)       |

## Package Structure

* `core`
    * `data`: Contains entity and database related classes.
    * `tracking`: Classes that handles tracking.
* `di` : Hilt Modules.
* `domain`: Common use cases.
* `ui`
    * `nav`: Contains app navigation and destinations.
    * `screen`: Contains UI.
    * `theme`: Material3 theme.
    * `utils`: UI utility classes and common components.
* `utils`: Utility class used across the app.

## Build With

[Kotlin](https://kotlinlang.org/):
As the programming language.

[Jetpack Compose](https://developer.android.com/jetpack/compose) :
To build UI.

[Jetpack Navigation](https://developer.android.com/jetpack/compose/navigation) :
For navigation between screens and deep linking.

[Room](https://developer.android.com/jetpack/androidx/releases/room) :
To store and manage running statistics.

[Google Maps API](https://developers.google.com/maps/documentation/android-sdk) :
To track user's running activity such as speed, distance and path on the map.

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android) :
For injecting dependencies.

[Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore) :
To store user related data.

[Coil](https://coil-kt.github.io/coil/compose/) :
To load image asynchronously.

## Architecture

This app follows MVVM architecture, Uni Directional Flow (UDF) pattern and Single architecture
pattern.
HLD of tracking architecture is shown in the below image:
![tracking_architecture](https://github.com/sDevPrem/run-track/assets/130966261/932e9df7-cf34-4902-aa84-73a6431ca236)

## Installation

Simple clone this app and open in Android Studio.

### Google Map Integration

Do these steps if you want to show google maps. The tracking
functionalities will work as usual even if you don't do
these step.

1. Create and Get Google Maps API key by using this official
   [guide](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
2. Open `local.properties` file.
3. Enter your API key like this:

```
MAPS_API_KEY=your_maps_api_key
```

## Project Status

These features are left to be implemented:

1. Show user running statistics on a chart.
2. Profile menu implementation.
3. Unit Tests
