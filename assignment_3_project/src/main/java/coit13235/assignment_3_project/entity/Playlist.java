package coit13235.assignment_3_project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing a music playlist.
 */
@Entity
@Table(name = "playlist")
public class Playlist {
    
    /**
     * Unique identifier for the playlist.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Name of the playlist.
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * Optional description of the playlist.
     */
    @Column(length = 1000)
    private String description;
    
    /**
     * Whether the playlist is publicly shareable.
     */
    @Column(nullable = false)
    private boolean isPublic = false;
    
    /**
     * The user who created this playlist.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    
    /**
     * The audio files in this playlist.
     */
    @ManyToMany
    @JoinTable(
        name = "playlist_audiofile",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "audiofile_id")
    )
    private List<AudioFile> audioFiles;
    
    /**
     * Timestamp when the playlist was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the playlist was last modified.
     */
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor.
     */
    public Playlist() {
        this.audioFiles = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with name and owner.
     * @param name the playlist name
     * @param owner the playlist owner
     */
    public Playlist(String name, User owner) {
        this();
        this.name = name;
        this.owner = owner;
    }
    
    // Getters and setters
    
    /**
     * Gets the unique identifier for the playlist.
     * @return the playlist ID
     */
    public Long getId() { return id; }
    
    /**
     * Sets the unique identifier for the playlist.
     * @param id the playlist ID
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * Gets the playlist name.
     * @return the playlist name
     */
    public String getName() { return name; }
    
    /**
     * Sets the playlist name.
     * @param name the playlist name
     */
    public void setName(String name) { this.name = name; }
    
    /**
     * Gets the playlist description.
     * @return the playlist description
     */
    public String getDescription() { return description; }
    
    /**
     * Sets the playlist description.
     * @param description the playlist description
     */
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Checks if the playlist is publicly shareable.
     * @return true if public, false otherwise
     */
    public boolean isPublic() { return isPublic; }
    
    /**
     * Sets whether the playlist is publicly shareable.
     * @param isPublic true to make public, false for private
     */
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    
    /**
     * Gets the playlist owner.
     * @return the user who owns this playlist
     */
    public User getOwner() { return owner; }
    
    /**
     * Sets the playlist owner.
     * @param owner the user who owns this playlist
     */
    public void setOwner(User owner) { this.owner = owner; }
    
    /**
     * Gets the audio files in this playlist.
     * @return list of audio files
     */
    public List<AudioFile> getAudioFiles() { return audioFiles; }
    
    /**
     * Sets the audio files in this playlist.
     * @param audioFiles list of audio files
     */
    public void setAudioFiles(List<AudioFile> audioFiles) { 
        this.audioFiles = audioFiles;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Gets the creation timestamp.
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    /**
     * Sets the creation timestamp.
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    /**
     * Gets the last update timestamp.
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    /**
     * Sets the last update timestamp.
     * @param updatedAt the last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}