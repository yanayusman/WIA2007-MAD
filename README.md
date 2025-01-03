# SharePlate

SharePlate is an Android application designed to facilitate food and non-food item donations, connecting donors with those in need. The app promotes community engagement and reduces waste through an easy-to-use platform.

## Features

- **User Authentication**
  - Secure login and signup functionality
  - Google Sign-in integration
  - Profile management

- **Donation Management**
  - Food item donations
  - Non-food item donations
  - Image upload capability
  - Real-time status tracking

- **Community Features**
  - Community campaigns
  - Event participation
  - Notifications system
  - Search functionality

- **User Interface**
  - Material Design implementation
  - Responsive layouts
  - Dark mode support
  - Custom animations

## Technical Stack

- **Platform**: Android (minimum SDK 24)
- **Language**: Java & Kotlin
- **Backend**: Firebase
  - Authentication
  - Cloud Firestore
  - Cloud Storage
- **Dependencies**:
  - Firebase BoM
  - AndroidX components
  - Material Design components
  - Glide for image loading
  - SwipeRefreshLayout
  - LocalBroadcastManager

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Configure Firebase:
   - Add your `google-services.json` file to the app directory
   - Enable Authentication, Firestore, and Storage in Firebase Console
4. Build and run the application

## Project Structure

shareplate/
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/shareplateapp/
│ │ │ │ ├── activities/
│ │ │ │ ├── fragments/
│ │ │ │ ├── models/
│ │ │ │ └── repositories/
│ │ │ └── res/
│ │ └── test/
│ └── build.gradle.kts
└── build.gradle.kts

## Requirements

- Android Studio Arctic Fox or newer
- JDK 17
- Android SDK 34
- Google Play Services
- Active internet connection

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Acknowledgments

- Material Design for Android
- Firebase platform
- Google Play Services
- Open source community

## Contact

For support or queries, please open an issue in the repository.

---
© 2024 SharePlate Team. All Rights Reserved.