# Music Search & iTunes Integration Documentation

## Overview
The music search system integrates with the iTunes API to allow users to search for songs, albums, and artists, then interact with the results through favorites, playlists, and ratings.

## Features

### iTunes Search
- **Song Search**: Find tracks by title or artist name
- **Form-based Search**: Click "Search" button to get results
- **Preview Audio**: Built-in audio players for 30-second previews

### Track Interactions
- **Add to Favorites**: Mark tracks as favorites with heart icon
- **Add to Playlists**: Select playlists to add tracks to
- **Rate Tracks**: Give 1-5 star ratings to songs
- **Share to Chat**: Send track links to chat with embedded players

### Search Results Display
- **Action Buttons**: Quick access to favorites, playlists, and rating
- **Audio Controls**: Play/pause buttons for preview audio

### Action Buttons
- **Favorites**: ♥/♡ toggle button ("Fav"/"Unfav")
- **Playlists**: "➕ Add to Playlist" button opens playlist modal
- **Rating**: ★ dropdown for 1-5 star rating with "Rate" button
- **Chat**: "💬 Send" button to share in chat
- **Audio**: ▶️ play button for preview

## Technical Implementation

### Frontend Components

#### JavaScript (`music-search.js`)
- `handleMusicSearch()` - Handle search form submission
- Form submission to `/ui/chat/search-music` endpoint
- Result processing and display
- Re-attach event listeners after content update

#### iTunes API Integration
- **Search Endpoint**: `https://itunes.apple.com/search`
- **Parameters**: term, media=music, limit
- **Response**: JSON with track metadata and preview URLs

#### HTML Template (`chat.html`)
- **Music Tab**: Container for search and results
- **Search Input**: Text field for search queries
- **Results Area**: Dynamic container for track cards

#### CSS Styling (`_music.css`)
- **Track Card Layout**: Grid layout for track information
- **Action Button Styling**: Consistent button appearance
- **Responsive Design**: Mobile-friendly track cards

## Usage Instructions

### Searching for Music
1. Switch to "Search" tab (the default active tab)
2. Type song or artist name in the search box
3. Click "Search" button to get results
4. Scroll through results to find desired tracks

### Interacting with Tracks
1. **Favorite**: Click ♥ to add to favorites
2. **Playlist**: Click 📋 to select playlist for adding
3. **Rate**: Click ★ and select 1-5 stars
4. **Share**: Click 💬 to send to chat
5. **Preview**: Click ▶️ to play 30-second preview

### Playing Previews
1. Click play button on any track card
2. 30-second preview plays through iTunes
3. Click pause to stop playback

## Troubleshooting

### Common Issues

#### No Search Results
- **Cause**: iTunes API unavailable or network issues
- **Solution**: Check internet connection and try again

#### Audio Previews Not Playing
- **Cause**: iTunes preview URL unavailable or browser restrictions
- **Solution**: Try different browser or check browser audio settings

### Debug Steps
1. **Check Network**: Verify internet connection to iTunes API
2. **Browser Console**: Look for CORS or network errors
3. **Test Different Terms**: Try simpler search queries

## Limitations

### iTunes API Restrictions
- **Preview Length**: Limited to 30-second previews
- **Availability**: Some tracks may not have previews
- **Regional**: Results vary by country/region
- **Rate Limits**: Too many requests may be throttled

## Related Documentation
- [Playlist Management](PLAYLIST_SYSTEM.md) - Adding tracks to playlists
- [Favorites System](FAVORITES_SYSTEM.md) - Managing favorite tracks
- [Rating System](RATING_SYSTEM.md) - Rating tracks and albums
- [Chat System](CHAT_SYSTEM.md) - Sharing tracks in chat