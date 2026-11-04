package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.AudioFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing audio file storage and retrieval.
 */
@Service
public class AudioFileService {
    /**
     * Finds audio files by a single iTunes track ID.
     * @param itunesTrackId the iTunes track ID
     * @return list of matching AudioFile entities
     */
    public List<AudioFile> findByItunesTrackId(String itunesTrackId) {
        if (itunesTrackId == null) return List.of();
        return audioFileRepository.findByItunesTrackIdIn(List.of(itunesTrackId));
    }
    private final AudioFileRepository audioFileRepository;

    /**
     * Constructor for dependency injection.
     * @param audioFileRepository the audio file repository
     */
    public AudioFileService(AudioFileRepository audioFileRepository) {
        this.audioFileRepository = audioFileRepository;
    }

    /**
     * Saves an uploaded audio file for a user.
     * @param file the uploaded file
     * @param user the user uploading the file
     * @return the saved AudioFile entity
     * @throws IOException if file reading fails
     */
    public AudioFile saveAudioFile(MultipartFile file, User user) throws IOException {
        String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        AudioFile audioFile = new AudioFile();
        audioFile.setFilename(uniqueFilename);
        audioFile.setOriginalFilename(file.getOriginalFilename());
        audioFile.setContentType(file.getContentType());
        audioFile.setSize(file.getSize());
        audioFile.setUser(user);
        audioFile.setFileData(file.getBytes()); // Store file data in database
        audioFile.setUploadedAt(java.time.LocalDateTime.now());
        return audioFileRepository.save(audioFile);
    }

    /**
     * Gets all audio files uploaded by a specific user.
     * @param userId the user ID
     * @return list of audio files for the user
     */
    public List<AudioFile> getAudioFilesByUser(Long userId) {
        return audioFileRepository.findByUserId(userId);
    }
    
    /**
     * Gets only actually uploaded audio files by a specific user (excludes favorited iTunes tracks).
     * @param userId the user ID
     * @return list of uploaded audio files for the user
     */
    public List<AudioFile> getUploadedFilesByUser(Long userId) {
        return audioFileRepository.findByUserIdAndFileDataIsNotNull(userId);
    }

    /**
     * Gets an audio file by its ID.
     * @param id the audio file ID
     * @return the AudioFile entity, or null if not found
     */
    public AudioFile getAudioFile(Long id) {
        return audioFileRepository.findById(id).orElse(null);
    }


    
    /**
     * Finds an audio file by track name and artist name.
     * @param trackName the track name
     * @param artistName the artist name
     * @return the AudioFile if found, null otherwise
     */
    public AudioFile findByTrackNameAndArtistName(String trackName, String artistName) {
        List<AudioFile> files = audioFileRepository.findByTrackNameAndArtistName(trackName, artistName);
        if (files == null || files.isEmpty()) {
            return null;
        }
        // If multiple, log a warning and return the first
        if (files.size() > 1) {
            System.err.println("WARNING: Multiple AudioFile records found for track '" + trackName + "' and artist '" + artistName + "'. Returning the first.");
        }
        return files.get(0);
    }
    
    /**
     * Saves an audio track from iTunes data.
     * @param trackName the track name
     * @param artistName the artist name
     * @param previewUrl the preview URL
     * @param trackId the iTunes track ID
     * @param user the user saving the track
     * @return the saved AudioFile entity
     */
    public AudioFile saveFromItunes(String trackName, String artistName, String previewUrl, String trackId, User user) {
        AudioFile audioFile = new AudioFile();
        audioFile.setTrackName(trackName);
        audioFile.setArtistName(artistName);
        audioFile.setPreviewUrl(previewUrl);
        audioFile.setItunesTrackId(trackId);
        audioFile.setUser(user);
        
        // Set default values for file-based fields since this is from iTunes
        audioFile.setFilename(null); // No local file
        audioFile.setOriginalFilename(trackName + " - " + artistName + ".m4a");
        audioFile.setContentType("audio/mp4");
        audioFile.setSize(0L); // Unknown size for preview
        
        return audioFileRepository.save(audioFile);
    }
    
    /**
     * Finds audio files by their iTunes track IDs.
     * @param itunesIds the list of iTunes track IDs
     * @return list of matching AudioFile entities
     */
    public List<AudioFile> findByItunesIds(List<String> itunesIds) {
        return audioFileRepository.findByItunesTrackIdIn(itunesIds);
    }

    /**
     * Deletes an audio file by ID.
     * @param id the audio file ID
     */
    public void deleteAudioFile(Long id) {
        AudioFile audioFile = audioFileRepository.findById(id).orElse(null);
        if (audioFile == null) {
            return; // File not found in database
        }

        // Delete from database (file data is stored in database)
        audioFileRepository.deleteById(id);
    }
    
    /**
     * Cleans up duplicate iTunes-style records created for uploaded files.
     * These records have iTunes track IDs starting with "uploaded-" but no file data.
     * @return number of duplicate records cleaned up
     */
    public int cleanupDuplicateUploadedFileRecords() {
        List<AudioFile> duplicates = audioFileRepository.findByItunesTrackIdStartingWith("uploaded-");
        int cleanedUp = 0;
        
        for (AudioFile duplicate : duplicates) {
            // Only delete if this is clearly a duplicate iTunes-style record (no file data)
            if (duplicate.getFileData() == null) {
                audioFileRepository.delete(duplicate);
                cleanedUp++;
                System.out.println("Cleaned up duplicate record for uploaded file: " + duplicate.getItunesTrackId());
            }
        }
        
        return cleanedUp;
    }
}
