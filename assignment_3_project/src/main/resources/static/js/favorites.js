// Favorites functionality module
// Handles track and artist favorites management

// Handle favorite toggle buttons (for the search results)
function handleFavoriteToggles() {
    document.querySelectorAll('.fav-toggle-btn').forEach(btn => {
        // Remove existing listeners to prevent duplicates
        btn.removeEventListener('click', favoriteToggleHandler);
        btn.addEventListener('click', favoriteToggleHandler);
    });
}

async function favoriteToggleHandler(e) {
    e.preventDefault();
    
    const trackName = this.dataset.trackName;
    const artistName = this.dataset.artistName;
    const previewUrl = this.dataset.previewUrl;
    let itunesTrackId = this.dataset.itunesTrackId;
    const isFav = this.dataset.fav === 'true';
    
    // Handle undefined or null iTunes track ID
    if (!itunesTrackId || itunesTrackId === 'undefined' || itunesTrackId === 'null') {
        itunesTrackId = `generated-${btoa(trackName + '-' + artistName).replace(/[^a-zA-Z0-9]/g, '')}`;
    }
    
    // Show loading state
    const originalText = this.textContent;
    this.disabled = true;
    
    try {
        const formData = new FormData();
        formData.append('trackName', trackName);
        formData.append('artistName', artistName);
        if (previewUrl) formData.append('previewUrl', previewUrl);
        formData.append('itunesTrackId', itunesTrackId);
        
        const response = await fetch('/ui/chat/toggle-favorite', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            // Toggle button state
            if (isFav) {
                // Currently favorited, so unfavorite it
                this.textContent = '♡ Favorite';
                this.classList.remove('btn-favorite-active');
                this.classList.add('btn-favorite');
                this.dataset.fav = 'false';
                this.setAttribute('aria-label', 'Add to favorites');
                
                // Remove from favorites tab if it exists
                removeFromFavoritesTab(itunesTrackId);
            } else {
                // Currently not favorited, so favorite it
                this.textContent = '♥ Unfavorite';
                this.classList.remove('btn-favorite');
                this.classList.add('btn-favorite-active');
                this.dataset.fav = 'true';
                this.setAttribute('aria-label', 'Remove from favorites');
                
                // Add to favorites tab
                addToFavoritesTab(trackName, artistName, previewUrl, itunesTrackId);
            }
        } else {
            // Revert on error
            this.textContent = originalText;
            alert('Failed to update favorites. Please try again.');
        }
    } catch (error) {
        console.error('Favorite toggle error:', error);
        this.textContent = originalText;
        alert('Connection error. Please try again.');
    } finally {
        this.disabled = false;
    }
}

// Helper function to add artist to favorites tab
function addArtistToFavoritesTab(artistName) {
    const artistFavoritesSection = document.getElementById('artist-favorites');
    const emptyState = artistFavoritesSection.querySelector('.empty-state');
    
    if (emptyState) {
        emptyState.remove();
    }
    
    // Check if this artist is already in favorites tab
    const existingArtist = Array.from(artistFavoritesSection.querySelectorAll('.artist-favorite')).find(item => {
        const nameElement = item.querySelector('[data-artist-name]');
        return nameElement && nameElement.dataset.artistName === artistName;
    });
    
    if (existingArtist) {
        return; // Already exists
    }
    
    const artistFavoriteItem = document.createElement('div');
    artistFavoriteItem.className = 'favorite-item artist-favorite';
    artistFavoriteItem.innerHTML = `
        <div class="artist-info">
            <h4 class="artist-name">★ <span>${artistName}</span></h4>
            <p class="favorite-type">Favorite Artist</p>
        </div>
        <div class="artist-actions">
            <button type="button" 
                    class="btn btn-primary search-artist-btn"
                    data-artist-name="${artistName}"
                    aria-label="Search for more songs by this artist">
                🔍 Search
            </button>
            <button type="button" 
                    class="btn btn-info send-artist-to-chat-btn"
                    data-artist-name="${artistName}"
                    aria-label="Send artist to chat">
                💬 Send to Chat
            </button>
            <button type="button" 
                    class="btn btn-danger remove-artist-fav-btn"
                    data-artist-name="${artistName}"
                    aria-label="Remove artist from favorites">
                Remove
            </button>
        </div>
    `;
    
    artistFavoritesSection.appendChild(artistFavoriteItem);
}

// Helper function to remove artist from favorites tab
function removeArtistFromFavoritesTab(artistName) {
    const artistFavoritesSection = document.getElementById('artist-favorites');
    const artistFavoriteItems = Array.from(artistFavoritesSection.querySelectorAll('.artist-favorite'));
    
    const artistItem = artistFavoriteItems.find(item => {
        const button = item.querySelector('[data-artist-name]');
        return button && button.dataset.artistName === artistName;
    });
    
    if (artistItem) {
        artistItem.remove();
        
        // Check if artist favorites section is now empty
        const remainingArtistItems = artistFavoritesSection.querySelectorAll('.artist-favorite');
        if (remainingArtistItems.length === 0) {
            const emptyMessage = document.createElement('div');
            emptyMessage.className = 'empty-state';
            emptyMessage.textContent = 'No favorite artists yet. Favorite some artists from search results!';
            artistFavoritesSection.appendChild(emptyMessage);
        }
    }
}

// Helper function to add item to favorites tab
function addToFavoritesTab(trackName, artistName, previewUrl, itunesTrackId) {
    const trackFavoritesSection = document.getElementById('track-favorites');
    const emptyState = trackFavoritesSection.querySelector('.empty-state');
    
    if (emptyState) {
        emptyState.remove();
    }
    
    const favoriteItem = document.createElement('div');
    favoriteItem.className = 'favorite-item';
    favoriteItem.setAttribute('data-itunes-track-id', itunesTrackId);
    favoriteItem.innerHTML = `
        <div class="track-info">
            <h4 class="track-title">${trackName}</h4>
            <p class="track-artist">by <span>${artistName}</span></p>
            ${previewUrl ? `<audio src="${previewUrl}" controls class="music-embed" aria-label="Audio preview"></audio>` : ''}
        </div>
        <div class="track-actions">
            <button type="button" 
                    class="btn btn-danger remove-fav-btn"
                    data-itunes-track-id="${itunesTrackId}"
                    data-track-name="${trackName}"
                    data-artist-name="${artistName}"
                    aria-label="Remove from favorites">
                Remove
            </button>
        </div>
    `;
    
    trackFavoritesSection.appendChild(favoriteItem);
    
    // Add event listener to the new remove button
    const removeBtn = favoriteItem.querySelector('.remove-fav-btn');
    removeBtn.addEventListener('click', removeFavoriteHandler);
}

// Helper function to remove item from favorites tab
function removeFromFavoritesTab(itunesTrackId) {
    const trackFavoritesSection = document.getElementById('track-favorites');
    const favoriteItem = trackFavoritesSection.querySelector(`[data-itunes-track-id="${itunesTrackId}"]`);
    
    if (favoriteItem) {
        favoriteItem.remove();
        
        // Check if track favorites section is now empty
        const remainingItems = trackFavoritesSection.querySelectorAll('.favorite-item');
        if (remainingItems.length === 0) {
            const emptyMessage = document.createElement('div');
            emptyMessage.className = 'empty-state';
            emptyMessage.textContent = 'No favorite songs yet. Search for music and add some favorites!';
            trackFavoritesSection.appendChild(emptyMessage);
        }
    }
}

// Handle remove favorite buttons (for favorites tab)
function handleRemoveFavorites() {
    document.querySelectorAll('.remove-fav-btn').forEach(btn => {
        btn.removeEventListener('click', removeFavoriteHandler);
        btn.addEventListener('click', removeFavoriteHandler);
    });
}

async function removeFavoriteHandler(e) {
    e.preventDefault();
    
    const itunesTrackId = this.dataset.itunesTrackId;
    const trackName = this.dataset.trackName;
    const favoriteItem = this.closest('.favorite-item');
    
    // Show loading state
    this.disabled = true;
    
    try {
        const formData = new FormData();
        formData.append('itunesTrackId', itunesTrackId);
        
        const response = await fetch('/ui/chat/remove-favorite', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            // Remove the item from the DOM
            favoriteItem.remove();
            
            // Update any corresponding search result buttons to unfavorited state
            const searchButtons = document.querySelectorAll(`[data-itunes-track-id="${itunesTrackId}"]`);
            searchButtons.forEach(btn => {
                if (btn.classList.contains('fav-toggle-btn')) {
                    btn.textContent = '♡ Favorite';
                    btn.classList.remove('btn-favorite-active');
                    btn.classList.add('btn-favorite');
                    btn.dataset.fav = 'false';
                    btn.setAttribute('aria-label', 'Add to favorites');
                }
            });
            
            // Check if favorites tab is now empty
            const favoritesContainer = document.getElementById('music-favorites');
            const favoriteItems = favoritesContainer.querySelectorAll('.favorite-item');
            
            if (favoriteItems.length === 0) {
                const emptyMessage = document.createElement('div');
                emptyMessage.className = 'empty-state';
                emptyMessage.textContent = 'No favorite tracks yet. Search for music and add some favorites!';
                favoritesContainer.appendChild(emptyMessage);
            }
        } else {
            this.textContent = 'Remove';
            this.disabled = false;
            alert('Failed to remove favorite. Please try again.');
        }
    } catch (error) {
        console.error('Remove favorite error:', error);
        this.textContent = 'Remove';
        this.disabled = false;
        alert('Connection error. Please try again.');
    }
}

// Handle remove artist favorite buttons
function handleRemoveArtistFavorites() {
    document.addEventListener('click', async function(e) {
        if (e.target.classList.contains('remove-artist-fav-btn')) {
            e.preventDefault();
            
            const button = e.target;
            const artistName = button.dataset.artistName;
            
            if (!artistName) return;
            
            // Show loading state
            button.disabled = true;
            
            try {
                const formData = new FormData();
                formData.append('artistName', artistName);
                
                const response = await fetch('/ui/chat/toggle-favorite-artist', {
                    method: 'POST',
                    body: formData
                });
                
                const data = await response.json();
                
                if (data.success && !data.favorite) {
                    // Successfully removed - remove from DOM
                    const artistFavoriteItem = button.closest('.artist-favorite');
                    if (artistFavoriteItem) {
                        artistFavoriteItem.remove();
                    }
                    
                    // Update the favorite artist buttons in search results to show unfavorited state
                    const searchButtons = document.querySelectorAll(`[data-artist-name="${artistName}"]`);
                    searchButtons.forEach(searchButton => {
                        if (searchButton.classList.contains('favorite-artist-btn')) {
                            searchButton.classList.remove('favorited');
                            searchButton.classList.remove('btn-warning');
                            searchButton.classList.add('btn-secondary');
                            searchButton.textContent = '♡ Favorite Artist';
                            searchButton.setAttribute('aria-label', 'Add artist to favorites');
                        }
                    });
                    
                    // Check if favorites tab is now empty
                    const favoritesContainer = document.getElementById('music-favorites');
                    const favoriteItems = favoritesContainer.querySelectorAll('.favorite-item');
                    
                    if (favoriteItems.length === 0) {
                        const emptyMessage = document.createElement('div');
                        emptyMessage.className = 'empty-state';
                        emptyMessage.textContent = 'No favorite tracks or artists yet. Search for music and add some favorites!';
                        favoritesContainer.appendChild(emptyMessage);
                    }
                } else {
                    button.textContent = 'Remove';
                    button.disabled = false;
                    alert('Failed to remove artist favorite: ' + (data.error || 'Unknown error'));
                }
            } catch (error) {
                console.error('Remove artist favorite error:', error);
                button.textContent = 'Remove';
                button.disabled = false;
                alert('Connection error. Please try again.');
            }
        }
    });
}

// Handle send artist to chat buttons
function handleSendArtistToChat() {
    document.addEventListener('click', async function(e) {
        if (e.target.classList.contains('send-artist-to-chat-btn')) {
            e.preventDefault();
            
            const button = e.target;
            const artistName = button.dataset.artistName;
            
            if (!artistName) return;
            
            // Show loading state
            const originalText = button.textContent;
            button.textContent = '💬...';
            button.disabled = true;
            
            try {
                // Send artist name as a chat message using same endpoint as search results
                const formData = new FormData();
                formData.append('content', `🎤 Check out this artist: ${artistName}`);
                
                const response = await fetch('/ui/chat', {
                    method: 'POST',
                    body: formData
                });
                
                if (response.ok) {
                    // Message sent successfully - refresh chat like search results do
                    setTimeout(() => {
                        if (typeof Chat !== 'undefined' && Chat.checkForNewMessages) {
                            Chat.checkForNewMessages();
                        }
                    }, 500);
                    
                    // Success - show confirmation
                    button.textContent = '✓ Sent';
                    setTimeout(() => {
                        button.textContent = originalText;
                        button.disabled = false;
                    }, 2000);
                } else {
                    throw new Error('Failed to send artist');
                }
            } catch (error) {
                console.error('Send artist error:', error);
                button.textContent = originalText;
                button.disabled = false;
                alert('Failed to send artist to chat. Please try again.');
            }
        }
    });
}

// Handle favorite artist button clicks with AJAX
function handleFavoriteArtists() {
    document.addEventListener('click', async function(e) {
        if (e.target.classList.contains('favorite-artist-btn')) {
            e.preventDefault();
            
            const button = e.target;
            const form = button.closest('form');
            const artistName = button.dataset.artistName;
            const isFavorited = button.dataset.isFavorited === 'true';
            
            if (!artistName) return;
            
            // Show loading state
            const originalText = button.innerHTML;
            button.innerHTML = '⏳';
            button.disabled = true;
            
            try {
                const formData = new FormData();
                formData.append('artistName', artistName);
                
                const response = await fetch('/ui/chat/toggle-favorite-artist', {
                    method: 'POST',
                    body: formData
                });
                
                const data = await response.json();
                
                if (data.success) {
                    // Update ALL favorite artist buttons for this artist
                    const allArtistButtons = document.querySelectorAll('.favorite-artist-btn');
                    const matchingButtons = Array.from(allArtistButtons).filter(btn => 
                        btn.dataset.artistName === artistName
                    );
                    
                    matchingButtons.forEach(artistButton => {
                        artistButton.dataset.isFavorited = data.favorite.toString();
                        
                        if (data.favorite) {
                            // Now favorited
                            artistButton.classList.add('favorited');
                            artistButton.classList.remove('btn-outline-secondary');
                            artistButton.classList.add('btn-outline-warning');
                            artistButton.innerHTML = '<span>★ Artist</span>';
                            artistButton.setAttribute('aria-label', 'Remove artist from favorites');
                        } else {
                            // No longer favorited
                            artistButton.classList.remove('favorited');
                            artistButton.classList.remove('btn-outline-warning');
                            artistButton.classList.add('btn-outline-secondary');
                            artistButton.innerHTML = '<span>☆ Artist</span>';
                            artistButton.setAttribute('aria-label', 'Add artist to favorites');
                        }
                        artistButton.disabled = false;
                    });
                    
                    if (data.favorite) {
                        // Add to favorites tab
                        addArtistToFavoritesTab(artistName);
                    } else {
                        // Remove from favorites tab
                        removeArtistFromFavoritesTab(artistName);
                    }
                } else {
                    button.innerHTML = originalText;
                    alert('Failed to update artist favorite: ' + (data.error || 'Unknown error'));
                }
            } catch (error) {
                console.error('Favorite artist error:', error);
                button.innerHTML = originalText;
                alert('Connection error. Please try again.');
            } finally {
                button.disabled = false;
            }
        }
    });
}

// Initialize favorites functionality
function initializeFavorites() {
    handleFavoriteToggles();
    handleRemoveFavorites();
    handleRemoveArtistFavorites();
    handleSendArtistToChat();
    handleFavoriteArtists();
}

// Export functions for use by other modules
window.Favorites = {
    initializeFavorites,
    handleFavoriteToggles,
    handleRemoveFavorites,
    handleRemoveArtistFavorites,
    handleSendArtistToChat,
    handleFavoriteArtists,
    addToFavoritesTab,
    removeFromFavoritesTab,
    addArtistToFavoritesTab,
    removeArtistFromFavoritesTab
};