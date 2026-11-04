// Playlist UI module
// Handles playlist modals, display interactions, and UI-specific functionality

// Handle playlist view toggle
function handlePlaylistToggles() {
    document.querySelectorAll('.toggle-playlist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            
            // Try both selectors - dynamic content first, then static
            let contents = document.querySelector(`#playlist-contents-${playlistId}`);
            if (!contents) {
                contents = document.querySelector(`[data-playlist-id="${playlistId}"].playlist-contents`);
            }
            
            const icon = this.querySelector('.toggle-icon');
            const textSpan = this.querySelector('.toggle-text');
            
            if (contents) {
                const isHidden = contents.style.display === 'none' || !contents.style.display;
                
                if (isHidden) {
                    // Load playlist contents if not loaded yet
                    const currentContent = contents.innerHTML.trim();
                    
                    if (currentContent === '' || currentContent.includes('<!-- Playlist contents will be loaded here -->')) {
                        await loadPlaylistContents(playlistId);
                    }
                    contents.style.display = 'block';
                    if (icon) icon.textContent = '▲';
                    if (textSpan) textSpan.textContent = ' Hide Tracks';
                } else {
                    contents.style.display = 'none';
                    if (icon) icon.textContent = '▼';
                    if (textSpan) textSpan.textContent = ' View Tracks';
                }
            } else {
                Utils.showNotification('Error: Could not find playlist contents', 'error');
            }
        });
    });
}

// Load playlist contents dynamically
async function loadPlaylistContents(playlistId) {
    try {
        const response = await fetch(`/api/playlists/${playlistId}/tracks`, {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            }
        });
        
        if (response.ok) {
            const tracks = await response.json();
            const contents = document.querySelector(`#playlist-contents-${playlistId}`);
            
            if (contents) {
                if (tracks.length === 0) {
                    contents.innerHTML = '<div class="empty-playlist">This playlist is empty. Add tracks from search results or favorites!</div>';
                } else {
                    let tracksHtml = '';
                    tracks.forEach(track => {
                        tracksHtml += `
                            <div class="playlist-track">
                                <div class="track-info">
                                    <span class="track-name">${track.trackName}</span>
                                    <span class="track-artist">by <span>${track.artistName}</span></span>
                                </div>
                                <div class="track-actions">
                                    ${track.previewUrl ? `
                                        <audio controls preload="none" class="audio-preview">
                                            <source src="${track.previewUrl}" type="audio/mp4">
                                            Your browser does not support the audio element.
                                        </audio>
                                    ` : ''}
                                    <div class="track-buttons">
                                        <button class="btn btn-sm btn-outline-warning favorite-toggle-btn"
                                                data-track-name="${track.trackName}" data-artist-name="${track.artistName}" data-is-favorited="false"
                                                aria-label="Toggle favorite">
                                            ♡ Fav
                                        </button>
                                        <form class="rating-form" style="display: inline-flex; align-items: center; gap: 4px;">
                                            <input type="hidden" name="trackName" value="${track.trackName}" />
                                            <input type="hidden" name="artistName" value="${track.artistName}" />
                                            <select name="rating" class="rating-select" style="font-size: 12px; padding: 2px;">
                                                <option value="1">1</option>
                                                <option value="2">2</option>
                                                <option value="3">3</option>
                                                <option value="4">4</option>
                                                <option value="5">5</option>
                                            </select>
                                            <button type="submit" class="btn btn-sm btn-outline-info rate-btn">Rate</button>
                                        </form>
                                        <button class="btn btn-sm btn-primary send-track-btn"
                                                data-track-name="${track.trackName}" data-artist-name="${track.artistName}" data-preview-url="${track.previewUrl || ''}"
                                                aria-label="Send to chat">
                                            Send
                                        </button>
                                        <button class="btn btn-sm btn-danger remove-from-playlist-btn"
                                                data-playlist-id="${playlistId}" data-audio-file-id="${track.id}"
                                                aria-label="Remove from playlist">
                                            Remove
                                        </button>
                                    </div>
                                </div>
                            </div>
                        `;
                    });
                    contents.innerHTML = tracksHtml;
                    
                    // Reattach event handlers for the new content
                    if (typeof Favorites !== 'undefined') {
                        Favorites.handleFavoriteToggles();
                    }
                    handleRemoveFromPlaylist();
                    handleSendTrackFromPlaylist();
                }
            }
        } else {
            // Show error message with retry option
            const contents = document.querySelector(`#playlist-contents-${playlistId}`);
            if (contents) {
                contents.innerHTML = `
                    <div class="error-message" style="text-align: center; padding: 20px; color: #f85149;">
                        <p>Failed to load playlist tracks (Error ${response.status})</p>
                        <button class="btn btn-sm btn-outline-primary retry-load-btn" 
                                onclick="loadPlaylistContents(${playlistId})">
                            Retry Loading
                        </button>
                    </div>
                `;
            }
            Utils.showNotification(`Error loading playlist: ${response.status}`, 'error');
        }
    } catch (error) {
        console.error('Error loading playlist contents:', error);
        Utils.showNotification('Error loading playlist contents', 'error');
    }
}

// Handle add to playlist buttons
function handleAddToPlaylist() {
    document.querySelectorAll('.add-to-playlist-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const trackName = this.dataset.trackName;
            const artistName = this.dataset.artistName;
            const previewUrl = this.dataset.previewUrl;
            let itunesTrackId = this.dataset.itunesTrackId;
            
            // Handle undefined or null iTunes track ID
            if (!itunesTrackId || itunesTrackId === 'undefined' || itunesTrackId === 'null') {
                // Generate a fallback iTunes track ID based on track info
                itunesTrackId = `generated-${btoa(trackName + '-' + artistName).replace(/[^a-zA-Z0-9]/g, '')}`;
            }
            
            showPlaylistModal(trackName, artistName, previewUrl, itunesTrackId);
        });
        
        // Check and update button state based on whether track is in playlists
        updatePlaylistButtonState(btn);
    });
}

// Update playlist button state based on whether track exists in playlists
async function updatePlaylistButtonState(button) {
    const trackName = button.dataset.trackName;
    const artistName = button.dataset.artistName;
    
    if (!trackName || !artistName) return;
    
    try {
        const response = await fetch(`/api/check-track-in-playlists?trackName=${encodeURIComponent(trackName)}&artistName=${encodeURIComponent(artistName)}`, {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            }
        });
        if (response.ok) {
            const result = await response.json();
            
            if (result.inPlaylists) {
                button.classList.remove('btn-success');
                button.classList.add('btn-warning');
                button.innerHTML = `✓ In ${result.playlists.length} playlist${result.playlists.length === 1 ? '' : 's'}`;
                button.setAttribute('title', `In playlists: ${result.playlists.join(', ')}`);
            } else {
                button.classList.remove('btn-warning');
                button.classList.add('btn-success');
                button.innerHTML = 'Add to Playlist';
                button.removeAttribute('title');
            }
        }
    } catch (error) {
        console.error('Error checking playlist state:', error);
    }
}

// Show playlist selection modal
async function showPlaylistModal(trackName, artistName, previewUrl, itunesTrackId) {
    const modal = document.getElementById('playlist-modal');
    const trackInfo = document.getElementById('modal-track-info');
    const playlistList = document.getElementById('modal-playlist-list');
    const noPlaylistsMsg = document.getElementById('modal-no-playlists');
    
    // Set track info
    trackInfo.textContent = `Add "${trackName}" by ${artistName} to playlist:`;
    
    try {
        // Fetch user's playlists
        const response = await fetch('/api/playlists/my', {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            }
        });
        if (response.ok) {
            const playlists = await response.json();
            
            if (playlists.length === 0) {
                playlistList.style.display = 'none';
                noPlaylistsMsg.style.display = 'block';
            } else {
                noPlaylistsMsg.style.display = 'none';
                playlistList.style.display = 'block';
                
                // Create custom dropdown using div elements (better for modals)
                playlistList.innerHTML = `
                    <div class="custom-dropdown-container">
                        <div class="custom-dropdown" id="custom-playlist-dropdown">
                            <div class="dropdown-selected" id="dropdown-selected">
                                <span class="selected-text">Choose a playlist...</span>
                                <span class="dropdown-arrow">▼</span>
                            </div>
                            <div class="dropdown-options" id="dropdown-options" style="display: none;">
                                ${playlists.map(playlist => 
                                    `<div class="dropdown-option" data-playlist-id="${playlist.id}">
                                        ${playlist.name} (${playlist.audioFiles ? playlist.audioFiles.length : 0} tracks)
                                     </div>`
                                ).join('')}
                            </div>
                        </div>
                        <button type="button" id="add-to-playlist-confirm" class="add-playlist-confirm-btn" disabled>
                            Add to Playlist
                        </button>
                    </div>
                `;
                
                // Add custom dropdown functionality
                const dropdown = document.getElementById('custom-playlist-dropdown');
                const selected = document.getElementById('dropdown-selected');
                const options = document.getElementById('dropdown-options');
                const confirmButton = document.getElementById('add-to-playlist-confirm');
                let selectedPlaylistId = '';
                
                // Toggle dropdown
                selected.addEventListener('click', function(e) {
                    e.stopPropagation();
                    const isOpen = options.style.display === 'block';
                    options.style.display = isOpen ? 'none' : 'block';
                    dropdown.classList.toggle('open', !isOpen);
                    console.log('Dropdown toggled:', !isOpen);
                });
                
                // Handle option selection
                options.addEventListener('click', function(e) {
                    if (e.target.classList.contains('dropdown-option')) {
                        selectedPlaylistId = e.target.dataset.playlistId;
                        const selectedText = e.target.textContent.trim();
                        
                        // Update selected display
                        selected.querySelector('.selected-text').textContent = selectedText;
                        
                        // Close dropdown
                        options.style.display = 'none';
                        dropdown.classList.remove('open');
                        
                        // Enable confirm button
                        confirmButton.disabled = false;
                        
                        console.log('Playlist selected:', selectedPlaylistId, selectedText);
                    }
                });
                
                // Close dropdown when clicking outside
                document.addEventListener('click', function(e) {
                    if (!dropdown.contains(e.target)) {
                        options.style.display = 'none';
                        dropdown.classList.remove('open');
                    }
                });
                
                // Confirm button action
                confirmButton.addEventListener('click', function() {
                    if (selectedPlaylistId) {
                        console.log('Adding track to playlist:', selectedPlaylistId);
                        addTrackToPlaylist(selectedPlaylistId, trackName, artistName, previewUrl, itunesTrackId);
                    }
                });
            }
        } else {
            throw new Error('Failed to load playlists');
        }
    } catch (error) {
        console.error('Error loading playlists:', error);
        playlistList.innerHTML = '<div class="error" style="color: #f85149; padding: 16px; text-align: center;">Failed to load playlists. Please try again.</div>';
    }
    
    // Show modal
    modal.style.display = 'flex';
    modal.style.alignItems = 'center';
    modal.style.justifyContent = 'center';
}

// Hide playlist modal
function hidePlaylistModal() {
    const modal = document.getElementById('playlist-modal');
    modal.style.display = 'none';
}

// Add track to selected playlist
async function addTrackToPlaylist(playlistId, trackName, artistName, previewUrl, itunesTrackId) {
    try {
        // Ensure iTunes track ID is valid
        if (!itunesTrackId || itunesTrackId === 'undefined' || itunesTrackId === 'null') {
            itunesTrackId = `generated-${btoa(trackName + '-' + artistName).replace(/[^a-zA-Z0-9]/g, '')}`;
        }
        
        const response = await fetch('/ui/chat/add-track-to-playlist', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `playlistId=${playlistId}&itunesTrackId=${encodeURIComponent(itunesTrackId)}&trackName=${encodeURIComponent(trackName)}&artistName=${encodeURIComponent(artistName)}&previewUrl=${encodeURIComponent(previewUrl || '')}`
        });
        
        if (response.ok) {
            const result = await response.json();
            if (result.success) {
                Utils.showNotification('Track added to playlist!', 'success');
                hidePlaylistModal();
                
                // Refresh all "Add to Playlist" button states for this track
                document.querySelectorAll('.add-to-playlist-btn').forEach(btn => {
                    if (btn.dataset.trackName === trackName && btn.dataset.artistName === artistName) {
                        updatePlaylistButtonState(btn);
                    }
                });
            } else {
                Utils.showNotification(result.error || 'Failed to add track to playlist', 'error');
            }
        } else {
            Utils.showNotification('Failed to add track to playlist', 'error');
        }
    } catch (error) {
        console.error('Error adding track to playlist:', error);
        Utils.showNotification('Error adding track to playlist', 'error');
    }
}

// Handle remove from playlist
function handleRemoveFromPlaylist() {
    document.querySelectorAll('.remove-from-playlist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            const audioFileId = this.dataset.audioFileId;
            
            if (!confirm('Remove this track from the playlist?')) {
                return;
            }
            
            try {
                const response = await fetch('/ui/chat/remove-track-from-playlist', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `playlistId=${playlistId}&audioFileId=${audioFileId}`
                });
                
                if (response.ok) {
                    const result = await response.json();
                    if (result.success) {
                        // Remove track from UI
                        this.closest('.playlist-track').remove();
                        Utils.showNotification('Track removed from playlist', 'success');
                    } else {
                        Utils.showNotification(result.error || 'Failed to remove track', 'error');
                    }
                } else {
                    Utils.showNotification('Failed to remove track', 'error');
                }
            } catch (error) {
                console.error('Error removing track:', error);
                Utils.showNotification('Error removing track', 'error');
            }
        });
    });
}

// Handle send track to chat from playlist
function handleSendTrackFromPlaylist() {
    document.querySelectorAll('.send-track-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const trackName = this.dataset.trackName;
            const artistName = this.dataset.artistName;
            const previewUrl = this.dataset.previewUrl;
            
            try {
                const response = await fetch('/ui/chat/send-track', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `trackName=${encodeURIComponent(trackName)}&artistName=${encodeURIComponent(artistName)}&previewUrl=${encodeURIComponent(previewUrl || '')}`
                });
                
                if (response.ok) {
                    Utils.showNotification('Track sent to chat!', 'success');
                    // Reload messages or redirect to chat tab
                    if (window.location.hash !== '#chat') {
                        window.location.href = '#chat';
                        location.reload();
                    }
                } else {
                    Utils.showNotification('Failed to send track', 'error');
                }
            } catch (error) {
                console.error('Error sending track:', error);
                Utils.showNotification('Error sending track', 'error');
            }
        });
    });
}

// Show public playlist modal with details
function showPublicPlaylistModal(playlist) {
    // Create and show modal with playlist details
    const modal = document.createElement('div');
    modal.className = 'modal playlist-detail-modal';
    modal.style.display = 'block';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h4>${playlist.name}</h4>
                <button type="button" class="btn-close">&times;</button>
            </div>
            <div class="modal-body">
                <p><strong>Created by:</strong> ${playlist.owner.username}</p>
                <p><strong>Tracks:</strong> ${playlist.audioFiles ? playlist.audioFiles.length : 0}</p>
                ${playlist.audioFiles && playlist.audioFiles.length > 0 ? `
                    <div class="playlist-tracks">
                        <h5>Tracks:</h5>
                        ${playlist.audioFiles.map(track => `
                            <div class="track-item">
                                <div class="track-info">
                                    <strong>${track.trackName}</strong>
                                    <span>by ${track.artistName}</span>
                                </div>
                                ${track.previewUrl ? `
                                    <audio controls preload="none" class="audio-preview">
                                        <source src="${track.previewUrl}" type="audio/mp4">
                                    </audio>
                                ` : ''}
                            </div>
                        `).join('')}
                    </div>
                ` : '<p>No tracks in this playlist.</p>'}
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Add close functionality
    modal.querySelector('.btn-close').addEventListener('click', () => {
        modal.remove();
    });
    
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.remove();
        }
    });
}

// Initialize playlist modal close events
function initializePlaylistModalEvents() {
    const modal = document.getElementById('playlist-modal');
    if (modal) {
        // Close on backdrop click
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                hidePlaylistModal();
            }
        });
        
        // Close button
        modal.querySelector('.playlist-modal-close')?.addEventListener('click', hidePlaylistModal);
        modal.querySelector('.modal-cancel')?.addEventListener('click', hidePlaylistModal);
        
        // Close on Escape key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && modal.style.display === 'block') {
                hidePlaylistModal();
            }
        });
    }
}

// Initialize playlist UI functionality
function initializePlaylistUI() {
    handlePlaylistToggles();
    handleAddToPlaylist();
    handleRemoveFromPlaylist();
    handleSendTrackFromPlaylist();
    initializePlaylistModalEvents();
}

// Export functions for use by other modules
window.PlaylistUI = {
    initializePlaylistUI,
    handlePlaylistToggles,
    handleAddToPlaylist,
    handleRemoveFromPlaylist,
    handleSendTrackFromPlaylist,
    showPlaylistModal,
    hidePlaylistModal,
    showPublicPlaylistModal,
    loadPlaylistContents,
    updatePlaylistButtonState,
    addTrackToPlaylist
};