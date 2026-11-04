// Music search functionality module
// Handles iTunes music search, results display, and search form interactions

// Handle music search with AJAX
function handleMusicSearch() {
    const searchForm = document.getElementById('music-search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const query = formData.get('query');
            
            if (!query.trim()) return;
            
            const resultsContainer = document.getElementById('itunes-results');
            resultsContainer.innerHTML = '<div class="loading">Searching...</div>';
            
            try {
                const response = await fetch('/ui/chat/search-music', {
                    method: 'POST',
                    body: formData
                });
                
                if (response.ok) {
                    const html = await response.text();
                    // Create a temporary div to parse the response
                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = html;
                    
                    // Find the results in the response
                    const newResults = tempDiv.querySelectorAll('.track-item');
                    
                    // Clear and populate results
                    resultsContainer.innerHTML = '';
                    if (newResults.length > 0) {
                        newResults.forEach(result => {
                            resultsContainer.appendChild(result.cloneNode(true));
                        });
                        // Re-attach event listeners after updating content
                        if (typeof Favorites !== 'undefined') {
                            Favorites.handleFavoriteToggles();
                        }
                        if (typeof PlaylistUI !== 'undefined') {
                            PlaylistUI.handleAddToPlaylist();
                        }
                    } else {
                        resultsContainer.innerHTML = '<div class="empty-state">No results found. Try a different search term.</div>';
                    }
                } else {
                    resultsContainer.innerHTML = '<div class="error">Search failed. Please try again.</div>';
                }
            } catch (error) {
                console.error('Search error:', error);
                resultsContainer.innerHTML = '<div class="error">Connection error. Please try again.</div>';
            }
        });
    }
}

// Handle artist action buttons (search and send to chat)
function handleArtistActionButtons() {
    document.addEventListener('click', async function(e) {
        if (e.target.classList.contains('search-artist-btn')) {
            e.preventDefault();
            
            const button = e.target;
            const artistName = button.dataset.artistName;
            
            if (!artistName) return;
            
            // Show loading state
            const originalText = button.textContent;
            button.textContent = '🔍...';
            button.disabled = true;
            
            try {
                // Switch to search tab and perform search
                Utils.switchTab('search');
                
                // Fill in search form and submit
                const searchInput = document.querySelector('#music-search-form input[name="query"]');
                if (searchInput) {
                    searchInput.value = artistName;
                    
                    // Trigger search
                    const searchForm = document.getElementById('music-search-form');
                    if (searchForm) {
                        const formData = new FormData(searchForm);
                        const resultsContainer = document.getElementById('itunes-results');
                        resultsContainer.innerHTML = '<div class="loading">Searching for songs by ' + artistName + '...</div>';
                        
                        const response = await fetch('/ui/chat/search-music', {
                            method: 'POST',
                            body: formData
                        });
                        
                        if (response.ok) {
                            const html = await response.text();
                            const tempDiv = document.createElement('div');
                            tempDiv.innerHTML = html;
                            const newResults = tempDiv.querySelectorAll('.track-item');
                            
                            resultsContainer.innerHTML = '';
                            newResults.forEach(result => {
                                resultsContainer.appendChild(result.cloneNode(true));
                            });
                            
                            // Re-initialize handlers for new elements
                            if (typeof Favorites !== 'undefined') {
                                Favorites.handleFavoriteToggles();
                            }
                        } else {
                            resultsContainer.innerHTML = '<div class="error">Failed to search. Please try again.</div>';
                        }
                    }
                }
            } catch (error) {
                console.error('Search artist error:', error);
                alert('Failed to search for artist. Please try again.');
            } finally {
                button.textContent = originalText;
                button.disabled = false;
            }
        }
        
        if (e.target.classList.contains('send-artist-btn')) {
            e.preventDefault();
            
            const button = e.target;
            const artistName = button.dataset.artistName;
            
            if (!artistName) return;
            
            // Show loading state
            const originalText = button.textContent;
            button.textContent = '💬...';
            button.disabled = true;
            
            try {
                // Send artist name as a chat message using same endpoint as favorites
                const formData = new FormData();
                formData.append('content', `🎤 Check out this artist: ${artistName}`);
                
                const response = await fetch('/ui/chat', {
                    method: 'POST',
                    body: formData
                });
                
                if (response.ok) {
                    // Message sent successfully - refresh chat like favorites does
                    setTimeout(() => {
                        if (typeof Chat !== 'undefined' && Chat.checkForNewMessages) {
                            Chat.checkForNewMessages();
                        }
                    }, 500);
                    
                    // Show success feedback
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
                button.textContent = '✗ Failed';
                setTimeout(() => {
                    button.textContent = originalText;
                    button.disabled = false;
                }, 2000);
            }
        }
    });
}

// Initialize music search functionality
function initializeMusicSearch() {
    handleMusicSearch();
    handleArtistActionButtons();
}

// Export functions for use by other modules
window.MusicSearch = {
    initializeMusicSearch,
    handleMusicSearch,
    handleArtistActionButtons
};