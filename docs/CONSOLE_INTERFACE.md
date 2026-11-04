# Console Interface Documentation

## Overview

The Music Chat Application includes a professional console interface that provides command-line access to all application features. This interface is designed for testing, automation, and grader evaluation.

## Access Methods

### 🌐 **Web-Based Launcher (Recommended)**
The easiest way to access the console interface:

1. Start the application
2. Open your browser to: `http://localhost:8080/console`
3. Click the "Start Console Interface" button
4. Monitor your application's terminal/logs for the console interface

## Authentication

The console interface includes five pre-configured test accounts:

| Username | Password | Description |
|----------|----------|-------------|
| `user` | `password123` | Primary test account |
| `alice` | `password123` | Secondary test account |
| `bob` | `password123` | Secondary test account |
| `charlie` | `password123` | Secondary test account |
| `diana` | `password123` | Secondary test account |


## Console Interface Structure

```
=== MUSIC CHAT CONSOLE INTERFACE ===
1. Chat Management
   ├── Send Message
   ├── View Chat History
   └── Real-time Updates

2. Music Search
   ├── Search iTunes API
   ├── Browse Results
   └── Add to Playlist

3. Data Viewing
   ├── View Users
   ├── View Audio Files
   ├── View Playlists
   ├── View Favorites
   └── View Ratings

4. Automated Testing
   ├── Run All Tests
   ├── User Tests
   ├── Chat Tests
   ├── Music Tests
   └── Data Tests

5. Exit
```

## Usage Examples

### Basic Chat Flow
```
1. Login with username: user
2. Navigate to Chat Management
3. Send a message: "Hello from console!"
4. View chat history to confirm message
```

### Music Search Flow
```
1. Login with username: alice
2. Navigate to Music Search
3. Search for: "Taylor Swift"
4. Browse results and select a track
5. Add to personal playlist
```

### Testing Flow
```
1. Login with username: user
2. Navigate to Automated Testing
3. Run comprehensive test suite
4. Review test results and coverage
```

## Integration Points

### Database Integration
- Full H2 database connectivity
- Transaction management
- Data persistence verification
- Foreign key relationship testing

### API Integration
- iTunes API connectivity tests
- External service validation
- Error handling verification
- Rate limiting compliance

### Security Integration
- Authentication testing
- Password encryption verification
- Session management validation
- Authorization boundary testing

## Troubleshooting

### Common Issues

**Console doesn't start:**
- Ensure application is running (`./mvnw spring-boot:run`)
- Check for port conflicts (default: 8080)
- Verify Java 21 is installed

**Login fails:**
- Use exact credentials: username `user`, password `password123`
- Ensure database initialization completed
- Check application logs for errors

**Features not working:**
- Verify internet connection for iTunes API
- Check application logs for detailed error messages
- Ensure all dependencies are properly loaded

### Support
For technical issues:
1. Check application logs in terminal
2. Review console output for error messages
3. Verify all dependencies are installed
4. Ensure proper Java version (21+)