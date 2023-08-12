# RunTrack

RunTrack is a Fitness Tracking app utilizing modern Android technologies, including
Jetpack Compose, MVVM architecture, and Google Maps API. The app allows users to
track their running activities, displaying real-time routes on an interactive map
while storing essential statistics using Room database.

## Feature

1. Live tracking of running activity using GPS.
2. Tracking of user's running path in Map using Google Map Compose library.
3. Using Foreground Service, even the user closed the app and remove
   from the background, this app stills continue to track user running stats.
4. Room database to store and manage running statistics.
5. Deep linking using jetpack navigation component.
6. Handling conditional navigation to on boarding screen.
7. New Jetpack Compose image picker - helps to pick image
   without any permission.

## Screenshot

|                                         |                                          |
|-----------------------------------------|------------------------------------------|
| ![](image/runtrack_home_ss.png)         | ![](image/runtrack_live_tracking_ss.png) |
| ![](image/runtrack_running_info_ss.png) | ![](image/runtrack_profile_ss.png)       |

## Package Structure

- `core`: Handles the backend of the app like tracking and database related work.
- `domain`: Contains common use cases.
- `ui`: Handles the user interface components including navigation.
- `utils`: Includes utility classes and helper functions used across the app.
- `di` : Contains hilt modules.

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
![](image/tracking_architecture.png)

## Project Status

These features are left to be implemented:

1. Show user running statistics on a chart.
2. Unit Tests
