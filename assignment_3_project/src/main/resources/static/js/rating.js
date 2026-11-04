/**
 * Rating System JavaScript
 * Handles rating functionality for tracks across different sections
 */

class RatingManager {
    constructor() {
        this.init();
    }

    init() {
        // Wait for DOM to be ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.setupEventListeners());
        } else {
            this.setupEventListeners();
        }
    }

    setupEventListeners() {
        // Set up event delegation for rating buttons
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('rate-btn')) {
                this.handleRatingSubmit(e);
            }
        });

        // Set up event delegation for rating selects
        document.addEventListener('change', (e) => {
            if (e.target.classList.contains('rating-select')) {
                this.handleRatingChange(e);
            }
        });
    }

    handleRatingChange(event) {
        const selectElement = event.target;
        const ratingValue = selectElement.value;
        const rateButton = selectElement.parentElement.querySelector('.rate-btn');
        const ratingWrapper = selectElement.closest('.rating-form-wrapper');
        
        if (rateButton && ratingWrapper) {
            if (ratingValue) {
                rateButton.disabled = false;
                rateButton.textContent = `Rate ${ratingValue}★`;
                rateButton.classList.remove('btn-outline-warning');
                rateButton.classList.add('btn-warning');
                
                // Add visual feedback for selection
                ratingWrapper.classList.add('has-selection');
            } else {
                rateButton.disabled = true;
                rateButton.textContent = 'Rate';
                rateButton.classList.remove('btn-warning');
                rateButton.classList.add('btn-outline-warning');
                
                // Remove visual feedback
                ratingWrapper.classList.remove('has-selection');
            }
        }
    }

    async handleRatingSubmit(event) {
        event.preventDefault();
        const button = event.target;
        const ratingGroup = button.closest('.rating-group');
        const ratingWrapper = button.closest('.rating-form-wrapper');
        
        if (!ratingGroup) {
            console.error('Rating group not found');
            return;
        }

        const selectElement = ratingGroup.querySelector('.rating-select');
        if (!selectElement) {
            console.error('Rating select not found');
            return;
        }

        const rating = parseInt(selectElement.value);
        if (!rating || rating < 1 || rating > 5) {
            if (typeof Utils !== 'undefined' && Utils.showNotification) {
                Utils.showNotification('Please select a rating first', 'error');
            } else {
                this.showMessage('Please select a rating first', 'error');
            }
            return;
        }

        // Get track information from data attributes
        const trackName = selectElement.dataset.trackName;
        const artistName = selectElement.dataset.artistName;
        const previewUrl = selectElement.dataset.previewUrl;
        const audioFileId = selectElement.dataset.audioFileId;

        if (!trackName || !artistName) {
            console.error('Track information missing');
            if (typeof Utils !== 'undefined' && Utils.showNotification) {
                Utils.showNotification('Track information is missing', 'error');
            } else {
                this.showMessage('Track information is missing', 'error');
            }
            return;
        }

        // Disable button during request with enhanced animation
        const originalText = button.textContent;
        button.disabled = true;
        button.textContent = 'Rating...';
        button.style.animation = 'pulse 1s infinite';

        try {
            let response;
            
            // Use different endpoints based on available data
            if (audioFileId) {
                // For uploaded files with audio file ID
                response = await fetch('/api/ratings', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        audioFileId: parseInt(audioFileId),
                        rating: rating
                    })
                });
            } else {
                // For iTunes tracks or tracks without file ID
                response = await fetch('/ui/chat/rate-track', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        trackName: trackName,
                        artistName: artistName,
                        previewUrl: previewUrl || '',
                        rating: rating
                    })
                });
            }

            if (response.ok) {
                const result = audioFileId ? await response.json() : await response.json();
                
                if (audioFileId || result.success) {
                    // Show enhanced success notification
                    if (typeof Utils !== 'undefined' && Utils.showNotification) {
                        Utils.showNotification(`🎵 Successfully rated "${trackName}" ${rating} stars!`, 'success');
                    } else {
                        this.showMessage(`Rated ${rating} stars!`, 'success');
                    }
                    this.updateRatingDisplay(selectElement, rating);
                    
                    // Show success state with enhanced animation
                    if (ratingWrapper) {
                        ratingWrapper.classList.remove('has-selection');
                        ratingWrapper.classList.add('success');
                        setTimeout(() => {
                            ratingWrapper.classList.remove('success');
                        }, 2000);
                    }
                    
                    // Reset the form
                    selectElement.value = '';
                    button.textContent = '✓ Rated';
                    setTimeout(() => {
                        button.textContent = originalText;
                        button.disabled = true;
                        button.classList.remove('btn-warning');
                        button.classList.add('btn-outline-warning');
                    }, 2000);
                } else {
                    throw new Error(result.error || 'Failed to rate');
                }
            } else {
                const errorText = await response.text();
                throw new Error(`Failed to rate: ${errorText}`);
            }
        } catch (error) {
            console.error('Error rating track:', error);
            if (typeof Utils !== 'undefined' && Utils.showNotification) {
                Utils.showNotification(error.message || 'Failed to rate track', 'error');
            } else {
                this.showMessage(error.message || 'Failed to rate track', 'error');
            }
            
            // Reset selection on error
            selectElement.value = '';
            if (ratingWrapper) {
                ratingWrapper.classList.remove('has-selection');
            }
        } finally {
            // Re-enable button and remove animation
            button.style.animation = '';
            if (button.textContent === 'Rating...') {
                button.disabled = true;
                button.textContent = originalText;
                button.classList.remove('btn-warning');
                button.classList.add('btn-outline-warning');
            }
        }
    }

    updateRatingDisplay(selectElement, rating) {
        // Update any existing rating displays for this track
        const trackName = selectElement.dataset.trackName;
        const artistName = selectElement.dataset.artistName;
        
        // Find and update any rating displays
        const ratingItems = document.querySelectorAll('.rating-item');
        ratingItems.forEach(item => {
            const trackTitle = item.querySelector('.track-title');
            const trackArtist = item.querySelector('.track-artist');
            
            if (trackTitle && trackArtist) {
                const itemTrackName = trackTitle.textContent.trim();
                const itemArtistName = trackArtist.textContent.replace('by ', '').trim();
                
                if (itemTrackName === trackName && itemArtistName === artistName) {
                    const ratingScore = item.querySelector('.rating-score');
                    if (ratingScore) {
                        ratingScore.textContent = `${rating}/5 stars`;
                    }
                }
            }
        });
    }

    showMessage(message, type = 'info') {
        // Create a toast message
        const toast = document.createElement('div');
        toast.className = `alert alert-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'info'} position-fixed`;
        toast.style.cssText = `
            top: 20px;
            right: 20px;
            z-index: 1050;
            min-width: 250px;
            animation: slideInFromRight 0.3s ease-out;
        `;
        toast.textContent = message;

        // Add animation styles if not already present
        if (!document.getElementById('toast-styles')) {
            const style = document.createElement('style');
            style.id = 'toast-styles';
            style.textContent = `
                @keyframes slideInFromRight {
                    from {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
                @keyframes slideOutToRight {
                    from {
                        transform: translateX(0);
                        opacity: 1;
                    }
                    to {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                }
                @keyframes pulse {
                    0%, 100% {
                        transform: scale(1);
                        opacity: 1;
                    }
                    50% {
                        transform: scale(1.05);
                        opacity: 0.8;
                    }
                }
            `;
            document.head.appendChild(style);
        }

        document.body.appendChild(toast);

        // Auto-remove after 4 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.style.animation = 'slideOutToRight 0.3s ease-in';
                setTimeout(() => {
                    if (toast.parentNode) {
                        toast.parentNode.removeChild(toast);
                    }
                }, 300);
            }
        }, 4000);
    }
}

// Initialize rating manager when script loads
const ratingManager = new RatingManager();