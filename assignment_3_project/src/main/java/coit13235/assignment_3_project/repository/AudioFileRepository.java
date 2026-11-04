package coit13235.assignment_3_project.repository;

import coit13235.assignment_3_project.entity.AudioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for AudioFile entities.
 */
public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {
    /**
     * Finds all audio files uploaded by a specific user.
     * @param userId the user ID
     * @return list of audio files for the user
     */
    List<AudioFile> findByUserId(Long userId);
    
    /**
     * Finds an audio file by track name and artist name.
     * @param trackName the track name
     * @param artistName the artist name
     * @return the AudioFile if found, null otherwise
     */
    List<AudioFile> findByTrackNameAndArtistName(String trackName, String artistName);
    
    /**
     * Finds audio files by their iTunes track IDs.
     * @param itunesTrackIds the list of iTunes track IDs
     * @return list of matching AudioFile entities
     */
    List<AudioFile> findByItunesTrackIdIn(List<String> itunesTrackIds);
    
    /**
     * Finds duplicate iTunes-style records for uploaded files (should be cleaned up).
     * @return list of AudioFile entities with iTunes track IDs starting with "uploaded-"
     */
    List<AudioFile> findByItunesTrackIdStartingWith(String prefix);
    
    /**
     * Finds only actually uploaded audio files by a specific user (those with file data).
     * This excludes iTunes tracks that were favorited but not uploaded.
     * @param userId the user ID
     * @return list of uploaded audio files for the user
     */
    List<AudioFile> findByUserIdAndFileDataIsNotNull(Long userId);
}
