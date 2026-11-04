# Database Documentation

## Overview
This Spring Boot application uses a H2 database for data persistence

## Authentication Architecture

The application uses a **database-backed authentication system** with Spring Security:

### Database-Backed Authentication
- **Purpose**: Handles login/logout, session security, and user management
- **Storage**: H2 database `users` table with encrypted passwords
- **Configuration**: `SecurityConfiguration.java` and `UserService.java`
- **Password Encryption**: BCrypt hashing for secure password storage
- **User Registration**: Users can register new accounts via `/register` endpoint

### Default User
- **Username**: `user`
- **Password**: `password`
- **Created**: Automatically on startup if no users exist
- **Purpose**: Provides immediate access for testing/development

### User Management
- **Registration**: New users can register through the web interface
- **Authentication**: Database lookup with password verification
- **Session Management**: Spring Security handles user sessions
- **User Isolation**: Each user has isolated data (favorites, playlists, ratings)

### How Authentication Works:
1. User logs in with database credentials (username/password)
2. Spring Security validates against database User records
3. Authenticated user context (`authentication.getName()`) used for all operations
4. All user data is properly isolated by user relationship

### Location
- **Database Type**: In-memory H2 database (as specified by instructor)
- **Storage**: Temporary memory storage (data resets on restart)
- **Configuration File**: `src/main/resources/application.properties`

### Connection Details
- **Driver**: `org.h2.Driver`
- **JDBC URL**: `jdbc:h2:mem:chatdb`
- **Username**: `sa`
- **Password**: *(leave blank)*

### H2 Console Access
The H2 database console is enabled for development and debugging purposes:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:chatdb`
- **Username**: `sa`
- **Password**: *(leave blank)*

## Database Schema

The application uses Spring Data JPA. The database schema is automatically generated based on the entity classes.

### Entities and Tables

#### 1. Message Table
- **Entity Class**: `coit13235.assignment_3_project.entity.Message`
- **Table Name**: `message`
- **Purpose**: Stores chat messages between users

**Columns**:
- `id` (BIGINT, Primary Key, Auto-generated)
- `sender` (VARCHAR) - Username of the message sender
- `content` (VARCHAR, max 1000 chars) - Message content
- `created_at` (TIMESTAMP) - When the message was created

#### 2. User Table
- **Entity Class**: `coit13235.assignment_3_project.entity.User`
- **Table Name**: `users`
- **Purpose**: User authentication and data relationships

**Columns**:
- `id` (BIGINT, Primary Key, Auto-generated)
- `username` (VARCHAR, Unique, Not Null) - User's login username
- `password` (VARCHAR, Not Null) - BCrypt encrypted password

**Important Notes**:
- Passwords are encrypted using BCrypt for security
- Each user has isolated data (favorites, playlists, ratings) through foreign key relationships
- Users can register new accounts through the `/register` endpoint
- Default user `user/password` is created automatically on startup

#### 3. AudioFile Table
- **Entity Class**: `coit13235.assignment_3_project.entity.AudioFile`
- **Table Name**: `audiofile`
- **Purpose**: Stores both iTunes API track metadata AND uploaded audio file data

**Columns**:
- `id` (BIGINT, Primary Key, Auto-generated)
- `filename` (VARCHAR, Optional) - Reference filename (for identification)
- `originalFilename` (VARCHAR, Optional) - Original filename as uploaded
- `contentType` (VARCHAR, Optional) - MIME type of audio file
- `size` (BIGINT, Optional) - File size in bytes
- `fileData` (BLOB, Optional) - Binary audio file data stored in database (for uploaded files)
- `uploadedAt` (TIMESTAMP, Optional) - Timestamp when file was uploaded
- `user_id` (BIGINT, Foreign Key) - User who uploaded the file or added the iTunes track
- `trackName` (VARCHAR) - Track name (from iTunes API or uploaded file metadata)
- `artistName` (VARCHAR) - Artist name (from iTunes API or uploaded file metadata)
- `previewUrl` (VARCHAR, Optional) - iTunes preview URL (for iTunes tracks)
- `itunesTrackId` (VARCHAR, Optional) - iTunes track identifier (for iTunes tracks)

**Storage Architecture**:
- **Database Storage**: Audio files are stored as binary data directly in the database
- **No File System Dependencies**: Eliminates issues with file paths and directory management
- **Improved Reliability**: Database transactions ensure data consistency
- **Better Deployment**: No external file storage requirements

**Usage Patterns**:
- **iTunes Tracks**: Created when users add tracks from iTunes API search results to playlists/favorites
- **Uploaded Files**: Created when users upload actual audio files (stored as BLOB in database)
- **Hybrid Entity**: Single table handles both iTunes metadata and uploaded file binary data

#### 4. Playlist Table
- **Entity Class**: `coit13235.assignment_3_project.entity.Playlist`
- **Table Name**: `playlist`
- **Purpose**: Stores user-created playlists

**Columns**:
- `id` (BIGINT, Primary Key, Auto-generated)
- `name` (VARCHAR, Not Null) - Playlist name
- `description` (VARCHAR, max 1000 chars) - Optional playlist description  
- `isPublic` (BOOLEAN, Not Null, default false) - Whether playlist is publicly shareable
- `user_id` (BIGINT, Foreign Key, Not Null) - Owner user (maps to `owner` field)
- `createdAt` (TIMESTAMP, Not Null) - Creation timestamp
- `updatedAt` (TIMESTAMP) - Last modification timestamp
- **Join Table**: Many-to-many relationship with AudioFile through `playlist_audiofile` table

#### 5. Favorite Table
- **Entity Class**: `coit13235.assignment_3_project.entity.Favorite`
- **Table Name**: `favorite`
- **Purpose**: Stores user favorites for audio tracks or artists

**Columns**:
- `id` (BIGINT, Primary Key, Auto-generated)
- `user_id` (BIGINT, Foreign Key, Not Null) - User who marked as favorite
- `audiofile_id` (BIGINT, Foreign Key, Optional) - Audio file marked as favorite
- `artist_name` (VARCHAR, Optional) - Artist name marked as favorite
- `type` (ENUM: TRACK/ARTIST, Not Null) - Type of favorite
- `created_at` (TIMESTAMP) - When favorited
- **Constraints**: Unique combinations of (user_id, audiofile_id) and (user_id, artist_name)

#### 6. Rating Table
- **Entity Class**: `coit13235.assignment_3_project.entity.Rating`
- **Table Name**: `rating`
- **Purpose**: Stores user ratings for audio tracks

**Columns**:
- `id` (BIGINT, Primary Key, Auto-generated)
- `user_id` (BIGINT, Foreign Key, Not Null) - User who gave the rating
- `audiofile_id` (BIGINT, Foreign Key, Not Null) - Audio file being rated
- `rating` (INTEGER, Not Null) - Rating value (1-5 stars)
- `created_at` (TIMESTAMP, Not Null) - When rating was created
- `updated_at` (TIMESTAMP) - When rating was last updated

## Data Repositories

The application uses Spring Data JPA repositories for database operations:

- **MessageRepository**: `coit13235.assignment_3_project.repository.MessageRepository`
- **UserRepository**: `coit13235.assignment_3_project.repository.UserRepository`
- **AudioFileRepository**: `coit13235.assignment_3_project.repository.AudioFileRepository`
- **PlaylistRepository**: `coit13235.assignment_3_project.repository.PlaylistRepository`
- **FavoriteRepository**: `coit13235.assignment_3_project.repository.FavoriteRepository`
- **RatingRepository**: `coit13235.assignment_3_project.repository.RatingRepository`

## Database Setup and Initialization

### Automatic Setup
The database is automatically created when the application starts:
1. Hibernate creates tables based on entity classes (`spring.jpa.hibernate.ddl-auto=create-drop`)
2. The application uses in-memory storage as specified by instructor
3. Database schema is recreated fresh on each startup
4. Data persists during application runtime using Spring Data JPA

### Seed Data
Seed data is loaded through `ApplicationRunner` classes that implement initialization logic:
- Default user with username "user" for authentication
- Sample chat messages from Alice and Bob
- Data is loaded fresh on every application restart
- Demonstrates proper "persisted data" using JPA repositories

### Development Configuration
For development, the following settings are enabled:
- **H2 Console**: Accessible at `/h2-console`
- **SQL Logging**: All SQL queries are logged (`spring.jpa.show-sql=true`)
- **DDL Auto**: Tables are recreated on each startup (`spring.jpa.hibernate.ddl-auto=create-drop`)
- **In-Memory Database**: Data is properly persisted using JPA during runtime

## Development and Testing

### In-Memory Database Benefits
- **Clean State**: Database starts fresh on each application restart
- **No File Management**: No database files to manage or back up
- **Fast Testing**: Quick startup and teardown for testing
- **No Persistence Issues**: Cannot accidentally corrupt data files

### Data Reset
To reset the database:
1. Simply restart the application
2. Fresh database with seed data will be automatically created
3. No file cleanup required

### H2 Console
The H2 console provides a web interface for:
- Viewing table structures
- Running SQL queries
- Inspecting data during development
- Debugging database issues

**Access**: `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:chatdb`

#### Console Launcher Implementation
The application includes a dedicated console launcher interface:

- **Frontend Architecture**: Proper separation of concerns with dedicated `console-launcher.html`, `console-launcher.css`, and `console-launcher.js` files
- **User Experience**: Interactive console startup with loading states and error handling
- **Code Organization**: External CSS and JavaScript files referenced via Thymeleaf for maintainable development
- **Integration**: Seamless integration with Spring Boot's H2 console functionality

This implementation demonstrates industry-standard frontend architecture with modular, maintainable code structure.

---