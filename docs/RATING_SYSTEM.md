# Rating System Documentation

## Overview
The rating system allows users to rate audio tracks from 1-5 stars and provides a community ratings tab where users can view all ratings and edit their own.

## Features

### Community Ratings Tab
- **View All Ratings**: Browse ratings from all users with track information
- **Live Updates**: Ratings refresh automatically every 30 seconds
- **User Attribution**: Each rating shows the username of the rater

### Edit Your Own Ratings
- **Inline Editing**: Simple dropdown to change your rating (no complex modals)
- **Instant Updates**: Changes save immediately when dropdown value changes
- **Delete Ratings**: Remove your ratings with a confirmation dialog
- **User Ownership**: Only your own ratings show edit controls

### Chat Integration
- **Inline Rating Buttons**: Rate tracks directly from chat audio messages
- **Consistent UI**: Rating controls positioned next to track names
- **Real-time Feedback**: Notifications confirm successful rating changes

### Authentication
- **Login Required**: Must be authenticated to rate or edit ratings
- **Session Management**: Uses Spring Security for user sessions
- **Ownership Verification**: Backend validates user owns ratings before allowing edits

## User Interface

### Rating Display Format
```
★★★★★ 4/5 stars - username [Edit Dropdown] [Delete Button]
```

### Edit Controls (Your Ratings Only)
- **Rating Dropdown**: Select 1-5 stars with star symbols (★)
- **Delete Button**: 🗑️ icon with confirmation prompt

### Notifications
- **Success**: "🎵 Rating updated to X stars!" (green)
- **Delete**: "🎵 Rating deleted successfully!" (green)
- **Error**: Authentication or network error messages (red/orange)

## Technical Implementation

### Backend Components

#### Rating Entity (`Rating.java`)
```java
@Entity
public class Rating {
    private Long id;
    private User user;           // Who rated
    private AudioFile audioFile; // What was rated
    private int rating;          // 1-5 stars
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### Rating Controller (`RatingController.java`)
- **POST** `/api/ratings` - Create new rating
- **GET** `/api/ratings/all` - Get all ratings with track info
- **PUT** `/api/ratings/{id}` - Update existing rating
- **DELETE** `/api/ratings/{id}` - Delete rating
- **GET** `/api/ratings/my` - Get current user's ratings

#### Rating Service (`RatingService.java`)
- `rateAudioFile()` - Create or update rating
- `getAllRatings()` - Fetch all ratings with track details
- `getRatingsByUser()` - Get user-specific ratings
- `deleteRating()` - Remove rating with ownership validation

### Frontend Components

#### JavaScript (`utils.js`)
- `loadRatings()` - Fetch and display all ratings
- `addRatingEditListeners()` - Handle dropdown changes and delete clicks
- `switchTab()` - Load ratings when switching to ratings tab

#### HTML Template (`chat.html`)
- **Ratings Tab**: Container for community ratings display
- **Dynamic Content**: JavaScript populates rating items
- **Responsive Design**: Works on desktop and mobile

#### CSS Styling (`_tracks-favorites.css`)
- **High Contrast Dropdowns**: Dark background, white text for readability
- **Rating Item Layout**: Flexbox layout for track info and rating details
- **Modal Animations**: Smooth transitions for any popup content

## Database Schema

### Rating Table
```sql
CREATE TABLE rating (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    audiofile_id BIGINT NOT NULL,
    rating INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (audiofile_id) REFERENCES audio_file(id),
    UNIQUE(user_id, audiofile_id)  -- One rating per user per track
);
```

### Key Relationships
- **User ↔ Rating**: One-to-Many (user can rate multiple tracks)
- **AudioFile ↔ Rating**: One-to-Many (track can have multiple ratings)
- **Unique Constraint**: One rating per user per track

## Security Features

### Authentication Requirements
- **Login Check**: All rating operations require authentication
- **Session Validation**: Spring Security validates user sessions
- **Anonymous Access**: Can view ratings but cannot create/edit
- **Multi-User Support**: Each user maintains their own independent rating history
- **Database Authentication**: Uses secure database-backed user accounts

### Authorization Controls
- **Ownership Verification**: Users can only edit/delete their own ratings
- **Backend Validation**: Double-checks user ownership on every edit/delete
- **Frontend Filtering**: Edit controls only show for user's own ratings

### Input Validation
- **Rating Range**: 1-5 stars only
- **Audio File Validation**: Ensures rated track exists
- **SQL Injection Prevention**: Parameterized queries via JPA

## Performance Optimizations

### Frontend
- **Auto-refresh**: Updates every 30 seconds only when ratings tab is active
- **Efficient DOM Updates**: Removes and recreates rating list for consistency
- **Event Delegation**: Single event listener per container

### Backend
- **JPA Relationships**: Efficient joins for rating queries
- **Indexed Queries**: Database indexes on user_id and audiofile_id
- **Pagination Ready**: Structure supports future pagination implementation

## Troubleshooting

### Common Issues

#### Edit Buttons Not Showing
- **Cause**: User not logged in or session expired
- **Solution**: Login with username `user` and password `password`

#### Rating Changes Not Saving
- **Cause**: Authentication failure or network error
- **Solution**: Check network connection and re-login if needed

#### Page Not Updating
- **Cause**: JavaScript error or tab switching issue
- **Solution**: Refresh page or check browser console for errors

### Debug Steps
1. **Check Authentication**: Verify user is logged in
2. **Browser Console**: Look for JavaScript errors
3. **Network Tab**: Check API calls are succeeding
4. **Database**: Verify ratings are being saved in H2 console

## Related Documentation
- [Project Structure](PROJECT_STRUCTURE.md) - Overall application architecture
- [Database Documentation](DATABASE.md) - Database schema and configuration
- [Setup Instructions](SETUP.md) - How to run the application