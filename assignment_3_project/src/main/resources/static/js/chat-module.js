// Chat functionality module
// Handles message display, live updates, content processing, and chat form interactions

// Helper function to escape HTML
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Process individual message content for audio URLs
function processMessageContent(messageContent) {
    const content = messageContent.textContent || messageContent.innerText;
    
    // Check if content starts with [audio: and contains an audio URL
    if (content.includes('[audio:') && content.includes(']')) {
        // Extract the audio URL and track info - handle both absolute and relative URLs
        const audioMatch = content.match(/\[audio:\s*([^\s\]]+)\]\s*(.+)/i);
        if (audioMatch) {
            const audioUrl = audioMatch[1];
            const trackInfo = audioMatch[2].trim();
            
            // Try to extract track name and artist from the track info
            // Formats like "Track Name by Artist Name" or just "Track Name"
            let trackName = trackInfo;
            let artistName = 'Unknown Artist';
            
            const byMatch = trackInfo.match(/^(.+?)\s+by\s+(.+)$/i);
            if (byMatch) {
                trackName = byMatch[1].trim();
                artistName = byMatch[2].trim();
            }
            
            // Generate a unique identifier for this audio in chat
            const chatAudioId = `chat-audio-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
            
            // Create audio element with track info and rating functionality
            const audioHTML = `
                <div class="chat-audio" data-chat-audio-id="${chatAudioId}">
                    <audio src="${audioUrl}" controls class="music-embed"></audio>
                    <div class="track-info-with-rating">
                        <div class="track-info">
                            <span class="track-title">${escapeHtml(trackName)}</span>
                            ${artistName !== 'Unknown Artist' ? `<span class="track-artist">by ${escapeHtml(artistName)}</span>` : ''}
                        </div>
                        <div class="rating-form-wrapper">
                            <div class="rating-group">
                                <select class="rating-select" 
                                    data-track-name="${escapeHtml(trackName)}"
                                    data-artist-name="${escapeHtml(artistName)}"
                                    data-preview-url="${escapeHtml(audioUrl)}">
                                    <option value="">Rate</option>
                                    <option value="1">1★</option>
                                    <option value="2">2★</option>
                                    <option value="3">3★</option>
                                    <option value="4">4★</option>
                                    <option value="5">5★</option>
                                </select>
                                <button type="button" class="btn btn-sm btn-outline-warning rate-btn" disabled>Rate</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            messageContent.innerHTML = audioHTML;
        }
    } else {
        // Check for plain audio URLs in the content and convert them to audio elements
        const audioUrlPattern = /(https?:\/\/[^\s]+\.(?:mp3|wav|m4a|aac)(?:\?[^\s]*)?)/gi;
        let newContent = content;
        const matches = content.match(audioUrlPattern);
        
        if (matches) {
            matches.forEach(audioUrl => {
                // Generate a unique identifier for this audio in chat
                const chatAudioId = `chat-audio-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
                
                // Extract potential track name from URL (filename without extension)
                const urlParts = audioUrl.split('/');
                const filename = urlParts[urlParts.length - 1];
                const trackName = filename.replace(/\.[^/.]+$/, "").replace(/[_-]/g, ' ') || 'Audio Track';
                
                const audioElement = `
                    <div class="chat-audio" data-chat-audio-id="${chatAudioId}">
                        <audio src="${audioUrl}" controls class="music-embed"></audio>
                        <div class="track-info-with-rating">
                            <div class="track-info">
                                <span class="track-title">${escapeHtml(trackName)}</span>
                            </div>
                            <div class="rating-form-wrapper">
                                <div class="rating-group">
                                    <select class="rating-select" 
                                        data-track-name="${escapeHtml(trackName)}"
                                        data-artist-name="Unknown Artist"
                                        data-preview-url="${escapeHtml(audioUrl)}">
                                        <option value="">Rate</option>
                                        <option value="1">1★</option>
                                        <option value="2">2★</option>
                                        <option value="3">3★</option>
                                        <option value="4">4★</option>
                                        <option value="5">5★</option>
                                    </select>
                                    <button type="button" class="btn btn-sm btn-outline-warning rate-btn" disabled>Rate</button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                newContent = newContent.replace(audioUrl, audioElement);
            });
            messageContent.innerHTML = newContent;
        }
    }
}

// Process chat messages to convert audio URLs to audio elements
function processChatMessages() {
    const messageContents = document.querySelectorAll('.message-content');
    messageContents.forEach(messageContent => {
        processMessageContent(messageContent);
    });
    
    // Also enhance the message for track interactions
    enhanceChatMessages();
}

// Check for new messages immediately (used after sending a message)
async function checkForNewMessages() {
    try {
        const response = await fetch(window.location.href);
        if (response.ok) {
            const html = await response.text();
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;
            
            // Get the new messages
            const newMessagesList = tempDiv.querySelector('#messages');
            const currentMessagesList = document.querySelector('#messages');
            
            if (newMessagesList && currentMessagesList) {
                // Get current and new message items
                const currentMessages = currentMessagesList.querySelectorAll('.message-item');
                const newMessages = newMessagesList.querySelectorAll('.message-item');
                
                // If we have more messages in the new list, append the new ones
                if (newMessages.length > currentMessages.length) {
                    // Extract only the new messages (those beyond our current count)
                    for (let i = currentMessages.length; i < newMessages.length; i++) {
                        const newMessage = newMessages[i].cloneNode(true);
                        currentMessagesList.appendChild(newMessage);
                    }
                    
                    // Process any new audio content in the newly added messages
                    const newlyAdded = Array.from(currentMessagesList.children).slice(currentMessages.length);
                    newlyAdded.forEach(messageItem => {
                        const messageContent = messageItem.querySelector('.message-content');
                        if (messageContent) {
                            processMessageContent(messageContent);
                        }
                    });
                    
                    // Re-attach event handlers for shared content (playlists) and enhanced messages
                    handleSharedContentInChat();
                    enhanceChatMessages();
                    
                    // Scroll to bottom to show new messages
                    Utils.scrollChatToBottom();
                }
            }
        }
    } catch (error) {
        console.error('Error checking for new messages:', error);
    }
}

// Start live message updates
function startLiveMessageUpdates() {
    // Check for new messages every 3 seconds
    setInterval(() => {
        checkForNewMessages();
    }, 3000);
}

// Handle shared content interactions in chat
function handleSharedContentInChat() {
    // Handle view shared playlist buttons
    document.querySelectorAll('.view-shared-playlist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            await showSharedPlaylistModal(playlistId);
        });
    });
    
    // Handle copy shared playlist buttons
    document.querySelectorAll('.copy-shared-playlist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            await copySharedPlaylist(playlistId);
        });
    });
}

// Show modal with shared playlist tracks
async function showSharedPlaylistModal(playlistId) {
    try {
        const response = await fetch(`/api/playlists/${playlistId}/tracks`);
        if (response.ok) {
            const tracks = await response.json();
            const playlistResponse = await fetch(`/api/playlists/${playlistId}`);
            const playlist = playlistResponse.ok ? await playlistResponse.json() : {name: 'Playlist'};
            
            // Create a simple modal for viewing tracks
            const modal = document.createElement('div');
            modal.className = 'playlist-modal';
            modal.style.display = 'flex';
            modal.style.alignItems = 'center';
            modal.style.justifyContent = 'center';
            
            let tracksHtml = '';
            if (tracks.length === 0) {
                tracksHtml = '<div class="empty-playlist">This playlist is empty.</div>';
            } else {
                tracks.forEach(track => {
                    tracksHtml += `
                        <div class="playlist-track">
                            <div class="track-info">
                                <span class="track-name">${track.trackName}</span>
                                <span class="track-artist">by ${track.artistName}</span>
                            </div>
                            <div class="track-actions">
                                ${track.previewUrl ? `
                                    <audio controls preload="none" class="audio-preview">
                                        <source src="${track.previewUrl}" type="audio/mp4">
                                        Your browser does not support the audio element.
                                    </audio>
                                ` : ''}
                            </div>
                        </div>
                    `;
                });
            }
            
            modal.innerHTML = `
                <div class="playlist-modal-content">
                    <div class="playlist-modal-header">
                        <h4 class="playlist-modal-title">${playlist.name} (${tracks.length} tracks)</h4>
                        <button type="button" class="playlist-modal-close" aria-label="Close modal">&times;</button>
                    </div>
                    <div class="modal-body">
                        ${tracksHtml}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary modal-cancel">Close</button>
                    </div>
                </div>
            `;
            
            document.body.appendChild(modal);
            
            // Add close handlers
            modal.querySelector('.playlist-modal-close').addEventListener('click', () => {
                document.body.removeChild(modal);
            });
            modal.querySelector('.modal-cancel').addEventListener('click', () => {
                document.body.removeChild(modal);
            });
            modal.addEventListener('click', (e) => {
                if (e.target === modal) document.body.removeChild(modal);
            });
            
        } else {
            Utils.showNotification('Could not load playlist tracks', 'error');
        }
    } catch (error) {
        console.error('Error showing shared playlist:', error);
        Utils.showNotification('Error loading playlist', 'error');
    }
}

// Copy shared playlist to user's playlists
async function copySharedPlaylist(playlistId) {
    const newName = prompt('Enter a name for the copied playlist:');
    if (!newName) return;
    
    try {
        const response = await fetch('/ui/chat/copy-playlist', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `sourcePlaylistId=${playlistId}&newName=${encodeURIComponent(newName)}`
        });
        
        if (response.ok) {
            const result = await response.json();
            if (result.success) {
                Utils.showNotification(result.message, 'success');
                // Refresh the playlists tab to show the new copied playlist
                if (typeof PlaylistManager !== 'undefined' && PlaylistManager.refreshUserPlaylists) {
                    await PlaylistManager.refreshUserPlaylists();
                }
            } else {
                Utils.showNotification(result.error || 'Failed to copy playlist', 'error');
            }
        } else {
            const errorText = await response.text();
            Utils.showNotification(`Failed to copy playlist: ${response.status}`, 'error');
        }
    } catch (error) {
        console.error('Error copying playlist:', error);
        Utils.showNotification('Network error while copying playlist. Please try again.', 'error');
    }
}

// Enhance chat messages with action buttons for tracks
function enhanceChatMessages() {
    document.querySelectorAll('.message-content').forEach(messageContent => {
        // Skip if this message already has audio content or shared track content
        if (messageContent.querySelector('.chat-audio') || messageContent.querySelector('.shared-track')) {
            return;
        }
        
        const content = messageContent.textContent || messageContent.innerText;
        
        // Look for track patterns like "Track Name by Artist Name"
        const trackPattern = /^(.+) by (.+)$/;
        const match = content.match(trackPattern);
        
        if (match) {
            const trackName = match[1].trim();
            const artistName = match[2].trim();
            
            // Replace with enhanced track display using the new rating format
            messageContent.innerHTML = `
                <div class="shared-track">
                    <div class="shared-track-info">${escapeHtml(trackName)} by ${escapeHtml(artistName)}</div>
                    <div class="shared-track-actions">
                        <button class="btn btn-sm btn-outline-warning favorite-toggle-btn"
                                data-track-name="${escapeHtml(trackName)}" data-artist-name="${escapeHtml(artistName)}" data-is-favorited="false">
                            ♡ Favorite
                        </button>
                        <button class="btn btn-sm btn-outline-primary add-to-playlist-btn"
                                data-track-name="${escapeHtml(trackName)}" data-artist-name="${escapeHtml(artistName)}" data-preview-url="" data-itunes-track-id="">
                            Add to Playlist
                        </button>
                        <div class="rating-form-wrapper">
                            <div class="rating-group">
                                <select class="rating-select" 
                                    data-track-name="${escapeHtml(trackName)}"
                                    data-artist-name="${escapeHtml(artistName)}">
                                    <option value="">Rate</option>
                                    <option value="1">1★</option>
                                    <option value="2">2★</option>
                                    <option value="3">3★</option>
                                    <option value="4">4★</option>
                                    <option value="5">5★</option>
                                </select>
                                <button type="button" class="btn btn-sm btn-outline-warning rate-btn" disabled>Rate</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            // Reattach handlers for new buttons (these will be handled by other modules)
            if (typeof Favorites !== 'undefined' && Favorites.handleFavoriteToggles) {
                Favorites.handleFavoriteToggles();
            }
            if (typeof PlaylistUI !== 'undefined' && PlaylistUI.handleAddToPlaylist) {
                PlaylistUI.handleAddToPlaylist();
            }
        }
    });
}

// Initialize chat form handling
function initializeChatForm() {
    const chatForm = document.querySelector('.chat-input-form');
    const chatInput = document.querySelector('#chat-input');
    if (chatForm && chatInput) {
        chatForm.addEventListener('submit', async function(e) {
            e.preventDefault(); // Prevent page reload
            
            const content = chatInput.value.trim();
            if (!content) return;
            
            // Show sending state
            const submitButton = chatForm.querySelector('button[type="submit"]');
            const originalText = submitButton.textContent;
            submitButton.textContent = 'Sending...';
            submitButton.disabled = true;
            
            // Clear input immediately for better UX
            chatInput.value = '';
            
            try {
                const formData = new FormData();
                formData.append('content', content);
                
                const response = await fetch('/ui/chat', {
                    method: 'POST',
                    body: formData
                });
                
                if (response.ok) {
                    // Message sent successfully - the live update system will pick it up
                    // Force an immediate check for new messages
                    setTimeout(checkForNewMessages, 500);
                } else {
                    // Restore message on error
                    chatInput.value = content;
                    alert('Failed to send message. Please try again.');
                }
            } catch (error) {
                console.error('Error sending message:', error);
                chatInput.value = content; // Restore message
                alert('Connection error. Please try again.');
            } finally {
                // Restore button state
                submitButton.textContent = originalText;
                submitButton.disabled = false;
                chatInput.focus(); // Keep focus for continuous typing
            }
        });
    }
}

// Initialize chat functionality
function initializeChat() {
    processChatMessages();
    Utils.scrollChatToBottom();
    startLiveMessageUpdates();
    initializeChatForm();
    handleSharedContentInChat();
    enhanceChatMessages();
}

// Export functions for use by other modules
window.Chat = {
    initializeChat,
    checkForNewMessages,
    processChatMessages,
    enhanceChatMessages,
    handleSharedContentInChat
};