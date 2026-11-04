// Utility functions for the chat application

// Simple tab switching functionality - make it global immediately
function switchTab(tabName) {
    // Remove active from all tabs and content
    document.querySelectorAll('.music-tab').forEach(tab => {
        tab.classList.remove('active');
        tab.setAttribute('aria-selected', 'false');
    });
    document.querySelectorAll('.music-tab-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // Activate selected tab and content
    const tab = document.getElementById(tabName + '-tab');
    const content = document.getElementById('music-' + tabName);
    if (tab) {
        tab.classList.add('active');
        tab.setAttribute('aria-selected', 'true');
    }
    if (content) {
        content.classList.add('active');
    }
    
    // Refresh playlists when switching to playlists tab
    if (tabName === 'playlists') {
        loadUserPlaylists();
        // Auto-load public playlists as well
        setTimeout(() => {
            loadPublicPlaylists(false);
        }, 500);
    }
    
    // Load uploaded files when switching to upload tab
    if (tabName === 'upload' && window.audioUploadManager) {
        setTimeout(() => {
            window.audioUploadManager.loadUploadedFiles();
        }, 100);
    }
    
    // Load ratings when switching to ratings tab
    if (tabName === 'ratings') {
        loadRatings();
    }
}

// Show notification (enhanced toast message)
function showNotification(message, type = 'info', duration = 3000) {
    // Remove any existing notifications first
    const existing = document.querySelector('.notification-toast');
    if (existing) {
        existing.remove();
    }
    
    // Create notification element with enhanced styling
    const notification = document.createElement('div');
    notification.className = `notification-toast notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-icon">${getNotificationIcon(type)}</span>
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">&times;</button>
        </div>
    `;
    
    // Enhanced styling
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${getNotificationColor(type)};
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 1000;
        animation: slideInRight 0.3s ease-out;
        display: flex;
        align-items: center;
        gap: 8px;
        min-width: 300px;
        max-width: 500px;
    `;
    
    // Add to page
    document.body.appendChild(notification);
    
    // Auto-remove after delay (longer for errors)
    const timeoutDuration = type === 'error' ? 5000 : duration;
    setTimeout(() => {
        if (notification.parentElement) {
            notification.style.animation = 'slideInRight 0.3s ease reverse';
            setTimeout(() => notification.remove(), 300);
        }
    }, timeoutDuration);
}

// Helper function to get notification icon
function getNotificationIcon(type) {
    switch(type) {
        case 'success': return '✓';
        case 'error': return '✕';
        case 'warning': return '⚠';
        default: return 'ℹ';
    }
}

// Helper function to get notification color
function getNotificationColor(type) {
    switch(type) {
        case 'success': return '#4CAF50';
        case 'error': return '#f44336';
        case 'warning': return '#ff9800';
        default: return '#2196F3';
    }
}

// Handle collapsible sections in favorites tab
function handleCollapsibleSections() {
    document.addEventListener('click', function(e) {
        if (e.target.closest('.section-toggle')) {
            e.preventDefault();
            
            const button = e.target.closest('.section-toggle');
            const target = button.dataset.target;
            const content = document.getElementById(target);
            const icon = button.querySelector('.toggle-icon');
            
            if (content.classList.contains('expanded')) {
                content.classList.remove('expanded');
                content.classList.add('collapsed');
                icon.textContent = '+';
            } else {
                content.classList.remove('collapsed');
                content.classList.add('expanded');
                icon.textContent = '−';
            }
        }
    });
}

// Initialize notification styles if not already present
function initializeNotificationStyles() {
    if (!document.getElementById('notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            @keyframes slideInRight {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            
            .notification-content {
                display: flex;
                align-items: center;
                gap: 8px;
            }
            
            .notification-icon {
                font-weight: bold;
                font-size: 16px;
            }
            
            .notification-message {
                flex: 1;
            }
            
            .notification-close {
                background: none;
                border: none;
                color: white;
                font-size: 18px;
                cursor: pointer;
                padding: 0;
                margin-left: 8px;
                opacity: 0.8;
            }
            
            .notification-close:hover {
                opacity: 1;
            }
        `;
        document.head.appendChild(style);
    }
}

// Scroll chat to bottom
function scrollChatToBottom() {
    const messagesWrapper = document.querySelector('.messages-wrapper');
    if (messagesWrapper) {
        messagesWrapper.scrollTop = messagesWrapper.scrollHeight;
    }
}

// Open search UI and populate with artist name
function openSearchForArtist(artistName) {
    // Switch to search tab
    switchTab('search');
    
    // Populate search input with artist name
    const searchInput = document.querySelector('#music-search-form input[name="query"]');
    if (searchInput) {
        searchInput.value = artistName;
        
        // Optional: Auto-trigger the search
        const searchForm = document.getElementById('music-search-form');
        if (searchForm) {
            // Trigger the search after a short delay to ensure tab switch is complete
            setTimeout(() => {
                const submitEvent = new Event('submit', { bubbles: true, cancelable: true });
                searchForm.dispatchEvent(submitEvent);
            }, 100);
        }
    }
    
    // Show feedback to user
    showNotification(`🔍 Searching for "${artistName}"...`, 'info', 2000);
}

// Initialize utility functions
function initializeUtils() {
    initializeNotificationStyles();
    handleCollapsibleSections();
    
    // Tab click handlers
    document.querySelectorAll('.music-tab').forEach(tab => {
        tab.addEventListener('click', (e) => {
            const tabName = e.target.id.replace('-tab', '');
            switchTab(tabName);
        });
    });
}

// Export functions for use by other modules
window.Utils = {
    switchTab,
    showNotification,
    scrollChatToBottom,
    initializeUtils,
    // Responsive utility functions
    isMobile: () => window.ResponsiveManager ? window.ResponsiveManager.isMobile() : window.innerWidth <= 480,
    isTablet: () => window.ResponsiveManager ? window.ResponsiveManager.isTabletOrSmaller() : window.innerWidth <= 1024,
    getCurrentBreakpoint: () => window.ResponsiveManager ? window.ResponsiveManager.getCurrentBreakpoint() : 'unknown',
    
    // Enhanced notification for mobile
    showResponsiveNotification: (message, type = 'info', duration = 3000) => {
        const isMobileDevice = window.Utils.isMobile();
        
        if (isMobileDevice) {
            // Shorter duration and different positioning for mobile
            duration = Math.min(duration, 2500);
        }
        
        showNotification(message, type, duration);
    },
    
    // Responsive tab switching with mobile optimizations
    switchTabResponsive: (tabName) => {
        switchTab(tabName);
        
        // On mobile, scroll the active tab into view
        if (window.Utils.isMobile()) {
            setTimeout(() => {
                const activeTab = document.querySelector('.music-tab.active');
                if (activeTab) {
                    activeTab.scrollIntoView({
                        behavior: 'smooth',
                        block: 'nearest',
                        inline: 'center'
                    });
                }
            }, 100);
        }
    },
    
    // Check if device supports touch
    isTouchDevice: () => {
        return 'ontouchstart' in window || navigator.maxTouchPoints > 0;
    },
    
    // Get safe area insets for notched devices
    getSafeAreaInsets: () => {
        const style = getComputedStyle(document.documentElement);
        return {
            top: parseInt(style.getPropertyValue('env(safe-area-inset-top)') || '0'),
            right: parseInt(style.getPropertyValue('env(safe-area-inset-right)') || '0'),
            bottom: parseInt(style.getPropertyValue('env(safe-area-inset-bottom)') || '0'),
            left: parseInt(style.getPropertyValue('env(safe-area-inset-left)') || '0')
        };
    }
};

// Load and display ratings in the ratings tab
async function loadRatings() {
    try {
        // Fetch ratings and current user info in parallel
        const [ratingsResponse, userResponse] = await Promise.all([
            fetch('/api/ratings/all'),
            fetch('/api/user/current', { credentials: 'include' })
        ]);
        
        if (!ratingsResponse.ok) {
            throw new Error(`HTTP error! status: ${ratingsResponse.status}`);
        }
        
        const ratings = await ratingsResponse.json();
        let currentUser = null;
        
        // Try to get current user (might fail if not authenticated)
        if (userResponse.ok) {
            try {
                currentUser = await userResponse.json();
            } catch (e) {
                // User not authenticated
            }
        }
        
        const ratingsContainer = document.querySelector('#music-ratings');
        
        if (!ratingsContainer) {
            console.error('Ratings container not found');
            return;
        }
        
        // Remove existing ratings list if it exists
        const existingList = ratingsContainer.querySelector('.ratings-list');
        if (existingList) {
            existingList.remove();
        }
        
        // Create new ratings list container
        const ratingsList = document.createElement('div');
        ratingsList.className = 'ratings-list';
        
        if (ratings && ratings.length > 0) {
            ratings.forEach(rating => {
                const ratingItem = document.createElement('div');
                ratingItem.className = 'rating-item';
                
                const isOwnRating = currentUser && currentUser.username && rating.username === currentUser.username;
                
                ratingItem.innerHTML = `
                    <div class="rating-track-info">
                        <h4 class="track-title">${escapeHtml(rating.trackName)}</h4>
                        <p class="track-artist">by ${escapeHtml(rating.artistName)}</p>
                    </div>
                    <div class="rating-details">
                        <span class="rating-score">${rating.rating}/5 stars</span>
                        <span class="rating-separator"> - </span>
                        <span class="rating-user">${escapeHtml(rating.username)}</span>
                        ${isOwnRating ? `
                            <select class="rating-select edit-rating-dropdown" 
                                    data-rating-id="${rating.id}" 
                                    data-current-rating="${rating.rating}"
                                    style="margin-left: 8px; padding: 4px 6px; border-radius: 4px; border: 1px solid #ddd; background: #fff; font-size: 11px;">
                                <option value="1" ${rating.rating == 1 ? 'selected' : ''}>1★</option>
                                <option value="2" ${rating.rating == 2 ? 'selected' : ''}>2★</option>
                                <option value="3" ${rating.rating == 3 ? 'selected' : ''}>3★</option>
                                <option value="4" ${rating.rating == 4 ? 'selected' : ''}>4★</option>
                                <option value="5" ${rating.rating == 5 ? 'selected' : ''}>5★</option>
                            </select>
                            <button class="btn btn-sm btn-outline-danger delete-rating-btn" 
                                    data-rating-id="${rating.id}"
                                    title="Delete your rating"
                                    style="margin-left: 4px; font-size: 11px; padding: 1px 4px;">
                                🗑️
                            </button>
                        ` : ''}
                    </div>
                `;
                ratingsList.appendChild(ratingItem);
            });
            
            // Add event listeners for edit dropdowns and delete buttons
            addRatingEditListeners(ratingsList);
            
        } else {
            const emptyState = document.createElement('div');
            emptyState.className = 'empty-state';
            emptyState.textContent = 'No ratings yet. Be the first to rate some music!';
            ratingsList.appendChild(emptyState);
        }
        
        // Add the ratings list after the title
        const title = ratingsContainer.querySelector('.tab-title');
        if (title) {
            title.insertAdjacentElement('afterend', ratingsList);
        } else {
            ratingsContainer.appendChild(ratingsList);
        }
        
    } catch (error) {
        console.error('Error loading ratings:', error);
        const ratingsContainer = document.querySelector('#music-ratings');
        if (ratingsContainer) {
            ratingsContainer.innerHTML = `
                <div class="error-state">
                    <p>Failed to load ratings. Please try again later.</p>
                    <button onclick="loadRatings()" class="btn btn-primary btn-sm">Retry</button>
                </div>
            `;
        }
    }
}

// Add event listeners for rating edit dropdowns and delete buttons
function addRatingEditListeners(container) {
    // Edit rating dropdowns
    const editDropdowns = container.querySelectorAll('.edit-rating-dropdown');
    
    editDropdowns.forEach(dropdown => {
        dropdown.addEventListener('change', async function() {
            const ratingId = this.dataset.ratingId;
            const newRating = this.value;
            const originalRating = this.dataset.currentRating;
            
            try {
                const response = await fetch(`/api/ratings/${ratingId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                    body: JSON.stringify({
                        rating: parseInt(newRating)
                    })
                });
                
                if (response.ok) {
                    showNotification(`🎵 Rating updated to ${newRating} stars!`, 'success');
                    loadRatings(); // Refresh the ratings list
                } else {
                    throw new Error('Failed to update rating');
                }
            } catch (error) {
                console.error('Error updating rating:', error);
                // Revert dropdown to original value
                this.value = originalRating;
                if (error.message.includes('401') || error.message.includes('Unauthorized')) {
                    showNotification('Please log in to edit ratings.', 'warning');
                } else {
                    showNotification('Failed to update rating. Please try again.', 'error');
                }
            }
        });
    });
    
    // Delete rating buttons
    const deleteButtons = container.querySelectorAll('.delete-rating-btn');
    
    deleteButtons.forEach(btn => {
        btn.addEventListener('click', async function() {
            const ratingId = this.dataset.ratingId;
            
            if (confirm('Are you sure you want to delete this rating?')) {
                try {
                    const response = await fetch(`/api/ratings/${ratingId}`, {
                        method: 'DELETE',
                        credentials: 'include'
                    });
                    
                    if (response.ok) {
                        showNotification('🎵 Rating deleted successfully!', 'success');
                        loadRatings(); // Refresh the ratings list
                    } else {
                        throw new Error('Failed to delete rating');
                    }
                } catch (error) {
                    console.error('Error deleting rating:', error);
                    if (error.message.includes('401') || error.message.includes('Unauthorized')) {
                        showNotification('Please log in to delete ratings.', 'warning');
                    } else {
                        showNotification('Failed to delete rating. Please try again.', 'error');
                    }
                }
            }
        });
    });
}

// Helper function to escape HTML
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Also make openSearchForArtist globally available for onclick handlers
window.openSearchForArtist = openSearchForArtist;
window.switchTab = switchTab;
window.loadRatings = loadRatings;