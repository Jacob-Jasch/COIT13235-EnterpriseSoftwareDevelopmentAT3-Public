// Main application module
// Coordinates all other modules and handles initialization

// Application initialization
document.addEventListener('DOMContentLoaded', () => {
    // Initialize all modules in the correct order
    initializeApplication();
});

function initializeApplication() {
    console.log('Initializing music chat application...');
    
    // Initialize utilities first (notifications, tabs, etc.)
    if (typeof Utils !== 'undefined' && Utils.initializeUtils) {
        Utils.initializeUtils();
        console.log('✓ Utils module initialized');
    }
    
    // Set default active tab
    if (typeof Utils !== 'undefined' && Utils.switchTab) {
        Utils.switchTab('search');
        console.log('✓ Default tab set to search');
    }
    
    // Initialize chat functionality
    if (typeof Chat !== 'undefined' && Chat.initializeChat) {
        Chat.initializeChat();
        console.log('✓ Chat module initialized');
    }
    
    // Initialize music search
    if (typeof MusicSearch !== 'undefined' && MusicSearch.initializeMusicSearch) {
        MusicSearch.initializeMusicSearch();
        console.log('✓ Music search module initialized');
    }
    
    // Initialize favorites functionality
    if (typeof Favorites !== 'undefined' && Favorites.initializeFavorites) {
        Favorites.initializeFavorites();
        console.log('✓ Favorites module initialized');
    }
    
    // Initialize playlist management
    if (typeof PlaylistManager !== 'undefined' && PlaylistManager.initializePlaylistManager) {
        PlaylistManager.initializePlaylistManager();
        console.log('✓ Playlist manager module initialized');
    }
    
    // Initialize playlist UI
    if (typeof PlaylistUI !== 'undefined' && PlaylistUI.initializePlaylistUI) {
        PlaylistUI.initializePlaylistUI();
        console.log('✓ Playlist UI module initialized');
    }
    
    console.log('🎵 Music chat application fully initialized!');
}

// Global error handler for better debugging
window.addEventListener('error', function(event) {
    console.error('Application Error:', event.error);
    if (typeof Utils !== 'undefined' && Utils.showNotification) {
        Utils.showNotification('An error occurred. Please refresh the page if issues persist.', 'error');
    }
});

// Global unhandled promise rejection handler
window.addEventListener('unhandledrejection', function(event) {
    console.error('Unhandled Promise Rejection:', event.reason);
    if (typeof Utils !== 'undefined' && Utils.showNotification) {
        Utils.showNotification('A network error occurred. Please check your connection.', 'error');
    }
});

// Export main app functionality
window.App = {
    initializeApplication
};