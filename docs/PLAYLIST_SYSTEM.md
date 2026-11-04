# Playlist Management System Documentation

## Overview
The playlist system allows users to create, manage, and organize custom playlists of tracks from iTunes search results and uploaded audio files.

## Features

### Playlist Creation
- **Create New Playlists**: Add playlists with custom names
- **Public/Private Settings**: Control playlist visibility
- **Track Addition**: Add tracks from search results or uploads
- **Playlist Organization**: Manage multiple playlists per user

### Playlist Management
- **View Playlists**: Browse all your created playlists
- **Delete Playlists**: Remove unwanted playlists
- **Track Removal**: Remove tracks from playlists

### Track Integration
- **iTunes Tracks**: Add tracks from music search results
- **Uploaded Audio**: Include uploaded audio files
- **Mixed Content**: Combine both iTunes and uploaded tracks

## User Interface

### Playlist Display
- **Playlist Cards**: Visual cards for each playlist
- **Track Count**: Shows number of tracks in playlist
- **Privacy Indicator**: Shows if playlist is public/private
- **Action Buttons**: Delete, and view options

### Track Addition
- **Add Button**: 📋 icon on track cards
- **Instant Addition**: Tracks added immediately to selected playlist
- **Confirmation**: Visual feedback when track is added

## Technical Implementation

### Backend Components

#### Playlist Entity (`Playlist.java`)
```java
@Entity
@Table(name = "playlist")
public class Playlist {
    private Long id;
    private String name;                    // Playlist name
    private String description;             // Optional description (not used in front end)
    private boolean isPublic;               // Visibility setting
    private User owner;                     // Playlist owner
    private List<AudioFile> audioFiles;     // Tracks in playlist
    private LocalDateTime createdAt;        // Creation timestamp
    private LocalDateTime updatedAt;        // Last update timestamp
}
```
## Database Schema

### Playlist Table
```sql
CREATE TABLE playlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    is_public BOOLEAN DEFAULT false,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);
```

### Playlist-AudioFile Relationship
```sql
CREATE TABLE playlist_audiofile (
    playlist_id BIGINT NOT NULL,
    audiofile_id BIGINT NOT NULL,
    PRIMARY KEY (playlist_id, audiofile_id),
    FOREIGN KEY (playlist_id) REFERENCES playlist(id),
    FOREIGN KEY (audiofile_id) REFERENCES audio_file(id)
);
```

## Security Features

### Authentication Requirements
- **Login Required**: Must be authenticated to create/manage playlists
- **User Ownership**: Users can only modify their own playlists
- **Session Validation**: Spring Security validates user sessions
- **Multi-User Isolation**: Each user has completely separate playlist collections
- **Database Authentication**: Uses database-backed user accounts with secure password storage

### Privacy Controls
- **Public/Private**: Control playlist visibility
- **Owner-only Editing**: Only playlist owner can modify
- **View Permissions**: Public playlists visible to all users

## Frontend Implementation

### User Interface Components
- **Playlist Tab**: Dedicated tab interface in the music section
- **Creation Form**: Input fields for new playlist creation with instant feedback
- **Playlist Cards**: Visual representation of playlists with metadata display
- **Selection Modal**: Dynamic dropdown for playlist selection when adding tracks
- **Action Buttons**: Consistent button styling across playlist management features

## Usage Instructions

### Creating Playlists
1. Switch to "Playlists" tab in the music section
2. In the "Create New Playlist" section, enter playlist name
3. Click "Create" button to create empty playlist
4. Navigate back to search/favorites to add tracks to the new playlist

### Adding to Existing Playlists
1. Click 📋 button on any track
2. Select existing playlist from dropdown modal
3. Click "Add to Playlist" to confirm
4. Track is instantly added to selected playlist

### Managing Playlists
1. Switch to playlists view (if available)
2. View all your created playlists
3. Edit playlist privacy settings
4. Delete unwanted playlists
5. Remove individual tracks from playlists

## Troubleshooting

### Common Issues

#### Playlist Modal Not Opening
- **Cause**: JavaScript error or authentication issue
- **Solution**: Ensure you're logged in and refresh page

#### Can't Create Playlist
- **Cause**: Empty playlist name or authentication failure
- **Solution**: Enter valid playlist name and check login status

#### Tracks Not Adding
- **Cause**: Network error or server issue
- **Solution**: Check internet connection and try again

### Debug Steps
1. **Check Authentication**: Verify user is logged in
2. **Browser Console**: Look for JavaScript errors
3. **Network Tab**: Check API calls are succeeding
4. **Refresh Page**: Reset application state

## Related Documentation
- [Music Search](MUSIC_SEARCH.md) - Finding tracks to add to playlists
- [Audio Upload Feature](AUDIO_UPLOAD_FEATURE.md) - Adding uploaded files to playlists
- [User Authentication](AUTHENTICATION.md) - Login requirements for playlists
- [Project Structure](PROJECT_STRUCTURE.md) - Overall application architecture