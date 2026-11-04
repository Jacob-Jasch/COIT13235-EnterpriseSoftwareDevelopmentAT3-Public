package coit13235.assignment_3_project.repository;

import coit13235.assignment_3_project.entity.Rating;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Rating entities.
 */
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    /**
     * Finds all ratings by a specific user.
     * @param user the user who gave the ratings
     * @return list of ratings by the user
     */
    List<Rating> findByUser(User user);
    
    /**
     * Finds all ratings for a specific audio file.
     * @param audioFile the audio file
     * @return list of ratings for the audio file
     */
    List<Rating> findByAudioFile(AudioFile audioFile);
    
    /**
     * Finds a specific rating by user and audio file.
     * @param user the user
     * @param audioFile the audio file
     * @return the rating if found
     */
    Optional<Rating> findByUserAndAudioFile(User user, AudioFile audioFile);
    
    /**
     * Calculates the average rating for a specific audio file.
     * @param audioFileId the audio file ID
     * @return the average rating
     */
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.audioFile.id = :audioFileId")
    Double getAverageRatingForAudioFile(@Param("audioFileId") Long audioFileId);
    
    /**
     * Counts the number of ratings for a specific audio file.
     * @param audioFileId the audio file ID
     * @return the count of ratings
     */
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.audioFile.id = :audioFileId")
    Long getRatingCountForAudioFile(@Param("audioFileId") Long audioFileId);
}