# Chat System Documentation

## Overview
The chat system provides real-time messaging functionality where users can send text messages and share audio links that automatically embed as playable audio players.

## Features

### Real-time Messaging
- **Instant Messages**: Send and receive messages with timestamps
- **Message History**: View previous chat messages in chronological order
- **User Attribution**: Messages show the sender's username

### Audio Link Detection
- **iTunes URLs**: Automatically detects and embeds iTunes preview links
- **Uploaded Audio**: Embeds audio players for uploaded files shared in chat
- **Smart Detection**: Recognizes audio URLs and creates playable audio elements

### Authentication & Multi-User Support
- **Database Authentication**: Uses Spring Security with database-backed user accounts
- **User Registration**: New users can register accounts via `/register`
- **User Isolation**: Each user sees the same shared chat (messages from all users)
- **Default User**: Username `user`, Password `password` (created automatically)
- **Session Management**: Spring Security handles user sessions and authentication

### Message Display
- **Timestamps**: Each message shows when it was sent
- **Username Display**: Clear identification of message senders
- **Audio Players**: Embedded audio controls for shared music

## User Interface

### Chat Input
- **Text Area**: Multi-line input for typing messages
- **Send Button**: Click to send message to chat
- **Enter Key**: Press Enter to send (Shift+Enter for new line)

### Message Format
```
[Timestamp] Username: Message content
[Audio Player] (for audio links)
```

### Chat Display
- **Scrollable Area**: View message history
- **Auto-scroll**: New messages automatically scroll into view
- **Audio Controls**: Play/pause buttons for embedded audio

## Technical Implementation

### Backend Components

#### Message Entity (`Message.java`)
```java
@Entity
public class Message {
    private Long id;
    private String content;      // Message text
    private String sender;       // Username of sender
    private LocalDateTime createdAt;
}
```

#### Chat Controller (`ChatController.java`)
- **POST** `/api/chat` - Send new message
- **GET** `/api/chat` - Get message history

#### Implementation Note
The chat functionality operates directly through the `ChatController` which uses `MessageRepository` and `UserRepository` for data access. Business logic is handled within the controller methods for streamlined operation.

### Frontend Components

#### JavaScript (`chat-module.js`)
- `processMessageContent()` - Process messages for audio URLs
- `processChatMessages()` - Process and display chat messages
- `checkForNewMessages()` - Check for new messages
- `initializeChat()` - Initialize chat functionality
- `enhanceChatMessages()` - Add enhancements to messages

#### HTML Template (`chat.html`)
- **Chat Tab**: Container for chat interface
- **Message Input**: Text area and send button
- **Message Display**: Scrollable message history area

#### CSS Styling (`_chat.css`)
- **Message Layout**: Flexbox layout for messages
- **Input Styling**: Styled text area and send button
- **Scroll Behavior**: Smooth scrolling for message area

## Database Schema

### Message Table
```sql
CREATE TABLE message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    sender VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## Troubleshooting

### Common Issues

#### Messages Not Appearing
- **Cause**: Network connection or server error
- **Solution**: Refresh page and try again

#### Can't Send Messages
- **Cause**: Empty message content
- **Solution**: Type message content before sending

### Debug Steps
1. **Check Network**: Verify internet connection
2. **Browser Console**: Look for JavaScript errors
3. **Refresh Page**: Reload to reset chat state

## Related Documentation
- [Audio Upload Feature](AUDIO_UPLOAD_FEATURE.md) - Audio file sharing
- [Project Structure](PROJECT_STRUCTURE.md) - Overall application architecture
- [Setup Instructions](SETUP.md) - How to run the application