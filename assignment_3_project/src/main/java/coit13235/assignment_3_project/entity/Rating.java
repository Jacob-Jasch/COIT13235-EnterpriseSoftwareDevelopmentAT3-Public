package coit13235.assignment_3_project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a user's rating of an audio track.
 */
@Entity
@Table(name = "rating")
public class Rating {
    
    /**
     * Unique identifier for the rating.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The user who gave the rating.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * The audio file being rated.
     */
    @ManyToOne
    @JoinColumn(name = "audiofile_id", nullable = false)
    private AudioFile audioFile;
    
    /**
     * The rating value (1-5 stars).
     */
    @Column(nullable = false)
    private int rating;
    
    /**
     * Timestamp when the rating was created.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the rating was last updated.
     */
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor.
     */
    public Rating() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with user, audio file, and rating value.
     * @param user the user giving the rating
     * @param audioFile the audio file being rated
     * @param rating the rating value (1-5)
     */
    public Rating(User user, AudioFile audioFile, int rating) {
        this();
        this.user = user;
        this.audioFile = audioFile;
        this.rating = rating;
    }
    
    // Getters and setters
    
    /**
     * Gets the unique identifier for the rating.
     * @return the rating ID
     */
    public Long getId() { return id; }
    
    /**
     * Sets the unique identifier for the rating.
     * @param id the rating ID
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * Gets the user who gave the rating.
     * @return the user
     */
    public User getUser() { return user; }
    
    /**
     * Sets the user who gave the rating.
     * @param user the user
     */
    public void setUser(User user) { this.user = user; }
    
    /**
     * Gets the audio file being rated.
     * @return the audio file
     */
    public AudioFile getAudioFile() { return audioFile; }
    
    /**
     * Sets the audio file being rated.
     * @param audioFile the audio file
     */
    public void setAudioFile(AudioFile audioFile) { this.audioFile = audioFile; }
    
    /**
     * Gets the rating value.
     * @return the rating value (1-5 stars)
     */
    public int getRating() { return rating; }
    
    /**
     * Sets the rating value.
     * @param rating the rating value (1-5 stars)
     */
    public void setRating(int rating) { 
        this.rating = rating;
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