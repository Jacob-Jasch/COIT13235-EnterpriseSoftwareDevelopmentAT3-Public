# Setup and Running Instructions

## Prerequisites

### Required Software
- **Java Development Kit (JDK) 21** or higher
- **Git** for version control
- **Maven** (optional - project includes Maven wrapper)

### Recommended Software
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Web Browser**: Chrome, Firefox, or Edge for testing

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/Jacob-Jasch/COIT13235-EnterpriseSoftwareDevelopmentAT3.git
cd COIT13235-EnterpriseSoftwareDevelopmentAT3
```

### 2. Navigate to Application Directory
```bash
cd assignment_3_project
```

### 3. Verify Java Installation
```bash
java -version
```
Expected output should show Java 21 or higher.

## Building the Application

### Using Maven Wrapper (Recommended)

#### Windows (PowerShell/Command Prompt)
```powershell
# Clean and compile
.\mvnw.cmd clean compile

# Run tests
.\mvnw.cmd test

```

#### Unix/Linux/macOS
```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package the application
./mvnw package
```

### Using Local Maven Installation
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package
```

## Running the Application

### Method 1: Using Maven (Development)

#### Windows
```powershell
.\mvnw.cmd spring-boot:run
```

#### Unix/Linux/macOS
```bash
./mvnw spring-boot:run
```

### Method 2: Using JAR File (Production)
```bash
# First, build the JAR
.\mvnw.cmd package

# Run the JAR
java -jar target/assignment_3_project-0.0.1-SNAPSHOT.jar
```

### Method 3: Using IDE
1. Open the project in your IDE
2. Locate `Assignment3ProjectApplication.java`
3. Right-click and select "Run" or "Debug"

## Accessing the Application

### Application URLs
Once the application is running, access it at:

- **Main Application**: `http://localhost:8080`
- **Chat Interface**: `http://localhost:8080/ui/chat`
- **Login Page**: `http://localhost:8080/login`
- **H2 Database Console**: `http://localhost:8080/h2-console`

### Default Login Credentials
- **Username**: `user`
- **Password**: `password`

This default user is created automatically on first startup. Additional users can register new accounts through the registration page.

### User Registration
- **Registration URL**: `http://localhost:8080/register`
- **Multi-User Support**: Each user has isolated data (favorites, playlists, ratings)
- **Authentication**: Database-backed with BCrypt password encryption

### H2 Database Console Access
- **JDBC URL**: `jdbc:h2:mem:chatdb`
- **Username**: `sa`
- **Password**: *(leave blank)*
- **Note**: In-memory database

## Testing the Application

### Running All Tests
```bash
# Windows
.\mvnw.cmd test

# Unix/Linux/macOS
./mvnw test
```

### Running Specific Test Classes
```bash
# Run chat API tests
.\mvnw.cmd test -Dtest=ChatApiTests

# Run repository tests
.\mvnw.cmd test -Dtest=ChatRepositoryTests
```

### Test Coverage Reports
Test coverage reports are generated using JaCoCo:
```bash
# Generate coverage report
.\mvnw.cmd jacoco:report

# View report at: target/site/jacoco/index.html
```

## Frontend Architecture

### Code Organization
The application follows modern web development best practices with proper separation of concerns:

- **HTML Structure**: Clean semantic markup using Thymeleaf templates focused on content structure and accessibility
- **CSS Presentation**: Modular CSS architecture with dedicated stylesheets for each component (search, chat, favorites, upload, etc.)
- **JavaScript Behavior**: Feature-specific JavaScript modules with no inline scripts for maintainable functionality
- **Console Launcher**: Dedicated web-based console interface with separated `console-launcher.css` and `console-launcher.js` files

### Frontend Components
- **Music Search**: `music-search.js` and `_music.css` for iTunes API integration
- **Chat System**: `chat-module.js` and `_chat.css` for real-time messaging
- **Favorites**: `favorites.js` and `_tracks-favorites.css` for track favoriting
- **Audio Upload**: `audio-upload.js` and `_upload.css` for file management
- **Console Interface**: `console-launcher.js` and `console-launcher.css` for H2 database access
- **Utility Functions**: `utils.js` for shared functionality across modules

This modular approach helps to ensure maintainability, reusability and follows best practice.

## API Endpoints

### Chat API
- **List Messages**: `GET http://localhost:8080/api/chat`
- **Add Message**: `POST http://localhost:8080/api/chat?sender=TestUser&content=Hello`
- **Get Message by ID**: `GET http://localhost:8080/api/chat/{id}`

### Audio API
- **Upload Audio**: `POST http://localhost:8080/api/audio/upload`
- **List Audio Files**: `GET http://localhost:8080/api/audio`
- **Download Audio**: `GET http://localhost:8080/api/audio/{id}/download`

### Playlist API
- **Create Playlist**: `POST http://localhost:8080/api/playlists`
- **List Playlists**: `GET http://localhost:8080/api/playlists`
- **Get Playlist**: `GET http://localhost:8080/api/playlists/{id}`

## Configuration

### Application Properties
The main configuration file is located at:
`src/main/resources/application.properties`

Key configurations:
```properties
# Application name
spring.application.name=assignment_3_project

# H2 Database configuration
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.url=jdbc:h2:mem:chatdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.show-sql=true
```

## Troubleshooting

### Common Issues and Solutions

#### 1. Java Version Issues
```bash
# Check Java version
java -version

# Set JAVA_HOME (Windows)
set JAVA_HOME=C:\Program Files\Java\jdk-21

# Set JAVA_HOME (Unix/Linux/macOS)
export JAVA_HOME=/path/to/java-21
```

#### 2. Build Failures
```bash
# Clean and retry
.\mvnw.cmd clean
.\mvnw.cmd compile
```

### Log Files
Application logs are displayed in the console. For file logging, add to `application.properties`:
```properties
logging.file.name=application.log
logging.level.root=INFO
logging.level.coit13235.assignment_3_project=DEBUG
```

## Support and Resources

### Documentation Links
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [H2 Database Documentation](http://www.h2database.com/html/main.html)
- [Maven Documentation](https://maven.apache.org/guides/)

### Project-Specific Documentation
- [Database Documentation](./DATABASE.md)
- [Project Structure](./PROJECT_STRUCTURE.md)
- [Requirements](./Requirements/README.md)
