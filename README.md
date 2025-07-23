# BaseApp-KMP (Kotlin Multiplatform)

A Kotlin Multiplatform application with authentication and chat capabilities built using Compose Multiplatform UI. This project demonstrates a complete authentication system with JWT token management and secure storage across multiple platforms.

## Features

- **Authentication System**: Complete user registration and login with JWT tokens
- **Secure Token Storage**: Platform-specific encrypted storage (Android Keystore, iOS Keychain)
- **Chat Interface**: Real-time chat functionality with session management
- **Comprehensive Logging**: Multi-platform logging system with security-aware token logging
- **Modern UI**: Built with Compose Multiplatform for consistent UI across platforms

## Architecture

The project follows a clean architecture pattern with:

- **Shared Module** (`com.base.shared`): Contains all business logic, networking, and data management
- **Platform-Specific Modules**: Android, iOS, web, and WASM implementations
- **Dependency Injection**: Centralized AuthModule for proper component wiring
- **MVVM Pattern**: ViewModels for state management with StateFlow

## Project Structure

```
shared/src/
├── commonMain/kotlin/com/base/shared/
│   ├── auth/           # Authentication management
│   ├── models/         # Data models and DTOs
│   ├── network/        # HTTP client and repositories
│   ├── screens/        # Compose UI screens
│   ├── storage/        # Token storage interfaces
│   ├── utils/          # Utilities and logging
│   └── viewModels/     # MVVM ViewModels
├── androidMain/        # Android-specific implementations
├── iosMain/           # iOS-specific implementations
├── jsMain/            # Web-specific implementations
└── wasmJsMain/        # WASM-specific implementations
```

## Current Supported Platforms

1. **Android** (`com.base.android`)
2. **iOS** (BaseApp-KMP)
3. **Web** (JavaScript)
4. **WASM** (WebAssembly)

## Key Components

### Authentication System
- **AuthManager**: Handles token lifecycle and validation
- **AuthRepository**: Network operations for login/registration
- **TokenStorage**: Secure, platform-specific token persistence
- **AuthModule**: Dependency injection for authentication components

### Security Features
- JWT token management with automatic refresh
- Encrypted storage using platform-specific secure storage
- Security-aware logging (only first 8 characters of tokens logged)
- HTTP client with configurable logging levels

### Networking
- Ktor HTTP client with timeout configuration
- JSON serialization with Kotlinx.serialization
- Centralized HTTP client provider
- Configurable logging (INFO level for security)

## Getting Started

### Prerequisites
- Android Studio or IntelliJ IDEA
- Xcode (for iOS development)
- Backend API server running on `http://10.0.2.2:8000` (Android emulator) or `http://localhost:8000`

### Running the Application

1. **Android**: Run the `androidApp` configuration
2. **iOS**: Open `iosApp.xcodeproj` in Xcode and run
3. **Web**: Run the `jsApp` configuration
4. **WASM**: Run the `wasmJsApp` configuration

### Backend Requirements

The application expects a REST API with the following endpoints:
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User authentication
- `GET /api/v1/auth/sessions` - Session management
- `POST /api/v1/chat/completions` - Chat functionality

## Configuration

### API Configuration
Update `ApiConfig.kt` files for each platform to configure the backend URL:
- Development: `http://10.0.2.2:8000` (Android)
- Production: Configure appropriate production URLs

### Logging
The application includes comprehensive logging with:
- Platform-specific implementations
- Authentication operation tracking
- Secure token logging (first 8 characters only)
- Configurable log levels

## Development Notes

- The project uses expect/actual pattern for platform-specific implementations
- All authentication state is managed through StateFlow for reactive UI updates
- Token storage is encrypted on all platforms for security
- HTTP logging is set to INFO level to prevent token exposure in logs

## License

```
MIT License

Copyright (c) 2024 Michele Giannone

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```