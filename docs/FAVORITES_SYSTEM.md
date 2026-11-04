# Favorites System Documentation

## Overview
The favorites system allows users to mark tracks and artists as favorites, providing quick access to preferred music through a dedicated favorites tab.

## Features

### Track Favorites
- **Heart Icon Toggle**: Click ♥ to favorite/unfavorite tracks
- **Visual Feedback**: Red heart for favorited, gray for unfavorited
- **Instant Updates**: Changes save immediately
- **Cross-Platform**: Works with both iTunes and uploaded tracks

### Favorites Display
- **Dedicated Tab**: Separate "Favorites" tab showing all favorited items
- **Track Information**: Track name, artist, and audio player
- **Action Buttons**: Remove, rate, and add to playlist
- **Live Updates**: Favorites refresh automatically

### Artist Favorites
- **Artist-Level Favoriting**: Mark artists as favorites

## User Interface

### Heart Icon States
- **Unfavorited**: ♡ (gray outline heart)
- **Favorited**: ♥ (red filled heart)

## Technical Implementation

### Backend Components

#### Favorite Entity (`Favorite.java`)
```java
@Entity
@Table(name = "favorite")
public class Favorite {
    private Long id;
    private User user;           // Who favorited
    private AudioFile audioFile; // What was favorited (for track favorites)
    private String artistName;   // Artist name (for artist favorites)
    private FavoriteType type;   // TRACK or ARTIST
    private LocalDateTime createdAt;
}
```

#### Favorite Service (`FavoriteService.java`)
- `toggleTrackFavorite()` - Add or remove track favorite
- `toggleArtistFavorite()` - Add or remove artist favorite
- `getAllFavoritesByUser()` - Get all favorites for user
- `isTrackFavorited()` - Check if track is favorited
- `isArtistFavorited()` - Check if artist is favorited

### Frontend Components

#### JavaScript (`favorites.js`)
- `handleFavoriteToggles()` - Handle heart icon clicks
- `favoriteToggleHandler()` - Process favorite toggle requests
- `addToFavoritesTab()` - Add track to favorites display
- `removeFromFavoritesTab()` - Remove track from favorites display

#### HTML Template (`chat.html`)
- **Favorites Tab**: Container for favorites display
- **Heart Icons**: Embedded in track cards
- **Track Cards**: Consistent layout with other tabs

#### CSS Styling (`_tracks-favorites.css`)
- **Heart Icon Styling**: Red/gray color states
- **Track Layout**: Consistent with other track displays

## Database Schema

### Favorite Table
```sql
CREATE TABLE favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    audiofile_id BIGINT NOT NULL,
    artist_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (audiofile_id) REFERENCES audio_file(id),
    UNIQUE(user_id, audiofile_id)  -- One favorite per user per track
);
```

### Key Relationships
- **User ↔ Favorite**: One-to-Many (user can have multiple favorites)
- **AudioFile ↔ Favorite**: One-to-Many (track can be favorited by multiple users)
- **Unique Constraint**: One favorite per user per track

## Security Features

### Authentication Requirements
- **Login Required**: Must be authenticated to favorite tracks
- **User Ownership**: Users can only manage their own favorites
- **Session Validation**: Spring Security validates user sessions
- **Multi-User Isolation**: Each registered user maintains completely separate favorite lists
- **Database Authentication**: Uses database-backed user accounts with BCrypt encryption

### Data Protection
- **User Isolation**: Users only see their own favorites
- **Input Validation**: Validate track existence before favoriting
- **SQL Injection Prevention**: Parameterized queries via JPA

## Usage Instructions

### Adding Favorites
1. Find a track (search results or uploaded files)
2. Click the ♡ (gray heart) icon
3. Heart turns red ♥ and track is favorited
4. Track appears in Favorites tab

### Removing Favorites
1. Go to Favorites tab or find track elsewhere
2. Click the ♥ (red heart) icon
3. Heart turns gray ♡ and track is unfavorited
4. Track disappears from Favorites tab

### Viewing Favorites
1. Switch to "Favorites" tab
2. Browse all your favorited tracks
3. Play tracks using embedded audio players

## Troubleshooting

### Common Issues

#### Heart Icon Not Changing
- **Cause**: JavaScript error or authentication issue
- **Solution**: Check login status and refresh page

#### Favorites Not Saving
- **Cause**: Network error or session timeout
- **Solution**: Re-login and try again

#### Favorites Tab Empty
- **Cause**: No favorites added or loading error
- **Solution**: Add some favorites or refresh page

### Debug Steps
1. **Check Authentication**: Verify user is logged in
2. **Browser Console**: Look for JavaScript errors
3. **Network Tab**: Check API calls are working
4. **Test Toggle**: Try favoriting/unfavoriting a track

## Related Documentation
- [Music Search](MUSIC_SEARCH.md) - Finding tracks to favorite
- [Audio Upload Feature](AUDIO_UPLOAD_FEATURE.md) - Favoriting uploaded files
- [Rating System](RATING_SYSTEM.md) - Rating favorite tracks
- [Playlist System](PLAYLIST_SYSTEM.md) - Adding favorites to playlists