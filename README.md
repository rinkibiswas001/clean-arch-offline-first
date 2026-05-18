# Modula - Android Clean Architecture Demo

An Android application that fetches and displays a paginated list of users with full offline support, built using Clean Architecture, MVVM, and offline-first principles.

## 🚀 Technologies & Libraries
- **Kotlin**: Primary programming language.
- **Clean Architecture**: Separation of concerns into Data, Domain, and Presentation layers.
- **Hilt**: Dependency injection.
- **Retrofit**: Network requests (REST API).
- **Room**: Local database for caching (Single Source of Truth).
- **Coroutines & Flow**: Asynchronous programming and reactive data streams.
- **View Binding**: Secure interaction with UI components.
- **RecyclerView + DiffUtil**: Efficient list rendering with pagination support.

## 🏗️ Architecture
The app follows the Clean Architecture pattern:
```
app/
├── data/
│   ├── local/          # Room database (UserDao, UserEntity, AppDatabase)
│   ├── remote/         # Retrofit API service & dto -> UserDataRes
│   ├── mapper/         # DTO ↔ Entity ↔ Domain mappers
│   └── repository/     # UserRepositoryImpl
├── domain/
│   ├── model/          # User (domain model)
│   ├── repository/     # UserRepository interface
│   └── usecase/        # GetUsersUseCase
├── presentation/
│   └── main_screen/    # MainActivity, UserViewModel, UserAdapter
└── util/
    └── Resource.kt     # Loading / Success / Error sealed class
```

## 🏗️ Data Flow
```
MainActivity
    └── observes → UserViewModel.uiState (StateFlow)
                        └── GetUsersUseCase
                                └── UserRepositoryImpl
                                        ├── UserDao (Room)        ← cache read/write
                                        └── ApiService (Retrofit) ← network fetch
```
- **Step-by-step flow**
```
1.  App starts          → MainActivity created
2.  Hilt                → injects UserViewModel into MainActivity
3.  ViewModel.init      → fetchUsers() called automatically
4.  UiState emits       → Resource.Loading → ProgressBar visible
5.  GetUsersUseCase     → invoke() called
6.  UserRepositoryImpl  → getUsers() called
                           ├── Room DB queried → cached users emitted instantly (if available)
                           └── Retrofit hits API → https://dummyjson.com/users
7.  API response        → List<UserDto> received
8.  Mapper              → each UserDto mapped to User via .toDomain()
9.  Room updated        → fresh data saved to local DB (cache refreshed)
10. Resource.Success    → emitted to ViewModel with user list
11. UiState emits       → Resource.Success(users) → isLoading = false
12. StateFlow           → notifies MainActivity collector
13. RecyclerView        → adapter.submitList(users) called
14. DiffUtil            → calculates list differences efficiently
15. UI renders          → RecyclerView displays the user list
```

## 📄 Key Features
- **Pagination**: Efficiently loads data from `dummyjson.com/users` in chunks.
- **Offline Support**: Uses Room as a Single Source of Truth. Data is first loaded from the cache while fresh data is fetched from the network.
- **Reactive UI**: The UI automatically updates when the local database changes.

## 🛠️ Getting Started
1. Clone the repository.
2. Open the project in Android Studio (Ladybug or newer).
3. Sync Gradle and run the `:app` module on an emulator or physical device.

## 🔗 API Reference
The project uses the following endpoint for user data:
`https://dummyjson.com/users?limit=10&skip=0`
