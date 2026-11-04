# Project Structure Documentation

## Overview
This document describes the structure and organization of the Music Chat Application Spring Boot project, including the web interface, console interface, and API documentation.

## Root Directory Structure

```
COIT13235-EnterpriseSoftwareDevelopmentAT3/
├── README.md                           # Main project documentation
├── assignment_3_project/               # Main application directory
│   ├── src/                           # Source code
│   ├── target/                        # Maven build output
│   ├── pom.xml                        # Maven configuration
│   ├── mvnw                          # Maven wrapper (Unix)
│   └── mvnw.cmd                      # Maven wrapper (Windows)
└── docs/                              # Project documentation
    ├── CONSOLE_INTERFACE.md           # Console interface documentation
    ├── DATABASE.md                    # Database documentation
    ├── PROJECT_STRUCTURE.md           # This file
    ├── PM/                           # Project Management docs
    ├── QM/                           # Quality Management docs
    ├── Requirements/                  # Requirements documentation
    └── TM/                           # Technical Management docs
```

## Application Structure (`assignment_3_project/`)

### Source Code (`src/`)

#### Main Source (`src/main/`)

```
src/main/
├── java/coit13235/assignment_3_project/
│   ├── Assignment3ProjectApplication.java    # Main Spring Boot application
│   ├── SecurityConfiguration.java           # Spring Security configuration
│   ├── config/
│   │   └── OpenApiConfig.java               # OpenAPI/Swagger configuration
│   ├── console/
│   │   └── ConsoleInterface.java            # Command-line interface
│   ├── controller/                          # Web & API controllers
│   │   ├── AudioController.java            # Audio file management
│   │   ├── ChatController.java             # Chat functionality (Web + REST)
│   │   ├── ConsoleController.java          # Console launcher interface
│   │   ├── FavoriteController.java         # Favorites API endpoints
│   │   ├── LoginController.java            # User authentication
│   │   ├── MusicController.java            # Music search integration
│   │   ├── PlaylistController.java         # Playlist management API
│   │   └── RatingController.java           # Rating system API
│   ├── entity/                             # JPA entities (database models)
│   │   ├── AudioFile.java                  # Audio file metadata
│   │   ├── Favorite.java                   # User favorites
│   │   ├── Message.java                    # Chat messages
│   │   ├── Playlist.java                   # Music playlists
│   │   ├── Rating.java                     # Item ratings
│   │   └── User.java                       # User accounts
│   ├── repository/                         # Data access layer
│   │   ├── AudioFileRepository.java        # Audio file data access
│   │   ├── FavoriteRepository.java         # Favorites data access
│   │   ├── MessageRepository.java          # Message data access
│   │   ├── PlaylistRepository.java         # Playlist data access
│   │   ├── RatingRepository.java           # Rating data access
│   │   └── UserRepository.java             # User data access
│   ├── service/                            # Business logic layer
│   │   ├── AudioFileService.java           # Audio file operations
│   │   ├── FavoriteService.java           # Favorites management
│   │   ├── MusicSearchService.java        # Music search integration
│   │   ├── PlaylistService.java           # Playlist management
│   │   ├── RatingService.java             # Rating operations
│   │   └── UserService.java               # User authentication & management
│   └── exception/                          # Custom exceptions
│       └── GlobalExceptionHandler.java     # Global error handling
└── resources/
    ├── application.properties              # Application configuration
    ├── static/                            # Static web assets
    │   ├── css/                           # Stylesheets
    │   └── js/                            # JavaScript files
    └── templates/                         # Thymeleaf templates
        ├── chat.html                      # Chat interface
        ├── error.html                     # Error pages
        ├── login.html                     # Login page
        └── register.html                  # User registration page
```

#### Test Source (`src/test/`)

```
src/test/java/coit13235/assignment_3_project/
├── Assignment3ProjectApplicationTests.java  # Application context tests
├── ChatApiTests.java                       # Chat API endpoint tests
├── ChatExtraTests.java                     # Additional chat tests
└── ChatRepositoryTests.java               # Repository layer tests
```

### Build Output (`target/`)

```
target/
├── classes/                              # Compiled Java classes
├── generated-sources/                    # Generated source files
├── generated-test-sources/               # Generated test sources
├── maven-status/                         # Maven build status
├── surefire-reports/                     # Test execution reports
└── test-classes/                         # Compiled test classes
```

### File Storage (Database)

**Architecture Change**: Audio files are now stored directly in the database as binary data (BLOB) instead of the file system.

```
uploads/
└── audio/                               # Legacy directory (no longer used)
```

**Benefits**:
- No file system dependencies
- Improved data consistency through database transactions  
- Better deployment portability
- Simplified backup and recovery

*Note: The uploads directory may still exist but is no longer used for new uploads. All audio data is stored in the `audiofile` table's `fileData` BLOB column.*

## Architecture Overview

### Spring Boot Application Layers

1. **Presentation Layer** (`controller/`)
   - Handles HTTP requests and responses
   - Implements MVC pattern with Thymeleaf templates
   - Manages user authentication and session

2. **Business Logic Layer** (`service/`)
   - Contains application business rules
   - Processes data between controllers and repositories
   - Handles complex operations and workflows

3. **Data Access Layer** (`repository/`)
   - Interfaces for database operations
   - Extends Spring Data JPA repositories
   - Provides CRUD operations and custom queries

4. **Data Model Layer** (`entity/`)
   - JPA entities representing database tables
   - Defines relationships between entities
   - Contains validation annotations

5. **Configuration Layer** (`config/`)
   - Spring Security configuration for authentication
   - Database and security settings
   - Multi-user access control and session management

5. **Exception Handling** (`exception/`)
   - Global exception handling
   - Custom error responses
   - Centralized error management

### Key Design Patterns

- **MVC (Model-View-Controller)**: Separates concerns between data, presentation, and logic
- **Repository Pattern**: Abstracts data access operations
- **Service Layer Pattern**: Encapsulates business logic
- **Dependency Injection**: Managed by Spring Framework

## Application Features

### Rating System
- **Location**: `RatingController`, `RatingService`, `Rating` entity
- **Functionality**: 
  - Ratings for audio tracks (1-5 stars)
  - Inline dropdown editing for authenticated users
  - Real-time rating updates without page refresh
  - Community ratings tab showing all user ratings
  - Rating button placement next to track names
- **UI Components**: Dropdown selectors, star displays, edit functionality
- **Security**: Authentication required for rating creation/updates

### Chat System
- **Location**: `ChatController`, `MessageRepository`, `Message` entity
- **Functionality**: Real-time messaging with timestamp display
- **Features**: Message history, audio link detection and embedding

### Music Management
- **Location**: `MusicController`, iTunes API integration
- **Functionality**: Music search, track display, playlist management
- **Features**: Track favorites, playlist CRUD operations

### Audio Upload System
- **Location**: `AudioController`, `AudioFileService`, `AudioFile` entity
- **Functionality**: File upload with database storage (BLOB)
- **Features**: Drag-and-drop upload, audio player embedding, playlist integration

## Configuration Files

### Maven Configuration (`pom.xml`)
- **Spring Boot Version**: 3.5.5
- **Java Version**: 21
- **Key Dependencies**:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Security
  - Spring Boot Starter Thymeleaf
  - H2 Database
  - Spring Cloud dependencies

### Application Configuration (`application.properties`)
- Database connection settings
- H2 console configuration
- JPA/Hibernate settings
- Application-specific properties

## Static Assets (`src/main/resources/static/`)

### CSS Stylesheets (`css/`)
```
css/
├── _audio-player.css                   # Global audio player styling
├── _buttons.css                       # Global button styles and states
├── _chat.css                          # Chat interface styling
├── _layout.css                        # Main layout and grid systems
├── _music.css                         # Music section layout and tabs
├── _tracks-favorites.css              # Track items, favorites styling, and rating dropdowns
├── _upload.css                        # Audio upload feature styling
├── console-launcher.css               # Console launcher interface styling
└── style.css                          # Main stylesheet (imports all others)
```

### JavaScript Modules (`js/`)
```
js/
├── audio-upload.js                     # Complete upload feature with interactions
├── chat-module.js                      # Chat message processing and display
├── console-launcher.js                 # Console launcher functionality and AJAX calls
├── favorites.js                        # Favorites management functionality
├── main.js                            # Application initialization
├── music-search.js                     # iTunes search and track display
├── playlist-manager.js                 # Playlist CRUD operations
├── playlist-ui.js                      # Playlist UI interactions and modals
└── utils.js                           # Shared utilities, tab management, and rating system
```

#### JavaScript Module Responsibilities

**audio-upload.js**
- Drag-and-drop file upload interface
- Upload progress tracking and validation
- Complete track-like functionality for uploaded files:
  - Send to chat with embedded audio players
  - Add to playlists with modal selection
  - Toggle favorites with visual feedback
  - Rate files (1-5 stars) with dropdown
  - Delete files with confirmation
- Favorites status loading and state management
- Integration with existing playlist and favorite systems

**chat-module.js**
- Real-time chat message processing
- Audio URL detection and embedding (both iTunes and uploaded files)
- Chat history management and display
- Message formatting and sanitization

**console-launcher.js**
- Web-based console interface launcher functionality
- AJAX console startup requests to `/console/start` endpoint
- Loading state management with spinner animations
- Error handling and user feedback messaging
- Button state management (disabled/enabled states)
- Status message display (success/error states)

**favorites.js**
- Track and artist favorite/unfavorite operations
- AJAX integration with favorite endpoints
- UI state updates and visual feedback

**music-search.js**
- iTunes API integration and search functionality
- Track result display and formatting
- Search result interaction handling

**playlist-manager.js & playlist-ui.js**
- Complete playlist CRUD operations
- Modal-based playlist selection interface
- Track addition/removal from playlists
- Public/private playlist visibility management

**utils.js**
- Tab switching and navigation
- Rating system functionality (load, edit, save ratings)
- Inline dropdown editing for rating updates
- Community ratings display and management
- Global utility functions
- Cross-module shared functionality

### Static Resource Architecture
- **Modular CSS**: Component-based styling with clear separation
- **Feature-Complete JavaScript**: Each module handles complete functionality
- **Consistent Styling**: Global button and component styles reused across features
- **Progressive Enhancement**: All features work with and without JavaScript
- **Responsive Design**: Mobile-friendly interfaces across all components

## Documentation Structure (`docs/`)

### Project Management (`PM/`)
- Weekly sprint plans
- Project documentation videos
- Progress tracking documents

### Quality Management (`QM/`)
- Test coverage reports (JaCoCo)
- Evidence screenshots
- Quality assurance documentation

### Technical Management (`TM/`)
- Entity Relationship Diagrams (ERD)
- Sequence diagrams
- Use case diagrams
- Technical specifications

### Requirements (`Requirements/`)
- Assignment specifications

## Audio File Storage Architecture

### Database Storage (Current)
- **Storage Method**: Binary data (BLOB) stored directly in H2 database
- **Table**: `audiofile` table with `fileData` column 
- **Benefits**: 
  - No file system dependencies
  - Improved data consistency
  - Better deployment portability
  - Automatic backup with database

### Legacy Directory Structure
- **Directory**: `uploads/audio/` (no longer used for new uploads)
- **Status**: Legacy - kept for reference but files stored in database
- **Supported Formats**: MP3, WAV, etc. (validation maintained)

---