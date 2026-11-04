# Audio Upload Feature Documentation

## Overview
The audio upload feature allows users to upload their own audio files to the music chat application with full interactive capabilities. This comprehensive feature includes a modern drag-and-drop interface, progress tracking, and complete integration with all existing music system features including favorites, playlists, ratings, and chat sharing.

## Features

### 1. Upload Interface
- **New Upload Tab**: Added a dedicated "Upload" tab to the music section
- **Drag & Drop**: Users can drag audio files directly onto the upload area
- **File Browser**: Click the upload area to browse and select files
- **Visual Feedback**: Hover effects and drag-over animations
- **Progress Tracking**: Real-time upload progress with visual progress bar

### 2. File Validation
- **Supported Formats**: MP3, WAV, OGG, AAC, M4A, WebM, FLAC
- **File Size Limit**: 50MB maximum file size
- **Type Validation**: Server-side and client-side file type checking
- **Error Handling**: Clear error messages for invalid files

### 3. Uploaded Files Management
- **File List**: View all uploaded audio files with metadata
- **Audio Preview**: Built-in audio player for each uploaded file
- **File Information**: Display filename, upload date, file size, and content type
- **Interactive Features**: Full track-like functionality for uploaded files

### 4. Interactive Features (New!)
#### Primary Actions
- **💬 Send to Chat**: Send uploaded files to chat with embedded audio players
- **➕ Add to Playlist**: Complete playlist integration with modal selection

#### Secondary Actions  
- **♡/♥ Favorites**: Toggle favorite status with visual feedback
- **⭐ Ratings**: Rate files 1-5 stars using dropdown selection
- **🗑️ Delete**: Remove files with confirmation dialog and cleanup

#### Smart Integration
- **Favorites Display**: Uploaded files appear in favorites tab with proper naming
- **User Attribution**: Shows uploader username for uploaded files in favorites
- **Dual Audio Support**: Supports both iTunes preview URLs and uploaded file streams
- **Consistent Styling**: All buttons match search results styling exactly

### 5. User Experience
- **Responsive Design**: Works on desktop and mobile devices
- **Visual Feedback**: Success animations and status messages for all actions
- **Accessibility**: ARIA labels and keyboard navigation support
- **Error Recovery**: Clear error messages and retry functionality
- **State Persistence**: Favorites and ratings persist across page reloads
- **Modal Integration**: Reuses existing playlist modal for seamless UX

## Technical Implementation

### Frontend Components
1. **HTML Template**: Enhanced upload tab in `chat.html` with full action buttons
2. **CSS Styling**: Comprehensive `_upload.css` with track-like styling
3. **JavaScript Module**: Advanced `audio-upload.js` with full feature support
4. **Tab Integration**: Updated `utils.js` to handle the new upload tab
5. **Global Styling**: Leverages `_buttons.css` for consistent button appearance

### Backend Integration
#### Existing API Endpoints
- `/api/audio/upload` - File upload endpoint
- `/api/audio/list` - List user's uploaded files  
- `/api/audio/stream/{id}` - Stream uploaded audio files

#### Interactive Feature APIs
- `/api/favorites/toggle` - Toggle favorite status for uploaded files
- `/api/ratings` - Rate uploaded files (1-5 stars)
- `/api/playlists/my` - Get user playlists for selection
- `/api/playlists/{id}/tracks/{fileId}` - Add uploaded files to playlists
- `/api/audio/{id}` (DELETE) - Delete uploaded files with cleanup

### Enhanced Features
#### Favorites Integration
- Shows uploaded filename when trackName is null
- Shows uploader username when artistName is null  
- Supports both iTunes tracks and uploaded files in same interface
- Proper audio player for uploaded files in favorites tab

#### File Management
- Complete CRUD operations (Create, Read, Update, Delete)
- Physical file cleanup on deletion
- User ownership validation for security
- File metadata preservation and display

### File Structure
```
src/main/resources/
├── templates/
│   └── chat.html (updated with upload tab)
└── static/
    ├── css/
    │   ├── styles.css (updated imports)
    │   └── _upload.css (new upload styles)
    └── js/
        ├── audio-upload.js (new upload functionality)
        └── utils.js (updated tab switching)
```

## Usage Instructions

### For Users
1. Navigate to the Music Hub section
2. Click on the "Upload" tab
3. Drag and drop an audio file onto the upload area, or click to browse
4. Watch the upload progress
5. Once uploaded, view your files in the "My Uploaded Files" section
6. Use the "Send" button to share files in chat
7. Use the "Playlist" button to add files to playlists (coming soon)

### For Developers
The upload system is modular and can be extended:
- Add new file validation rules in `isAudioFile()` method
- Extend file actions in `attachFileItemListeners()` method
- Customize upload progress in `uploadWithProgress()` method
- Add new file management features in the `createFileItemHtml()` method

## Security Considerations
- File type validation on both client and server side
- File size limits to prevent abuse
- User authentication required for uploads
- Files are stored securely on the server
- Content type validation prevents malicious uploads

## Future Enhancements
1. **Delete Functionality**: Add ability to delete uploaded files
2. **Playlist Integration**: Complete integration with playlist system
3. **Metadata Extraction**: Extract and display audio metadata (title, artist, duration)
4. **Batch Upload**: Support for uploading multiple files at once
5. **Cloud Storage**: Integration with cloud storage services
6. **Audio Processing**: Server-side audio format conversion

## Troubleshooting

### Common Issues
1. **Upload fails**: Check file format and size limitations
2. **Files not appearing**: Refresh the uploaded files list
3. **Progress stuck**: Check network connection
4. **Audio not playing**: Verify browser audio support

### Error Messages
- "Please select a valid audio file": File type not supported
- "File size must be less than 50MB": File too large
- "Upload failed: Network error": Connection issues
- "Not authenticated": User needs to log in

## Testing
The upload feature has been designed to work with the existing backend infrastructure. Test cases should cover:
- File upload with various audio formats
- Drag and drop functionality
- Progress tracking
- Error handling
- Mobile responsiveness
- Integration with chat system