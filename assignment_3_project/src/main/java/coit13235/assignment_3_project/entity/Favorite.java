package coit13235.assignment_3_project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a user's favorite audio track or artist.
 */
@Entity
@Table(name = "favorite",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "audiofile_id"}),
        @UniqueConstraint(columnNames = {"user_id", "artist_name"})
    }
)
public class Favorite {
    
    /**
     * Unique identifier for the favorite.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The user who marked this as favorite.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * The audio file marked as favorite (optional, for track favorites).
     */
    @ManyToOne
    @JoinColumn(name = "audiofile_id")
    private AudioFile audioFile;
    
    /**
     * The artist name marked as favorite (optional, for artist favorites).
     */
    @Column(name = "artist_name", length = 255)
    private String artistName;
    
    /**
     * The type of favorite (TRACK or ARTIST).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FavoriteType type;
    
    /**
     * Timestamp when the favorite was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Enum for favorite types.
     */
    public enum FavoriteType {
        TRACK, ARTIST
    }
    
    /**
     * Default constructor.
     */
    public Favorite() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor for favorite track.
     * @param user the user
     * @param audioFile the favorite audio file
     */
    public Favorite(User user, AudioFile audioFile) {
        this();
        this.user = user;
        this.audioFile = audioFile;
        this.type = FavoriteType.TRACK;
    }
    
    /**
     * Constructor for favorite artist.
     * @param user the user
     * @param artistName the favorite artist name
     */
    public Favorite(User user, String artistName) {
        this();
        this.user = user;
        this.artistName = artistName;
        this.type = FavoriteType.ARTIST;
    }
    
    // Getters and setters
    
    /**
     * Gets the unique identifier for the favorite.
     * @return the favorite ID
     */
    public Long getId() { return id; }
    
    /**
     * Sets the unique identifier for the favorite.
     * @param id the favorite ID
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * Gets the user who marked this as favorite.
     * @return the user
     */
    public User getUser() { return user; }
    
    /**
     * Sets the user who marked this as favorite.
     * @param user the user
     */
    public void setUser(User user) { this.user = user; }
    
    /**
     * Gets the audio file marked as favorite.
     * @return the audio file (null for artist favorites)
     */
    public AudioFile getAudioFile() { return audioFile; }
    
    /**
     * Sets the audio file marked as favorite.
     * @param audioFile the audio file
     */
    public void setAudioFile(AudioFile audioFile) { this.audioFile = audioFile; }
    
    /**
     * Gets the artist name marked as favorite.
     * @return the artist name (null for track favorites)
     */
    public String getArtistName() { return artistName; }
    
    /**
     * Sets the artist name marked as favorite.
     * @param artistName the artist name
     */
    public void setArtistName(String artistName) { this.artistName = artistName; }
    
    /**
     * Gets the favorite type.
     * @return the favorite type (TRACK or ARTIST)
     */
    public FavoriteType getType() { return type; }
    
    /**
     * Sets the favorite type.
     * @param type the favorite type
     */
    public void setType(FavoriteType type) { this.type = type; }
    
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
}