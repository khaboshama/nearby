## NearBy App
A mobile app for displaying realtime nearby places around you.
App uses Foursquare API to display information about nearby places around user using user’s current location specified by Latitude and Longitude. App has two operational modes, “Realtime" and “Single Update”. “Realtime” allows app to always display to the user the current near by places based on his location, data should be seamlessly updated if user
moved by 500 m from the location of last retrieved places. “Single update” mode means the app is updated once in app is launched and doesn’t update again. User should be able to switch between the two modes, default mode is “Realtime”. App should remember user choices for next launches.


## Guide to app architecture
<br>
<p align="center">
  <img src="https://user-images.githubusercontent.com/5102649/107129642-78415a80-68cf-11eb-83de-fee045b4d7bc.PNG">
</p>
<br>

## The app has following packages:
1. **constant**: It contains the constants class of the application.
2. **model**: It contains the database, network, repository, response and shared preference classes of the application.
3. **ui**: It contains the packages of each screen and each package contains View, viewModel and adapter of the screen.
4. **utils**: It contains the utils classes of the application.



## Library reference resources:
1. Coroutines: https://codelabs.developers.google.com/codelabs/kotlin-coroutines/
2. Retrofit: https://square.github.io/retrofit/
3. DataBinding: https://developer.android.com/topic/libraries/data-binding

## License
```
   Copyright (C) 2019 khaled Aboshama

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
