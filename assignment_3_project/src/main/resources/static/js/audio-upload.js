/**
 * Audio Upload Module
 * Handles file upload functionality including drag & drop, progress tracking, and file management
 */

class AudioUploadManager {
    constructor() {
        this.uploadArea = null;
        this.fileInput = null;
        this.uploadForm = null;
        this.progressContainer = null;
        this.progressFill = null;
        this.progressText = null;
        this.submitButton = null;
        this.uploadedFilesList = null;
        
        this.currentFile = null;
        this.isUploading = false;
        
        this.init();
    }

    init() {
        // Wait for DOM to be ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.setupElements());
        } else {
            this.setupElements();
        }
    }

    setupElements() {
        // Get DOM elements
        this.uploadArea = document.getElementById('upload-area');
        this.fileInput = document.getElementById('audio-file-input');
        this.uploadForm = document.getElementById('audio-upload-form');
        this.progressContainer = document.getElementById('upload-progress');
        this.progressFill = document.getElementById('progress-fill');
        this.progressText = document.getElementById('progress-text');
        this.submitButton = document.getElementById('upload-submit');
        this.uploadedFilesList = document.getElementById('uploaded-files-list');

        if (!this.uploadArea || !this.fileInput) {
            console.log('Upload elements not found on this page');
            return;
        }

        this.setupEventListeners();
        this.loadUploadedFiles();
    }

    setupEventListeners() {
        // File input change
        this.fileInput.addEventListener('change', (e) => this.handleFileSelect(e));

        // Drag and drop events
        this.uploadArea.addEventListener('click', () => this.fileInput.click());
        this.uploadArea.addEventListener('dragover', (e) => this.handleDragOver(e));
        this.uploadArea.addEventListener('dragleave', (e) => this.handleDragLeave(e));
        this.uploadArea.addEventListener('drop', (e) => this.handleDrop(e));

        // Form submission
        this.uploadForm.addEventListener('submit', (e) => this.handleUpload(e));

        // Prevent default drag behaviors on the entire document
        document.addEventListener('dragover', (e) => e.preventDefault());
        document.addEventListener('drop', (e) => e.preventDefault());
    }

    handleFileSelect(event) {
        const file = event.target.files[0];
        this.processFile(file);
    }

    handleDragOver(event) {
        event.preventDefault();
        this.uploadArea.classList.add('drag-over');
    }

    handleDragLeave(event) {
        event.preventDefault();
        // Only remove if leaving the upload area itself
        if (!this.uploadArea.contains(event.relatedTarget)) {
            this.uploadArea.classList.remove('drag-over');
        }
    }

    handleDrop(event) {
        event.preventDefault();
        this.uploadArea.classList.remove('drag-over');
        
        const files = event.dataTransfer.files;
        if (files.length > 0) {
            this.processFile(files[0]);
        }
    }

    processFile(file) {
        if (!file) return;

        // Validate file type
        if (!this.isAudioFile(file)) {
            this.showStatus('Please select a valid audio file (MP3, WAV, OGG, AAC, M4A)', 'error');
            return;
        }

        // Validate file size (50MB limit)
        if (file.size > 50 * 1024 * 1024) {
            this.showStatus('File size must be less than 50MB', 'error');
            return;
        }

        this.currentFile = file;
        this.updateUI();
        this.showStatus(`Selected: ${file.name} (${this.formatFileSize(file.size)})`, 'info');
    }

    isAudioFile(file) {
        const audioTypes = [
            'audio/mpeg',     // MP3
            'audio/wav',      // WAV
            'audio/ogg',      // OGG
            'audio/aac',      // AAC
            'audio/mp4',      // M4A
            'audio/x-m4a',    // M4A alternative
            'audio/webm',     // WebM
            'audio/flac'      // FLAC
        ];
        
        return audioTypes.includes(file.type) || 
               /\.(mp3|wav|ogg|aac|m4a|webm|flac)$/i.test(file.name);
    }

    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    updateUI() {
        if (this.currentFile) {
            this.submitButton.disabled = false;
            this.submitButton.textContent = `Upload ${this.currentFile.name}`;
        } else {
            this.submitButton.disabled = true;
            this.submitButton.textContent = 'Upload Audio';
        }
    }

    async handleUpload(event) {
        event.preventDefault();
        
        if (!this.currentFile || this.isUploading) {
            return;
        }

        this.isUploading = true;
        this.showProgress(0);
        
        try {
            const formData = new FormData();
            formData.append('file', this.currentFile);

            const response = await this.uploadWithProgress(formData);
            
            console.log('Upload response status:', response.status);
            
            if (response.status === 200 || response.status === 201) {
                let result;
                try {
                    result = JSON.parse(response.responseText);
                } catch (e) {
                    // If not JSON, treat as success anyway
                    result = { success: true };
                }
                this.showStatus('Upload successful!', 'success');
                this.resetForm();
                this.loadUploadedFiles();
                
                // Add success animation
                this.uploadArea.classList.add('upload-success-animation');
                setTimeout(() => {
                    this.uploadArea.classList.remove('upload-success-animation');
                }, 600);
            } else {
                const errorText = response.responseText || `HTTP ${response.status}`;
                console.error('Upload failed with status:', response.status, 'Response:', errorText);
                
                let errorMessage = 'Upload failed';
                if (response.status === 401) {
                    errorMessage = 'Please log in to upload files';
                } else if (response.status === 413) {
                    errorMessage = 'File too large';
                } else if (response.status === 415) {
                    errorMessage = 'File type not supported';
                } else if (response.status >= 500) {
                    errorMessage = 'Server error - please try again';
                } else {
                    errorMessage = `Upload failed: ${errorText}`;
                }
                
                this.showStatus(errorMessage, 'error');
            }
        } catch (error) {
            console.error('Upload error:', error);
            let errorMessage = 'Upload failed: ';
            if (error.message.includes('timeout')) {
                errorMessage += 'Request timeout - file may be too large';
            } else if (error.message.includes('Network')) {
                errorMessage += 'Network error - check your connection';
            } else {
                errorMessage += error.message;
            }
            this.showStatus(errorMessage, 'error');
        } finally {
            this.isUploading = false;
            this.hideProgress();
        }
    }

    uploadWithProgress(formData) {
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            
            // Track upload progress
            xhr.upload.addEventListener('progress', (e) => {
                if (e.lengthComputable) {
                    const percentComplete = (e.loaded / e.total) * 100;
                    this.showProgress(percentComplete);
                }
            });

            xhr.addEventListener('load', () => {
                resolve(xhr);
            });

            xhr.addEventListener('error', () => {
                console.error('XHR Error - Status:', xhr.status, 'Response:', xhr.responseText);
                reject(new Error(`Network error - Status: ${xhr.status}`));
            });

            xhr.addEventListener('timeout', () => {
                console.error('XHR Timeout');
                reject(new Error('Request timeout'));
            });

            // Set timeout to 60 seconds
            xhr.timeout = 60000;
            
            xhr.open('POST', '/api/audio/upload');
            
            // Send credentials with the request (needed for API authentication)
            xhr.withCredentials = true;
            
            console.log('Starting upload of file:', this.currentFile.name, 'Size:', this.currentFile.size);
            xhr.send(formData);
        });
    }

    showProgress(percent) {
        this.progressContainer.style.display = 'block';
        this.progressFill.style.width = percent + '%';
        this.progressText.textContent = `Uploading... ${Math.round(percent)}%`;
        this.submitButton.disabled = true;
    }

    hideProgress() {
        this.progressContainer.style.display = 'none';
        this.submitButton.disabled = false;
    }

    resetForm() {
        this.currentFile = null;
        this.fileInput.value = '';
        this.updateUI();
        this.hideStatus();
    }

    showStatus(message, type = 'info') {
        // Remove existing status elements
        this.hideStatus();
        
        const statusElement = document.createElement('div');
        statusElement.className = `upload-status ${type}`;
        statusElement.textContent = message;
        statusElement.id = 'upload-status-message';
        
        this.uploadForm.appendChild(statusElement);
        
        // Auto-hide success and info messages after 5 seconds
        if (type === 'success' || type === 'info') {
            setTimeout(() => this.hideStatus(), 5000);
        }
    }

    hideStatus() {
        const existing = document.getElementById('upload-status-message');
        if (existing) {
            existing.remove();
        }
    }

    async loadUploadedFiles() {
        if (!this.uploadedFilesList) return;

        try {
            const response = await fetch('/api/audio/list', {
                method: 'GET',
                credentials: 'include'  // Include credentials for API authentication
            });
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const files = await response.json();
            this.renderUploadedFiles(files);
        } catch (error) {
            console.error('Error loading uploaded files:', error);
            this.uploadedFilesList.innerHTML = `
                <div class="empty-state">
                    Failed to load uploaded files: ${error.message}
                    <br><button onclick="audioUploadManager.loadUploadedFiles()" class="btn btn-sm btn-secondary" style="margin-top: 10px;">Retry</button>
                </div>
            `;
        }
    }

    async renderUploadedFiles(files) {
        if (!files || files.length === 0) {
            this.uploadedFilesList.innerHTML = `
                <div class="empty-state">
                    No uploaded files yet. Upload your first audio file above!
                </div>
            `;
            return;
        }

        // For simplicity, let the existing favorites system manage state
        // We'll pass an empty set and let the existing system update button states
        const filesHtml = files.map(file => this.createFileItemHtml(file, new Set())).join('');
        this.uploadedFilesList.innerHTML = filesHtml;
        
        // Attach event listeners to new elements
        this.attachFileItemListeners();
    }

    createFileItemHtml(file, favoriteFileIds = new Set()) {
        const uploadDate = file.uploadedAt ? new Date(file.uploadedAt).toLocaleDateString() : 'Unknown';
        const fileSize = file.size ? this.formatFileSize(file.size) : 'Unknown size';
        const fileType = file.contentType ? file.contentType.replace('audio/', '').toUpperCase() : 'Audio';
        const isFavorited = favoriteFileIds.has(file.id);
        
        // Create track-like data for existing systems
        const trackName = file.originalFilename || file.filename;
        const artistName = file.user ? file.user.username : 'Unknown';
        const previewUrl = `/api/audio/stream/${file.id}`;
        const itunesTrackId = `uploaded-${file.id}`; // Unique identifier for uploaded files
        
        return `
            <div class="uploaded-file-item" data-file-id="${file.id}">
                <!-- File Header: Similar to track-header -->
                <div class="uploaded-file-info">
                    <h4 class="uploaded-file-name">
                        <span class="file-icon">🎵</span>
                        ${this.escapeHtml(trackName)}
                    </h4>
                    <p class="uploaded-file-meta">
                        <span class="upload-date">📅 ${uploadDate}</span> • 
                        <span class="file-size">💾 ${fileSize}</span> • 
                        <span class="file-type">🎼 ${fileType}</span>
                    </p>
                </div>
                
                <!-- Audio Player: Similar to track-audio -->
                <div class="track-audio">
                    <audio controls preload="none" class="uploaded-file-audio">
                        <source src="${previewUrl}" type="${file.contentType || 'audio/mpeg'}">
                        Your browser does not support the audio element.
                    </audio>
                </div>
                
                <!-- Actions: Use existing system structure -->
                <div class="track-actions">
                    <!-- Primary Actions Row -->
                    <div class="primary-actions">
                        <button class="btn btn-primary send-to-chat-btn" 
                                data-file-id="${file.id}"
                                data-filename="${this.escapeHtml(trackName)}"
                                aria-label="Send to chat">
                            💬 Send
                        </button>
                        <button class="btn btn-success add-to-playlist-btn"
                                data-track-name="${this.escapeHtml(trackName)}"
                                data-artist-name="${this.escapeHtml(artistName)}"
                                data-preview-url="${previewUrl}"
                                data-itunes-track-id="${itunesTrackId}"
                                aria-label="Add to playlist">
                            ➕ Add to Playlist
                        </button>
                    </div>
                    
                    <!-- Secondary Actions Row -->
                    <div class="secondary-actions">
                        <!-- Favorite Track Button - uses existing system -->
                        <button type="button" class="btn btn-sm fav-toggle-btn ${isFavorited ? 'btn-favorite-active' : 'btn-favorite'}"
                                data-track-name="${this.escapeHtml(trackName)}"
                                data-artist-name="${this.escapeHtml(artistName)}"
                                data-preview-url="${previewUrl}"
                                data-itunes-track-id="${itunesTrackId}"
                                data-fav="${isFavorited}"
                                aria-label="${isFavorited ? 'Remove from favorites' : 'Add to favorites'}">
                            ${isFavorited ? '♥ Unfav' : '♡ Fav'}
                        </button>
                        
                        <!-- Rating Button -->
                        <div class="rating-form-wrapper">
                            <div class="rating-group">
                                <select class="rating-select" 
                                    data-track-name="${this.escapeHtml(trackName)}"
                                    data-artist-name="${this.escapeHtml(artistName)}"
                                    data-audio-file-id="${file.id}">
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
                        
                        <!-- Delete File Button -->
                        <button class="btn btn-sm btn-danger delete-file-btn"
                                data-file-id="${file.id}"
                                data-filename="${this.escapeHtml(trackName)}"
                                aria-label="Delete file">
                            🗑️ Delete
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    attachFileItemListeners() {
        // Send to chat buttons (custom functionality)
        document.querySelectorAll('.send-to-chat-btn').forEach(btn => {
            btn.addEventListener('click', (e) => this.sendFileToChat(e));
        });

        // Delete file buttons (custom functionality)
        document.querySelectorAll('.delete-file-btn').forEach(btn => {
            btn.addEventListener('click', (e) => this.deleteFile(e));
        });

        // Rating functionality is now handled by rating.js
        // Let existing systems handle add-to-playlist and favorites
        // These will be picked up by playlist-ui.js and favorites.js
        if (typeof handleAddToPlaylist === 'function') {
            handleAddToPlaylist();
        }
        if (typeof handleFavoriteToggles === 'function') {
            handleFavoriteToggles();
        }
    }

    async sendFileToChat(event) {
        const fileId = event.target.dataset.fileId;
        const filename = event.target.dataset.filename;
        
        try {
            // Create a message with audio file reference that matches iTunes format
            const streamUrl = `/api/audio/stream/${fileId}`;
            const message = `[audio: ${streamUrl}] ${filename}`;
            
            // Send the file reference to chat
            const response = await fetch('/ui/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `content=${encodeURIComponent(message)}`
            });

            if (response.ok) {
                this.showStatus(`Sent "${filename}" to chat!`, 'success');
                // Refresh chat if possible
                if (typeof location !== 'undefined') {
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);
                }
            } else {
                this.showStatus('Failed to send file to chat', 'error');
            }
        } catch (error) {
            console.error('Error sending file to chat:', error);
            this.showStatus('Failed to send file to chat', 'error');
        }
    }

    async deleteFile(event) {
        const fileId = event.target.dataset.fileId;
        const filename = event.target.dataset.filename;
        
        if (!confirm(`Are you sure you want to delete "${filename}"? This action cannot be undone.`)) {
            return;
        }
        
        try {
            const response = await fetch(`/api/audio/${fileId}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                this.showStatus(`Deleted "${filename}" successfully!`, 'success');
                // Remove the file item from the DOM
                const fileItem = event.target.closest('.uploaded-file-item');
                if (fileItem) {
                    fileItem.remove();
                }
                // Refresh the list
                this.loadUploadedFiles();
            } else {
                const errorText = await response.text();
                this.showStatus(`Failed to delete: ${errorText}`, 'error');
            }
        } catch (error) {
            console.error('Error deleting file:', error);
            this.showStatus('Failed to delete file', 'error');
        }
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Global function to reload uploaded files (for refresh button)
function loadUploadedFiles() {
    if (window.audioUploadManager) {
        window.audioUploadManager.loadUploadedFiles();
    }
}

// Initialize the upload manager when the script loads
window.audioUploadManager = new AudioUploadManager();