package coit13235.assignment_3_project.controller;

import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.service.AudioFileService;
import coit13235.assignment_3_project.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for audio file upload, listing, and streaming endpoints.
 */
@Controller
@RequestMapping("/api/audio")
public class AudioController {

    private final AudioFileService audioFileService;
    private final UserRepository userRepository;

    /**
     * Handles audio file upload from the UI form (Thymeleaf).
     * Redirects to chat page on success or error, and sets error message in model
     * if needed.
     *
     * @param authentication the authentication object
     * @param file           the uploaded audio file
     * @param model          the UI model for error messages
     * @return redirect or chat view
     */
    @PostMapping("/ui/upload-audio")
    public String uploadAudioUI(Authentication authentication, @RequestParam("file") MultipartFile file,
            org.springframework.ui.Model model) {
        if (authentication == null) {
            return "redirect:/ui/chat?error=auth";
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/ui/chat?error=user";
        }
        String contentType = (file != null) ? file.getContentType() : null;
        if (file == null || file.isEmpty() || contentType == null || !contentType.startsWith("audio/")) {
            model.addAttribute("uploadError", "Only audio files are allowed.");
            return "chat";
        }
        try {
            audioFileService.saveAudioFile(file, user);
            return "redirect:/ui/chat?success";
        } catch (IOException e) {
            model.addAttribute("uploadError", "Failed to upload audio file.");
            return "chat";
        }
    }

    /**
     * Constructor for dependency injection.
     * 
     * @param audioFileService the audio file service
     * @param userRepository   the user repository
     */
    public AudioController(AudioFileService audioFileService, UserRepository userRepository) {
        this.audioFileService = audioFileService;
        this.userRepository = userRepository;
    }

    /**
     * Handles audio file upload for authenticated users.
     * 
     * @param authentication the authentication object
     * @param file           the uploaded audio file
     * @return the saved AudioFile or error response
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> uploadAudio(Authentication authentication, @RequestParam("file") MultipartFile file) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        String contentType = (file != null) ? file.getContentType() : null;
        if (file == null || file.isEmpty() || contentType == null || !contentType.startsWith("audio/")) {
            return ResponseEntity.badRequest().body("Only audio files are allowed.");
        }
        try {
            AudioFile saved = audioFileService.saveAudioFile(file, user);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload audio file.");
        }
    }

    /**
     * Lists only actually uploaded audio files by the authenticated user.
     * This excludes iTunes tracks that were favorited but not uploaded.
     * 
     * @param authentication the authentication object
     * @return list of uploaded audio files for the user
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<AudioFile>> listAudioFiles(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(audioFileService.getUploadedFilesByUser(user.getId()));
    }

    /**
     * Streams the audio file with the given ID.
     * 
     * @param id the audio file ID
     * @return the audio file as a byte array, or 404 if not found
     */
    @GetMapping("/stream/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> streamAudio(@PathVariable Long id) {
        AudioFile audioFile = audioFileService.getAudioFile(id);
        if (audioFile == null || audioFile.getFileData() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + audioFile.getOriginalFilename())
                .contentType(MediaType.parseMediaType(audioFile.getContentType()))
                .body(audioFile.getFileData());
    }

    /**
     * Saves an audio track from iTunes search to the database.
     * 
     * @param authentication the authentication object
     * @param request        the iTunes track data
     * @return the saved AudioFile or existing one
     */
    @PostMapping("/save-from-itunes")
    @ResponseBody
    public ResponseEntity<?> saveFromItunes(Authentication authentication, @RequestBody ItunesTrackRequest request) {
        System.out.println("=== DEBUG: saveFromItunes called ===");

        if (authentication == null) {
            System.out.println("ERROR: Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            System.out.println("ERROR: User not found for username: " + authentication.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        System.out.println("DEBUG: User found: " + user.getUsername());

        if (request.getTrackName() == null || request.getArtistName() == null || request.getPreviewUrl() == null) {
            System.out.println("ERROR: Missing required fields. TrackName: " + request.getTrackName() + ", ArtistName: "
                    + request.getArtistName() + ", PreviewUrl: " + request.getPreviewUrl());
            return ResponseEntity.badRequest().body("Track name, artist name, and preview URL are required");
        }

        System.out.println("DEBUG: Saving track: " + request.getTrackName() + " by " + request.getArtistName());

        try {
            AudioFile existing = audioFileService.findByTrackNameAndArtistName(request.getTrackName(),
                    request.getArtistName());
            if (existing != null) {
                System.out.println("DEBUG: Track already exists, returning existing: " + existing.getId());
                return ResponseEntity.ok(existing);
            }
            AudioFile audioFile = audioFileService.saveFromItunes(
                    request.getTrackName(),
                    request.getArtistName(),
                    request.getPreviewUrl(),
                    request.getTrackId(),
                    user);
            System.out.println("DEBUG: Successfully saved new track with ID: " + audioFile.getId());
            return ResponseEntity.ok(audioFile);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to save iTunes track: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save iTunes track: " + e.getMessage());
        }
    }

    /**
     * Gets audio files by their iTunes track IDs.
     * 
     * @param trackIds list of iTunes track IDs
     * @return list of matching AudioFile objects
     */
    @PostMapping("/by-itunes-ids")
    @ResponseBody
    public ResponseEntity<List<AudioFile>> getByItunesIds(@RequestBody List<String> trackIds) {
        if (trackIds == null || trackIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        try {
            List<AudioFile> audioFiles = audioFileService.findByItunesIds(trackIds);
            return ResponseEntity.ok(audioFiles);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to get audio files by iTunes IDs: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DTO for iTunes track data received from the frontend.
     */
    public static class ItunesTrackRequest {
        private String trackName;
        private String artistName;
        private String previewUrl;
        private String trackId;

        public String getTrackName() {
            return trackName;
        }

        public void setTrackName(String trackName) {
            this.trackName = trackName;
        }

        public String getArtistName() {
            return artistName;
        }

        public void setArtistName(String artistName) {
            this.artistName = artistName;
        }

        public String getPreviewUrl() {
            return previewUrl;
        }

        public void setPreviewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
        }

        public String getTrackId() {
            return trackId;
        }

        public void setTrackId(String trackId) {
            this.trackId = trackId;
        }
    }

    /**
     * Deletes an audio file.
     * 
     * @param id             the audio file ID
     * @param authentication the authentication object
     * @return success or error response
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteAudioFile(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        try {
            AudioFile audioFile = audioFileService.getAudioFile(id);
            if (audioFile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Audio file not found");
            }

            // Check if user owns the file
            if (!audioFile.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only delete your own files");
            }

            // Delete the file
            audioFileService.deleteAudioFile(id);
            return ResponseEntity.ok().body("Audio file deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete audio file");
        }
    }
    
    /**
     * Cleans up duplicate iTunes-style records created for uploaded files.
     * 
     * @param authentication the authentication object
     * @return cleanup results
     */
    @PostMapping("/cleanup-duplicates")
    @ResponseBody
    public ResponseEntity<?> cleanupDuplicates(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }
        
        try {
            int cleaned = audioFileService.cleanupDuplicateUploadedFileRecords();
            return ResponseEntity.ok().body(Map.of(
                "message", "Cleanup completed", 
                "duplicatesRemoved", cleaned
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to cleanup duplicates: " + e.getMessage());
        }
    }
}
