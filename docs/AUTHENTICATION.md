# User Authentication System Documentation

## Overview
The authentication system uses Spring Security with database-backed user management to provide secure multi-user functionality. Each user has their own isolated data including ratings, playlists, and favorites.

## Features

### Multi-User Support
- **Database Authentication**: Users stored in H2 database with encrypted passwords
- **User Registration**: New users can create accounts through the registration form
- **Data Isolation**: Each user's data (favorites, playlists, ratings) is completely separate
- **Session Management**: Persistent login sessions with proper user context

### User Management
- **Registration System**: Secure user account creation with validation
- **Password Security**: BCrypt encryption for all passwords
- **Username Validation**: Unique usernames with duplicate checking
- **User Context**: Access to current user information throughout app

### Protected Features
- **Ratings**: Create and edit track ratings (user-specific)
- **Playlists**: Create and manage personal playlists (user-specific)
- **Favorites**: Mark and manage favorite tracks and artists (user-specific)
- **Chat**: Enhanced features for authenticated users

## User Interface

### Login Page
- **Simple Form**: Username and password fields
- **Login Button**: Submit credentials
- **Error Messages**: Display authentication failures
- **Registration Link**: Link to create new account
- **Redirect**: Automatic redirect to intended page after login

### Registration Page
- **Registration Form**: Username, password, and confirmation fields
- **Validation**: Real-time validation with error messages
- **Password Requirements**: Minimum 6 characters
- **Success Redirect**: Automatic redirect to login after successful registration

## Frontend Implementation

### User Experience Components
- **Login Form**: Accessible form design with proper labeling and error messaging
- **Registration Form**: Real-time validation feedback with consistent styling
- **Error Handling**: User-friendly error messages with appropriate visual feedback
- **Navigation**: Seamless integration with the main application interface
- **Responsive Design**: Mobile-friendly authentication forms

## Technical Implementation

### Spring Security Configuration
- **Form Login**: `/login` endpoint for authentication
- **Session Management**: HTTP session-based authentication
- **Security Headers**: Standard security headers applied

### Authentication Flow
1. **Access Protected Resource**: User tries to access protected feature
2. **Check Authentication**: Spring Security checks if user is logged in
3. **Redirect to Login**: If not authenticated, redirect to login page
4. **Authenticate**: User enters credentials
5. **Validate Credentials**: Spring Security validates username/password
6. **Create Session**: Successful login creates authenticated session
7. **Redirect Back**: User redirected to originally requested resource

### User Entity (`User.java`)
```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String username;     // Unique username
}
```

### Authentication Method
- **Database Authentication**: Uses Spring Security's UserDetailsService with database backend
- **Default Credentials**: username="user", password="password"
- **Dynamic User Store**: Database-backed user registration and management
- **Password Encryption**: BCrypt hashing with unique salts

### Security Configuration
- **Password Encoding**: BCrypt password hashing
- **Session Timeout**: Configurable session expiration
- **Remember Me**: Optional persistent login

## Default Credentials

### Test Account
- **Username**: `user`
- **Password**: `password`
- **Purpose**: Testing

### Creating New Users
- **Registration**: Visit `/register` to create new accounts
- **Validation**: Username must be unique, password minimum 6 characters
- **Instant Access**: New users can immediately login and use all features

### Security Notes
- **Development Only**: Default credentials for development/testing
- **Change in Production**: Must change default credentials for production
- **Password Security**: All passwords encrypted with BCrypt

## Authentication Requirements

### Protected Features
- **Rating System**: Must be logged in to rate tracks
- **Playlist Management**: Authentication required for creating/editing playlists
- **Favorites**: Must be logged in to favorite/unfavorite tracks

## Security Features

### Password Security
- **BCrypt Hashing**: Passwords stored with secure hashing
- **Salt Generation**: Unique salt per password
- **Configurable Strength**: Adjustable hashing rounds

## Usage Instructions

### Creating New Account
1. Navigate to registration (`http://localhost:8080/register`)
2. Enter unique username and secure password
3. Confirm password and submit
4. Redirected to login page with success message
5. Login with new credentials

### Logging In
1. Navigate to application (`http://localhost:8080`)
2. Click login link or access protected feature
3. Enter credentials (default: username=`user`, password=`password`)
4. Click "Login" button
5. Redirected to intended page or dashboard

### Data Isolation
- **Personal Data**: Each user sees only their own favorites, playlists, and ratings
- **Secure Separation**: No data sharing between user accounts
- **Session-Based**: User context maintained throughout session

### Using Protected Features
1. Ensure you are logged in
2. Access ratings, playlists, or favorites features
3. Features work normally with authentication

### Troubleshooting Login
1. **Check Credentials**: Ensure username is `user` and password is `password`
2. **Clear Browser Data**: Clear cookies and try again

## Configuration

### Application Properties
```properties
# Security settings in application.properties
spring.security.user.name=user
spring.security.user.password=password
```

### Session Configuration
- **Timeout**: Configurable session expiration time
- **Persistence**: Sessions persist across browser restarts
- **Cleanup**: Automatic cleanup of expired sessions

## Troubleshooting

### Common Issues

#### Can't Login
- **Cause**: Incorrect username/password or server error
- **Solution**: Use `user`/`password` and check server is running

#### Features Not Working
- **Cause**: Not authenticated or session lost
- **Solution**: Check login status and re-authenticate if needed

### Debug Steps
1. **Check Credentials**: Verify using correct username/password
2. **Browser Console**: Look for authentication errors
3. **Network Tab**: Check login requests are successful
4. **Clear Session**: Clear browser cookies and retry

## Integration with Features

### Rating System
- **Authentication Check**: Validates user before allowing ratings
- **User Association**: Links ratings to authenticated user
- **Edit Permissions**: Only allows editing own ratings

### Playlist System
- **User Ownership**: Playlists owned by authenticated user
- **Privacy Controls**: User controls playlist visibility
- **Management Rights**: Only owner can modify playlists

### Favorites System
- **Personal Favorites**: Favorites specific to logged-in user
- **Privacy**: Favorites not visible to other users
- **Persistence**: Favorites saved across sessions

## Related Documentation
- [Rating System](RATING_SYSTEM.md) - Authentication requirements for ratings
- [Playlist System](PLAYLIST_SYSTEM.md) - Login requirements for playlists
- [Favorites System](FAVORITES_SYSTEM.md) - Authentication for favorites
- [Setup Instructions](SETUP.md) - Application configuration and startup