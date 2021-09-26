# LocationProviderApplication

Loction Provider application fetches a user's location and stores it in room database, and is written completely in Kotlin. Uses Work Manager for peridically fetching user's location in background and storing it in the local database, Hilt for dependency injection, Room database for persistant storage of user's location
and Fused Location Provider for using Google's location services.
 
Libraries used:
1. Work Manager
2. Fused Location Provider
3. Hilt
4. Room database


Architecture Patterns used:
1. Repository Pattern
2. Clean architecture
3. MVVM architecture
