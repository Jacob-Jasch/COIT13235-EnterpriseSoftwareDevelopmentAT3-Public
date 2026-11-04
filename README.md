# COIT13235-EnterpriseSoftwareDevelopmentAT3
<h2 align="center">Music Chat Application</h2>

A Spring Boot application that combines social messaging with music sharing capabilities. Users can chat with friends and share audio tracks/playlists in an integrated platform.

## Quick Start

```bash
# Clone the repository
git clone https://github.com/Jacob-Jasch/COIT13235-EnterpriseSoftwareDevelopmentAT3.git
cd COIT13235-EnterpriseSoftwareDevelopmentAT3/assignment_3_project

# Run the application
./mvnw spring-boot:run

# Access the application
# Web UI: http://localhost:8080/ui/chat
# Console Interface: http://localhost:8080/console
# API Documentation: http://localhost:8080/swagger-ui/index.html
# H2 Console: http://localhost:8080/h2-console
```

**Default Logins**: 
- Username: `user`, `alice`, `bob`, `charlie`, or `diana`
- Password: `password123` (for all accounts)

**Create New Account**: Visit `http://localhost:8080/register` to create additional users

## Interface Options

### 🖥️ **Console Interface**
Command-line interface accessible at `http://localhost:8080/console`
- **One-click launch** - No file system navigation required
- **Full feature access** - Chat, music search, data management
- **Automated testing** - Built-in testing
- **Perfect for graders** - Easy access without technical setup

### 🌐 **Web Interface**
Full-featured web application at `http://localhost:8080/ui/chat`
- Modern, responsive design
- Real-time chat interface
- Music search and playlist management
- User authentication and session management

### 📖 **API Documentation**
Comprehensive API documentation at `http://localhost:8080/swagger-ui/index.html`
- Interactive API explorer
- Complete endpoint documentation
- Authentication examples
- Request/response schemas

## Project Overview

This Spring Boot application integrates chat functionality with music sharing capabilities, featuring:

- **Real-time Chat System**: Send and receive messages with timestamps
- **Multi-User Support**: Five pre-configured users with isolated personal data
- **Audio File Management**: Share audio files using the iTunes API
- **Playlist Creation**: Create and manage custom playlists
- **User Favorites**: Mark and manage favorite tracks and artists
- **Rating System**: Rate songs and manage preferences
- **User Authentication**: Secure login and session management
- **Console Interface**: Command-line alternative for testing and automation
- **API Documentation**: Complete OpenAPI/Swagger documentation

## Architecture

**Technology Stack:**
- **Backend**: Spring Boot 3.5.5, Spring Data JPA, Spring Security
- **Frontend**: Thymeleaf templates, HTML/CSS/JavaScript
- **Database**: H2 in-memory database
- **API Documentation**: OpenAPI 3.0 with Swagger UI
- **Build Tool**: Maven
- **Java Version**: 21

**Application Layers:**
- **Controllers**: Handle HTTP requests and responses (Web + REST API)
- **Services**: Business logic and workflow management
- **Repositories**: Data access and persistence
- **Entities**: JPA models representing database schema
- **Console Interface**: Command-line interface for testing and automation

**Frontend Architecture:**
- **Separation of Concerns**: HTML templates, CSS stylesheets, and JavaScript modules in dedicated files
- **Modular CSS**: Component-based styling with consistent design system
- **Modern JavaScript**: Feature-complete modules with proper error handling
- **Responsive Design**: Mobile-friendly interfaces across all components

## Documentation

### Essential Guides
- **[Setup & Running Instructions](docs/SETUP.md)** - Complete setup, build, and deployment guide
- **[Database Documentation](docs/DATABASE.md)** - Database schema, configuration, and access details
- **[Project Structure](docs/PROJECT_STRUCTURE.md)** - Detailed code organization and architecture

### Feature Documentation
- **[Chat System](docs/CHAT_SYSTEM.md)** - Real-time messaging and audio link sharing
- **[Music Search](docs/MUSIC_SEARCH.md)** - iTunes integration and track discovery
- **[Rating System](docs/RATING_SYSTEM.md)** - Community rating system with inline editing
- **[Playlist System](docs/PLAYLIST_SYSTEM.md)** - Create and manage custom playlists
- **[Favorites System](docs/FAVORITES_SYSTEM.md)** - Mark and organize favorite tracks
- **[User Authentication](docs/AUTHENTICATION.md)** - Login system and security features
- **[Audio Upload Feature](docs/AUDIO_UPLOAD_FEATURE.md)** - Audio file management and sharing

### Project Documentation
- **[Requirements](docs/Requirements/README.md)** - Detailed project requirements and specifications
- **[Weekly Sprint Plans](docs/PM/)** - Project management and sprint documentation
- **[Technical Diagrams](docs/TM/)** - ERD, sequence diagrams, and use cases
- **[Testing](docs/QM/)** - Code testing, evidence of testing

## 🔧 Quick Access

### Application URLs
- **Main Application**: `http://localhost:8080`
- **Chat Interface**: `http://localhost:8080/ui/chat`
- **Login Page**: `http://localhost:8080/login`
- **H2 Database Console**: `http://localhost:8080/h2-console`

### Database Access
- **JDBC URL**: `jdbc:h2:mem:chatdb`
- **Username**: `sa`
- **Password**: *(leave blank)*
- **Type**: In-memory H2 database (as specified by instructor)
- **Note**: Data persists during runtime using Spring Data JPA

### API Endpoints
- **Chat API**: `GET/POST http://localhost:8080/api/chat`
- **Audio API**: `http://localhost:8080/api/audio`
- **Playlist API**: `http://localhost:8080/api/playlists`
- **Rating API**: `GET/POST/PUT/DELETE http://localhost:8080/api/ratings`

##  Testing

```bash
# Run all tests
./mvnw test

# Run specific test suites
./mvnw test -Dtest=ChatApiTests
./mvnw test -Dtest=ChatRepositoryTests
./mvnw test -Dtest=Assignment3ProjectApplicationTests

# Generate test coverage report
./mvnw jacoco:report
```

**Test Coverage**: Reports available in `target/site/jacoco/index.html`

## Project Structure

```
assignment_3_project/
├── src/main/java/coit13235/assignment_3_project/
│   ├── controller/          # Web controllers (MVC layer)
│   ├── service/            # Business logic layer
│   ├── repository/         # Data access layer
│   ├── entity/            # JPA entities (database models)
│   └── exception/         # Error handling
├── src/main/resources/
│   ├── application.properties
│   ├── templates/         # Thymeleaf HTML templates
│   └── static/           # CSS, JS, and static assets
├── uploads/              # User-uploaded audio files
└── docs/                # Project documentation
```

## Database Schema

**Main Entities:**
- `Message` - Chat messages with ratings
- `User` - User accounts and authentication
- `AudioFile` - Uploaded audio file metadata
- `Playlist` - User-created playlists
- `Favorite` - User favorites for tracks
- `Rating` - User ratings for various items

## Development

### Prerequisites
- Java 21+
- Maven
- Git

### Build Commands
```bash
# Compile
./mvnw compile

# Package
./mvnw package

# Clean build
./mvnw clean package

# Run with development profile
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Development Features
- **H2 Console**: Database inspection and SQL queries
- **SQL Logging**: All queries logged for debugging
- **Test Coverage**: JaCoCo integration for coverage reports

## Deployment

### Development
```bash
./mvnw spring-boot:run
```

## Weekly Development Progress

### Week 8 - Chat Foundation (✅ Complete)
- Spring Boot web application with Maven
- Thymeleaf UI for chat messages
- Spring Data JPA with H2 database
- Basic authentication and security
- Seed data initialization

### Week 9 - Audio Integration (✅ Complete) 
- Audio file upload and storage
- Playlist creation and management
- Music sharing capabilities
- Enhanced user interface

### Week 10 - Advanced Features (✅ Complete)
- User rating system
- Favorites functionality
- Multi-user support
- Enhanced security

### Week 11 - Finalization (✅ Complete)
- Rating features
- Final testing and documentation
- Playlist features


---

**Repository**: [COIT13235-EnterpriseSoftwareDevelopmentAT3](https://github.com/Jacob-Jasch/COIT13235-EnterpriseSoftwareDevelopmentAT3)  
**Course**: COIT13235 Enterprise Software Development  
**Institution**: CQUniversity  
**Assignment**: Assignment 3 - Team Enterprise Application Development

---
