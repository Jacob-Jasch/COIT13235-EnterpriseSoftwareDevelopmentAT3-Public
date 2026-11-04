# JavaScript Modules - Refactored Structure

This directory contains the JavaScript modules for the application.

## Module Structure

### Core Modules

1. **`utils.js`** - Utility functions
   - Tab switching functionality
   - Enhanced notification system with close buttons and animations
   - Collapsible sections handling
   - Scroll utilities
   - Notification styles initialization

2. **`chat-module.js`** - Chat functionality
   - Message processing and display
   - Live message updates
   - Audio content processing
   - Chat form handling
   - Shared content interactions

3. **`music-search.js`** - Music search functionality
   - iTunes API search handling
   - Search form interactions
   - Artist action buttons (search, send to chat)
   - Search results processing

4. **`favorites.js`** - Favorites management
   - Track favorites (add/remove)
   - Artist favorites (add/remove)
   - Favorites tab management
   - Favorite button state handling

5. **`playlist-manager.js`** - Playlist business logic
   - Playlist CRUD operations
   - Public/private playlist management
   - Playlist sharing functionality
   - User playlist loading
   - Public playlist discovery

6. **`playlist-ui.js`** - Playlist UI components
   - Playlist modals and interactions
   - Add to playlist functionality
   - Playlist content display
   - Track management within playlists
   - Playlist button state management

7. **`rating.js`** - Rating system functionality
   - Star rating interactions
   - Rating submission and updates
   - Rating display management
   - User rating state handling

8. **`audio-upload.js`** - Audio file upload functionality
   - File upload form handling
   - Upload progress tracking
   - Audio file management
   - Upload validation and feedback

9. **`console-launcher.js`** - App console launcher
   - Console startup functionality
   - Loading state management
   - Error handling for console access
   - AJAX-based console initialization

10. **`main.js`** - Application coordinator
    - Module initialization orchestration
    - Global error handling
    - Application startup sequence

## Module Dependencies

The modules are designed with clear dependencies:

```
main.js (coordinates all modules)
├── utils.js (no dependencies)
├── chat-module.js (depends on: utils.js)
├── music-search.js (depends on: utils.js, chat-module.js, favorites.js, playlist-ui.js)
├── favorites.js (depends on: utils.js)
├── rating.js (depends on: utils.js)
├── audio-upload.js (depends on: utils.js)
├── console-launcher.js (depends on: utils.js)
├── playlist-manager.js (depends on: utils.js, chat-module.js, playlist-ui.js)
└── playlist-ui.js (depends on: utils.js, favorites.js, playlist-manager.js)
```
