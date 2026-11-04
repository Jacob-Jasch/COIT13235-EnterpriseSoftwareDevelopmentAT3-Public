// Playlist management module
// Handles playlist CRUD operations, public/private toggle, sharing functionality

// Load and update user playlists dynamically
async function loadUserPlaylists() {
    const container = document.getElementById('user-playlists-container');
    if (!container) return;
    
    try {
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
                container.innerHTML = '<div class="empty-state">No playlists yet. Create your first playlist above!</div>';
            } else {
                // Generate HTML for playlists
                let playlistsHtml = '';
                playlists.forEach(playlist => {
                    const trackCount = playlist.audioFiles ? playlist.audioFiles.length : 0;
                    const visibility = playlist.isPublic ? 'Public' : 'Private';
                    const visibilityClass = playlist.isPublic ? 'public' : 'private';
                    
                    playlistsHtml += `
                        <div class="playlist-item" data-playlist-id="${playlist.id}">
                            <div class="playlist-header">
                                <div class="playlist-info">
                                    <h4 class="playlist-name">${playlist.name}</h4>
                                    <p class="playlist-meta">
                                        <span>${trackCount}</span> tracks
                                        <span class="playlist-visibility ${visibilityClass}">• ${visibility}</span>
                                    </p>
                                </div>
                                <div class="playlist-actions">
                                    <button class="btn btn-sm btn-info toggle-playlist-btn" 
                                            data-playlist-id="${playlist.id}"
                                            aria-label="View playlist contents">
                                        <span class="toggle-icon">▼</span> View
                                    </button>
                                    <button class="btn btn-sm btn-warning toggle-public-btn" 
                                            data-playlist-id="${playlist.id}" data-is-public="${playlist.isPublic}"
                                            aria-label="Toggle playlist privacy">
                                        ${playlist.isPublic ? 'Public' : 'Private'}
                                    </button>
                                    <button class="btn btn-sm btn-success share-playlist-btn" 
                                            data-playlist-id="${playlist.id}" 
                                            aria-label="Share playlist in chat">
                                        Share
                                    </button>
                                    <button class="btn btn-sm btn-danger delete-playlist-btn" 
                                            data-playlist-id="${playlist.id}" 
                                            aria-label="Delete playlist">
                                        Delete
                                    </button>
                                </div>
                            </div>
                            <div class="playlist-contents" id="playlist-contents-${playlist.id}" style="display: none;">
                                <!-- Playlist contents will be loaded here -->
                            </div>
                        </div>
                    `;
                });
                
                container.innerHTML = playlistsHtml;
                
                // Reattach event handlers for the new elements
                if (typeof PlaylistUI !== 'undefined') {
                    PlaylistUI.handlePlaylistToggles();
                }
                handlePlaylistDeletion();
                handlePlaylistPublicToggle();
                handleSharePlaylist();
            }
        }
    } catch (error) {
        console.error('Error loading playlists:', error);
        Utils.showNotification('Error loading playlists', 'error');
    }
}

// Handle playlist deletion
function handlePlaylistDeletion() {
    document.querySelectorAll('.delete-playlist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            let playlistName = this.dataset.playlistName;
            
            // Fallback: try to get playlist name from the UI if data attribute is missing
            if (!playlistName || playlistName === 'undefined') {
                const playlistItem = this.closest('.playlist-item');
                const nameElement = playlistItem?.querySelector('.playlist-name, h4');
                playlistName = nameElement?.textContent?.trim() || 'this playlist';
            }
            
            if (!confirm(`Are you sure you want to delete the playlist "${playlistName}"?`)) {
                return;
            }
            
            try {
                const response = await fetch('/ui/chat/delete-playlist', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `playlistId=${playlistId}`
                });
                
                if (response.ok) {
                    const result = await response.json();
                    if (result.success) {
                        Utils.showNotification('Playlist deleted successfully', 'success');
                        // Refresh the playlists display
                        await refreshUserPlaylists();
                    } else {
                        Utils.showNotification(result.error || 'Failed to delete playlist', 'error');
                    }
                } else {
                    Utils.showNotification('Failed to delete playlist', 'error');
                }
            } catch (error) {
                console.error('Error deleting playlist:', error);
                Utils.showNotification('Error deleting playlist', 'error');
            }
        });
    });
}

// Handle playlist public/private toggle
function handlePlaylistPublicToggle() {
    document.querySelectorAll('.toggle-public-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            const currentlyPublic = this.dataset.isPublic === 'true';
            
            try {
                const response = await fetch('/ui/chat/toggle-playlist-public', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `playlistId=${playlistId}`
                });
                
                if (response.ok) {
                    const result = await response.json();
                    if (result.success) {
                        // Update the button
                        this.dataset.isPublic = result.isPublic;
                        this.innerHTML = result.isPublic ? '🔓' : '🔒';
                        
                        // Update the visibility text
                        const playlistItem = document.querySelector(`[data-playlist-id="${playlistId}"].playlist-item`);
                        const visibilitySpan = playlistItem.querySelector('.playlist-visibility');
                        if (visibilitySpan) {
                            visibilitySpan.className = `playlist-visibility ${result.isPublic ? 'public' : 'private'}`;
                            visibilitySpan.textContent = result.isPublic ? '• Public' : '• Private';
                        }
                        
                        Utils.showNotification(result.message, 'success');
                    } else {
                        Utils.showNotification(result.error || 'Failed to update playlist', 'error');
                    }
                } else {
                    Utils.showNotification('Failed to update playlist', 'error');
                }
            } catch (error) {
                console.error('Error toggling playlist visibility:', error);
                Utils.showNotification('Error updating playlist', 'error');
            }
        });
    });
}

// Handle share playlist in chat
function handleSharePlaylist() {
    document.querySelectorAll('.share-playlist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            const playlistName = this.dataset.playlistName;
            
            try {
                const response = await fetch('/ui/chat/share-playlist', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `playlistId=${playlistId}`
                });
                
                if (response.ok) {
                    const result = await response.json();
                    if (result.success) {
                        Utils.showNotification(`Playlist "${playlistName}" shared in chat`, 'success');
                        // Force check for new messages to show the shared playlist
                        setTimeout(() => {
                            if (typeof Chat !== 'undefined' && Chat.checkForNewMessages) {
                                Chat.checkForNewMessages();
                            }
                        }, 500);
                        // Switch to chat tab to see the shared playlist
                        Utils.switchTab('search');
                        Utils.scrollChatToBottom();
                    } else {
                        Utils.showNotification(result.error || 'Failed to share playlist', 'error');
                    }
                } else {
                    Utils.showNotification('Failed to share playlist', 'error');
                }
            } catch (error) {
                console.error('Error sharing playlist:', error);
                Utils.showNotification('Error sharing playlist', 'error');
            }
        });
    });
}

// Handle create playlist form
function handleCreatePlaylist() {
    const form = document.getElementById('create-playlist-form');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const playlistName = formData.get('name');
            
            if (!playlistName.trim()) {
                Utils.showNotification('Please enter a playlist name', 'error');
                return;
            }
            
            try {
                const response = await fetch('/ui/chat/create-playlist', {
                    method: 'POST',
                    body: formData
                });
                
                if (response.ok) {
                    // Clear the form
                    form.reset();
                    
                    // Refresh the playlist list
                    await refreshUserPlaylists();
                    
                    Utils.showNotification(`Playlist "${playlistName}" created successfully!`, 'success');
                } else {
                    const result = await response.json().catch(() => ({}));
                    Utils.showNotification(result.error || 'Failed to create playlist', 'error');
                }
            } catch (error) {
                console.error('Error creating playlist:', error);
                Utils.showNotification('Error creating playlist', 'error');
            }
        });
    }
}

// Enhanced load public playlists function
async function loadPublicPlaylists(forceRefresh = false) {
    const container = document.getElementById('public-playlists-container');
    const playlistsList = document.getElementById('public-playlists-list');
    
    if (!forceRefresh && playlistsList.children.length > 0) {
        // Already loaded, no need to reload unless forced
        return;
    }
    
    try {
        // Show loading state
        playlistsList.innerHTML = '<div class="loading">Loading public playlists...</div>';
        
        const response = await fetch('/api/playlists/public', {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            }
        });
        if (response.ok) {
            const playlists = await response.json();
            
            playlistsList.innerHTML = '';
            
            if (playlists.length === 0) {
                playlistsList.innerHTML = '<div class="empty-state">No public playlists found.</div>';
            } else {
                playlists.forEach(playlist => {
                    const playlistItem = document.createElement('div');
                    playlistItem.className = 'public-playlist-item';
                    playlistItem.innerHTML = `
                        <div class="playlist-info">
                            <h5>${playlist.name}</h5>
                            <p>by ${playlist.owner.username} • ${playlist.audioFiles ? playlist.audioFiles.length : 0} tracks</p>
                        </div>
                        <div class="playlist-actions">
                            <button class="btn btn-sm btn-primary view-public-playlist-btn" 
                                    data-playlist-id="${playlist.id}"
                                    data-playlist-name="${playlist.name}">
                                👁️ View
                            </button>
                            <button class="btn btn-sm btn-success copy-public-playlist-btn" 
                                    data-playlist-id="${playlist.id}"
                                    data-playlist-name="${playlist.name}">
                                📋 Copy
                            </button>
                        </div>
                    `;
                    playlistsList.appendChild(playlistItem);
                });
                
                // Add event listeners for public playlist actions
                handlePublicPlaylistActions();
            }
            
            playlistsList.style.display = 'block';
        } else {
            Utils.showNotification('Failed to load public playlists', 'error');
        }
    } catch (error) {
        console.error('Error loading public playlists:', error);
        Utils.showNotification('Error loading public playlists', 'error');
    }
}

// Handle public playlist actions (view, copy)
function handlePublicPlaylistActions() {
    // Handle view public playlist
    document.querySelectorAll('.view-public-playlist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            const playlistName = this.dataset.playlistName;
            
            try {
                const response = await fetch(`/api/playlists/${playlistId}`, {
                    method: 'GET',
                    credentials: 'same-origin',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                    }
                });
                if (response.ok) {
                    const playlist = await response.json();
                    if (typeof PlaylistUI !== 'undefined' && PlaylistUI.showPublicPlaylistModal) {
                        PlaylistUI.showPublicPlaylistModal(playlist);
                    }
                } else {
                    Utils.showNotification('Failed to load playlist details', 'error');
                }
            } catch (error) {
                console.error('Error loading playlist:', error);
                Utils.showNotification('Error loading playlist', 'error');
            }
        });
    });
    
    // Handle copy public playlist
    document.querySelectorAll('.copy-public-playlist-btn').forEach(btn => {
        btn.addEventListener('click', async function() {
            const playlistId = this.dataset.playlistId;
            const playlistName = this.dataset.playlistName;
            
            const newName = prompt(`Copy playlist "${playlistName}" as:`, `${playlistName} (Copy)`);
            if (!newName) return;
            
            try {
                const response = await fetch('/ui/chat/copy-playlist', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `sourcePlaylistId=${playlistId}&newName=${encodeURIComponent(newName)}`
                });
                
                if (response.ok) {
                    const result = await response.json();
                    if (result.success) {
                        Utils.showNotification(`Playlist copied as "${newName}"`, 'success');
                        // Refresh playlists tab
                        location.reload();
                    } else {
                        Utils.showNotification(result.error || 'Failed to copy playlist', 'error');
                    }
                } else {
                    Utils.showNotification('Failed to copy playlist', 'error');
                }
            } catch (error) {
                console.error('Error copying playlist:', error);
                Utils.showNotification('Error copying playlist', 'error');
            }
        });
    });
}

// Handle refresh public playlists button
function handleRefreshPublicPlaylists() {
    const refreshBtn = document.querySelector('.refresh-public-playlists-btn');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', async function() {
            this.disabled = true;
            this.textContent = '🔄 Refreshing...';
            
            try {
                // Force refresh public playlists
                await loadPublicPlaylists(true);
                Utils.showNotification('Public playlists refreshed', 'success');
            } catch (error) {
                console.error('Error refreshing public playlists:', error);
                Utils.showNotification('Failed to refresh public playlists', 'error');
            } finally {
                this.disabled = false;
                this.textContent = '🔄 Refresh';
            }
        });
    }
}

// Handle public playlist discovery
function handlePublicPlaylistDiscovery() {
    const loadBtn = document.querySelector('.load-public-playlists-btn');
    if (loadBtn) {
        loadBtn.addEventListener('click', async function() {
            const container = document.getElementById('public-playlists-list');
            
            this.textContent = 'Loading...';
            this.disabled = true;
            
            try {
                const response = await fetch('/api/playlists/public', {
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
                        container.innerHTML = '<div class="empty-state">No public playlists found.</div>';
                    } else {
                        container.innerHTML = '';
                        playlists.forEach(playlist => {
                            const playlistDiv = document.createElement('div');
                            playlistDiv.className = 'public-playlist-item';
                            playlistDiv.innerHTML = `
                                <div class="playlist-info">
                                    <h5>${playlist.name}</h5>
                                    <p>by ${playlist.owner.username} • ${playlist.audioFiles ? playlist.audioFiles.length : 0} tracks</p>
                                </div>
                                <button class="btn btn-primary view-public-playlist-btn" data-playlist-id="${playlist.id}">View</button>
                            `;
                            container.appendChild(playlistDiv);
                        });
                        
                        // Add event listeners for view buttons
                        container.querySelectorAll('.view-public-playlist-btn').forEach(btn => {
                            btn.addEventListener('click', function() {
                                const playlistId = this.dataset.playlistId;
                                viewPublicPlaylist(playlistId);
                            });
                        });
                    }
                    
                    container.style.display = 'block';
                    this.style.display = 'none';
                } else {
                    throw new Error('Failed to load public playlists');
                }
            } catch (error) {
                console.error('Error loading public playlists:', error);
                Utils.showNotification('Failed to load public playlists', 'error');
            } finally {
                this.textContent = 'Browse Public Playlists';
                this.disabled = false;
            }
        });
    }
}

// View public playlist details using modal
async function viewPublicPlaylist(playlistId) {
    try {
        const response = await fetch(`/api/playlists/${playlistId}`, {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            }
        });
        if (response.ok) {
            const playlist = await response.json();
            
            // Use the proper modal function from PlaylistUI
            if (typeof PlaylistUI !== 'undefined' && PlaylistUI.showPublicPlaylistModal) {
                PlaylistUI.showPublicPlaylistModal(playlist);
            } else {
                // Fallback if PlaylistUI is not available
                console.error('PlaylistUI not available');
                Utils.showNotification('Unable to display playlist details', 'error');
            }
        } else {
            Utils.showNotification('Failed to load playlist details', 'error');
        }
    } catch (error) {
        console.error('Error viewing playlist:', error);
        Utils.showNotification('Error loading playlist', 'error');
    }
}

// Refresh the user's playlists display
async function refreshUserPlaylists() {
    await loadUserPlaylists();
    // Reattach event handlers after refreshing
    if (typeof PlaylistUI !== 'undefined') {
        PlaylistUI.handlePlaylistToggles();
    }
    handlePlaylistDeletion();
    handlePlaylistPublicToggle();
    handleSharePlaylist();
}

// Initialize playlist management functionality
function initializePlaylistManager() {
    handleCreatePlaylist();
    handlePlaylistDeletion();
    handlePlaylistPublicToggle();
    handleSharePlaylist();
    handleRefreshPublicPlaylists();
    handlePublicPlaylistDiscovery();
}

// Export functions for use by other modules
window.PlaylistManager = {
    initializePlaylistManager,
    loadUserPlaylists,
    loadPublicPlaylists,
    refreshUserPlaylists,
    handleCreatePlaylist,
    handlePlaylistDeletion,
    handlePlaylistPublicToggle,
    handleSharePlaylist,
    handleRefreshPublicPlaylists,
    handlePublicPlaylistDiscovery,
    handlePublicPlaylistActions
};