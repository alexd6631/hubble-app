# HubbleApp #

This is a simple Kotlin Multiplatform project, to showcase the usage of Kotlin
Flow and Reactive UI (SwiftUI & Jetpack Compose) with a touch of clean
architecture.

The app was developed for a presentation organized by GDG Toulouse
https://www.meetup.com/fr-FR/GDG-Toulouse/events/267133106/

Slides are available in this repository.

## Project structure ##

  * mvvm : A multiplatform port of KLiveData 
  * hubblesite-client: A Ktor client for Hubblesite API
  * app: The multiplatform app, with SwiftUI and Jetpack Compose UI
  
## Application architecture ##

The app is architectured along the principle of clean architecture, (aka
Hexagonal architecture, port and adapter, etc ...).

Due to the tiny nature of the project, separation between high-level code (the
domain models and use cases) and low-level code (actual implementation of
repositories and UI) is done on the source-set level, and not at the module
level. This also allow to reduce Kotlin Native compilation time.

### Business logic ###

Business logic is expressed in the core source set, in the form of use cases.
For instance :

``` kotlin
class ListPicturesUseCase(
    private val repository: PicturesRepository
) {
    fun listPictures(filter: Flow<String>): Flow<List<HubblePicture>> =
        combine(repository.getHubblePictures(), filter) { pics, f ->
            filterPictures(pics, f)
        }
}
```

Thanks to Flow, we combine the latest value of pictures obtained by the
repository and the latest value of the filter entered by the user, to give the
user back the matching pictures, which is the essence of our application logic.

In my opinion, this is a clear gain over plain coroutines, because we are
focusing on the data flow, instead of state implementation details we would need
to have with a more imperative solution based on coroutines.

This logic can be easily tested by mocking the repository, and temporal aspects
of this logic can also be easily simulated using virtual time. See
`ListPicturesUseCaseTest` for the associated test.

I highly recommend watching the following talks for testing coroutines, which
also applies to Flow:

https://www.youtube.com/watch?v=hMFwNLVK8HU

### Repository implementation ###

Repository is implemented using the Ktor client, and adapting to domain objects
expected by the use cases. The client is a straightforward consumption of a JSON
REST API using Ktor and Kotlinx.serialization.

  * https://ktor.io/clients/
  * https://github.com/Kotlin/kotlinx.serialization

### ViewModel implementation ###

As Kotlin Flow are by nature cold streams, they do not have state and we cannot
consume them directly in the reactive UI. To consume them and store their latest
state, we convert them to KLiveData by using helper methods in BaseViewModel.
Consumtion of the Flow is only performed when they are active listeners to the
LiveData.

Unfortunately it might seem redundant to use KLiveData when we have a richer API
given by Flow. Hopefully we may get a Flow equivalent of LiveData.

https://github.com/Kotlin/kotlinx.coroutines/pull/1354
TODO link to DataFlow

### SwiftUI implementation ###

A bridge is made to make BaseViewModel implement ObservableObject protocol,
allowing to observe view models in SwiftUI views using the @ObservedObject
property wrapper.

Currently it requires a hack in Kotlin code, to allow the Swift extension to
have state.

Views are componentarized so that their content can be easily previewed in any
given state, and to limit coupling with the view model.

There are a lot of videos of WWDC 2019 on SwiftUI. I highly recommend watching
them.

### Compose implementation ###

Compose implementation is quite similar to SwiftUI. Due to limited time, and
that the Compose is lot less mature. The UI is quite less polished compared to
SwiftUI

A great presentation of the internal working of Jetpack compose :
https://www.youtube.com/watch?v=6BRlI5zfCCk
