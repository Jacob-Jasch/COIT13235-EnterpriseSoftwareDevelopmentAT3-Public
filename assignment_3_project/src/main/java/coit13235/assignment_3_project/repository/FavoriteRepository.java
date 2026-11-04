package coit13235.assignment_3_project.repository;

import coit13235.assignment_3_project.entity.Favorite;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Favorite entities.
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * Finds all favorites by a specific user.
     * @param user the user
     * @return list of favorites by the user
     */
    List<Favorite> findByUser(User user);
    
    /**
     * Finds all track favorites by a specific user.
     * @param user the user
     * @return list of track favorites by the user
     */
    @Query("SELECT f FROM Favorite f WHERE f.user = :user AND f.type = 'TRACK'")
    List<Favorite> findTrackFavoritesByUser(@Param("user") User user);
    
    /**
     * Finds all artist favorites by a specific user.
     * @param user the user
     * @return list of artist favorites by the user
     */
    @Query("SELECT f FROM Favorite f WHERE f.user = :user AND f.type = 'ARTIST'")
    List<Favorite> findArtistFavoritesByUser(@Param("user") User user);
    
    /**
     * Finds a specific track favorite by user and audio file.
     * @param user the user
     * @param audioFile the audio file
     * @return the favorite if found
     */
    Optional<Favorite> findByUserAndAudioFile(User user, AudioFile audioFile);
    
    /**
     * Finds a specific artist favorite by user and artist name.
     * @param user the user
     * @param artistName the artist name
     * @return the favorite if found
     */
    Optional<Favorite> findByUserAndArtistName(User user, String artistName);
    
    /**
     * Checks if a user has favorited a specific track.
     * @param user the user
     * @param audioFile the audio file
     * @return true if favorited, false otherwise
     */
    boolean existsByUserAndAudioFile(User user, AudioFile audioFile);
    
    /**
     * Checks if a user has favorited a specific artist.
     * @param user the user
     * @param artistName the artist name
     * @return true if favorited, false otherwise
     */
    boolean existsByUserAndArtistName(User user, String artistName);
}