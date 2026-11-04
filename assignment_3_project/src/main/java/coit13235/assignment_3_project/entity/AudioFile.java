package coit13235.assignment_3_project.entity;

import jakarta.persistence.*;

/**
 * JPA Entity representing an uploaded audio file.
 */
@Entity
public class AudioFile {
    /**
     * Unique identifier for the audio file.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The stored filename on the server.
     */
    private String filename;

    /**
     * The original filename as uploaded by the user.
     */
    private String originalFilename;

    /**
     * The MIME type of the audio file.
     */
    private String contentType;

    /**
     * The size of the audio file in bytes.
     */
    private Long size;

    /**
     * The user who uploaded the audio file.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    /**
     * The track name (for iTunes integration).
     */
    private String trackName;
    
    /**
     * The artist name (for iTunes integration).
     */
    private String artistName;
    
    /**
     * The iTunes preview URL.
     */
    private String previewUrl;
    
    /**
     * The iTunes track ID.
     */
    private String itunesTrackId;

    /**
     * The file data stored in the database.
     */
    @Lob
    private byte[] fileData;

    /**
     * Timestamp when the file was uploaded.
     */
    private java.time.LocalDateTime uploadedAt;

    /**
     * Gets the unique identifier for the audio file.
     * @return the audio file ID
     */
    public Long getId() { return id; }

    /**
     * Sets the unique identifier for the audio file.
     * @param id the audio file ID
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the stored filename on the server.
     * @return the filename
     */
    public String getFilename() { return filename; }

    /**
     * Sets the stored filename on the server.
     * @param filename the filename
     */
    public void setFilename(String filename) { this.filename = filename; }

    /**
     * Gets the original filename as uploaded by the user.
     * @return the original filename
     */
    public String getOriginalFilename() { return originalFilename; }

    /**
     * Sets the original filename as uploaded by the user.
     * @param originalFilename the original filename
     */
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    /**
     * Gets the MIME type of the audio file.
     * @return the content type
     */
    public String getContentType() { return contentType; }

    /**
     * Sets the MIME type of the audio file.
     * @param contentType the content type
     */
    public void setContentType(String contentType) { this.contentType = contentType; }

    /**
     * Gets the size of the audio file in bytes.
     * @return the file size
     */
    public Long getSize() { return size; }

    /**
     * Sets the size of the audio file in bytes.
     * @param size the file size
     */
    public void setSize(Long size) { this.size = size; }

    /**
     * Gets the user who uploaded the audio file.
     * @return the user
     */
    public User getUser() { return user; }

    /**
     * Sets the user who uploaded the audio file.
     * @param user the user
     */
    public void setUser(User user) { this.user = user; }
    
    /**
     * Gets the track name.
     * @return the track name
     */
    public String getTrackName() { return trackName; }
    
    /**
     * Sets the track name.
     * @param trackName the track name
     */
    public void setTrackName(String trackName) { this.trackName = trackName; }
    
    /**
     * Gets the artist name.
     * @return the artist name
     */
    public String getArtistName() { return artistName; }
    
    /**
     * Sets the artist name.
     * @param artistName the artist name
     */
    public void setArtistName(String artistName) { this.artistName = artistName; }
    
    /**
     * Gets the iTunes preview URL.
     * @return the preview URL
     */
    public String getPreviewUrl() { return previewUrl; }
    
    /**
     * Sets the iTunes preview URL.
     * @param previewUrl the preview URL
     */
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    
    /**
     * Gets the iTunes track ID.
     * @return the iTunes track ID
     */
    public String getItunesTrackId() { return itunesTrackId; }
    
    /**
     * Sets the iTunes track ID.
     * @param itunesTrackId the iTunes track ID
     */
    public void setItunesTrackId(String itunesTrackId) { this.itunesTrackId = itunesTrackId; }

    /**
     * Gets the file data stored in the database.
     * @return the file data as byte array
     */
    public byte[] getFileData() { return fileData; }

    /**
     * Sets the file data to be stored in the database.
     * @param fileData the file data as byte array
     */
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    /**
     * Gets the upload timestamp.
     * @return the upload timestamp
     */
    public java.time.LocalDateTime getUploadedAt() { return uploadedAt; }

    /**
     * Sets the upload timestamp.
     * @param uploadedAt the upload timestamp
     */
    public void setUploadedAt(java.time.LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
