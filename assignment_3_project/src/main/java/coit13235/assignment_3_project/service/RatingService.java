package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.Rating;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing audio track ratings.
 */
@Service
public class RatingService {
    
    private final RatingRepository ratingRepository;
    
    /**
     * Constructor for dependency injection.
     * @param ratingRepository the rating repository
     */
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }
    
    /**
     * Rates an audio file or updates existing rating.
     * @param user the user giving the rating
     * @param audioFile the audio file being rated
     * @param ratingValue the rating value (1-5)
     * @param comment unused parameter (kept for compatibility)
     * @return the rating entity
     */
    @Transactional
    public Rating rateAudioFile(User user, AudioFile audioFile, int ratingValue, String comment) {
        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        Optional<Rating> existingRating = ratingRepository.findByUserAndAudioFile(user, audioFile);
        
        if (existingRating.isPresent()) {
            // Update existing rating
            Rating rating = existingRating.get();
            rating.setRating(ratingValue);
            rating.setUpdatedAt(LocalDateTime.now());
            return ratingRepository.save(rating);
        } else {
            // Create new rating
            Rating rating = new Rating(user, audioFile, ratingValue);
            return ratingRepository.save(rating);
        }
    }
    
    /**
     * Gets all ratings by a user.
     * @param user the user
     * @return list of ratings by the user
     */
    public List<Rating> getRatingsByUser(User user) {
        return ratingRepository.findByUser(user);
    }
    
    /**
     * Gets all ratings for an audio file.
     * @param audioFile the audio file
     * @return list of ratings for the audio file
     */
    public List<Rating> getRatingsForAudioFile(AudioFile audioFile) {
        return ratingRepository.findByAudioFile(audioFile);
    }
    
    /**
     * Gets a user's rating for a specific audio file.
     * @param user the user
     * @param audioFile the audio file
     * @return the rating if found
     */
    public Optional<Rating> getUserRatingForAudioFile(User user, AudioFile audioFile) {
        return ratingRepository.findByUserAndAudioFile(user, audioFile);
    }
    
    /**
     * Gets the average rating for an audio file.
     * @param audioFile the audio file
     * @return the average rating, or null if no ratings
     */
    public Double getAverageRating(AudioFile audioFile) {
        return ratingRepository.getAverageRatingForAudioFile(audioFile.getId());
    }
    
    /**
     * Gets the count of ratings for an audio file.
     * @param audioFile the audio file
     * @return the count of ratings
     */
    public Long getRatingCount(AudioFile audioFile) {
        return ratingRepository.getRatingCountForAudioFile(audioFile.getId());
    }
    
    /**
     * Deletes a rating.
     * @param rating the rating to delete
     */
    @Transactional
    public void deleteRating(Rating rating) {
        ratingRepository.delete(rating);
    }
    
    /**
     * Gets rating statistics for an audio file.
     * @param audioFile the audio file
     * @return rating statistics object
     */
    public RatingStatistics getRatingStatistics(AudioFile audioFile) {
        Double average = getAverageRating(audioFile);
        Long count = getRatingCount(audioFile);
        return new RatingStatistics(average != null ? average : 0.0, count);
    }
    
    /**
     * Gets all ratings in the system.
     * @return list of all ratings
     */
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }
    
    /**
     * Inner class for rating statistics.
     */
    public static class RatingStatistics {
        private final double averageRating;
        private final long ratingCount;
        
        public RatingStatistics(double averageRating, long ratingCount) {
            this.averageRating = averageRating;
            this.ratingCount = ratingCount;
        }
        
        public double getAverageRating() { return averageRating; }
        public long getRatingCount() { return ratingCount; }
    }
}