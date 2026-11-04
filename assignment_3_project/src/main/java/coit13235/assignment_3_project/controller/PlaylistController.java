package coit13235.assignment_3_project.controller;

import coit13235.assignment_3_project.entity.Playlist;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.service.PlaylistService;
import coit13235.assignment_3_project.service.AudioFileService;
import coit13235.assignment_3_project.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for playlist management endpoints.
 */
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final AudioFileService audioFileService;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     * 
     * @param playlistService  the playlist service
     * @param audioFileService the audio file service
     * @param userRepository   the user repository
     */
    @Autowired
    public PlaylistController(PlaylistService playlistService, AudioFileService audioFileService,
            UserRepository userRepository) {
        this.playlistService = playlistService;
        this.audioFileService = audioFileService;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new playlist.
     * 
     * @param authentication the authentication object
     * @param request        the playlist creation request
     * @return the created playlist or error response
     */
    @PostMapping
    public ResponseEntity<?> createPlaylist(Authentication authentication, @RequestBody PlaylistRequest request) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Playlist name is required");
        }

        try {
            Playlist playlist = playlistService.createPlaylist(
                    request.getName().trim(),
                    request.getDescription(),
                    request.isPublic(),
                    user);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create playlist");
        }
    }

    /**
     * Gets all playlists owned by the authenticated user.
     * 
     * @param authentication the authentication object
     * @return list of user's playlists
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyPlaylists(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Playlist> playlists = playlistService.getPlaylistsByOwner(user);
        return ResponseEntity.ok(playlists);
    }

    /**
     * Gets all public playlists.
     * 
     * @return list of public playlists
     */
    @GetMapping("/public")
    public ResponseEntity<List<Playlist>> getPublicPlaylists() {
        List<Playlist> playlists = playlistService.getPublicPlaylists();
        return ResponseEntity.ok(playlists);
    }

    /**
     * Searches for playlists by name.
     * 
     * @param name       the search term
     * @param publicOnly whether to search only public playlists
     * @return list of matching playlists
     */
    @GetMapping("/search")
    public ResponseEntity<List<Playlist>> searchPlaylists(
            @RequestParam String name,
            @RequestParam(defaultValue = "true") boolean publicOnly) {
        List<Playlist> playlists = publicOnly
                ? playlistService.searchPublicPlaylistsByName(name)
                : playlistService.searchPlaylistsByName(name);
        return ResponseEntity.ok(playlists);
    }

    /**
     * Gets a specific playlist by ID.
     * 
     * @param id             the playlist ID
     * @param authentication the authentication object
     * @return the playlist if accessible
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylist(@PathVariable Long id, Authentication authentication) {
        Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Playlist playlist = playlistOpt.get();

        User user = null;
        if (authentication != null) {
            user = userRepository.findByUsername(authentication.getName());
        }

        if (!playlistService.canUserAccessPlaylist(playlist, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(playlist);
    }

    /**
     * Gets tracks from a playlist.
     * 
     * @param id             the playlist ID
     * @param authentication the authentication object
     * @return the playlist tracks if accessible
     */
    @GetMapping("/{id}/tracks")
    public ResponseEntity<?> getPlaylistTracks(@PathVariable Long id, Authentication authentication) {
        Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Playlist playlist = playlistOpt.get();
        User user = null;
        if (authentication != null) {
            user = userRepository.findByUsername(authentication.getName());
        }

        if (!playlistService.canUserAccessPlaylist(playlist, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        return ResponseEntity.ok(playlist.getAudioFiles());
    }

    /**
     * Updates a playlist.
     * 
     * @param id             the playlist ID
     * @param authentication the authentication object
     * @param request        the update request
     * @return the updated playlist or error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlaylist(@PathVariable Long id, Authentication authentication,
            @RequestBody PlaylistRequest request) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Playlist playlist = playlistOpt.get();
        if (!playlistService.canUserModifyPlaylist(playlist, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Playlist name is required");
        }

        try {
            Playlist updated = playlistService.updatePlaylist(
                    playlist,
                    request.getName().trim(),
                    request.getDescription(),
                    request.isPublic());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update playlist");
        }
    }

    /**
     * Adds an audio file to a playlist.
     * 
     * @param id             the playlist ID
     * @param audioFileId    the audio file ID
     * @param authentication the authentication object
     * @return success or error response
     */
    @PostMapping("/{id}/tracks/{audioFileId}")
    public ResponseEntity<?> addTrackToPlaylist(@PathVariable Long id, @PathVariable Long audioFileId,
            Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Playlist playlist = playlistOpt.get();
        if (!playlistService.canUserModifyPlaylist(playlist, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        AudioFile audioFile = audioFileService.getAudioFile(audioFileId);
        if (audioFile == null) {
            return ResponseEntity.badRequest().body("Audio file not found");
        }

        try {
            Playlist updated = playlistService.addAudioFileToPlaylist(playlist, audioFile);
            return ResponseEntity.ok(Map.of("message", "Track added to playlist", "playlist", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add track to playlist");
        }
    }

    /**
     * Removes an audio file from a playlist.
     * 
     * @param id             the playlist ID
     * @param audioFileId    the audio file ID
     * @param authentication the authentication object
     * @return success or error response
     */
    @DeleteMapping("/{id}/tracks/{audioFileId}")
    public ResponseEntity<?> removeTrackFromPlaylist(@PathVariable Long id, @PathVariable Long audioFileId,
            Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Playlist playlist = playlistOpt.get();
        if (!playlistService.canUserModifyPlaylist(playlist, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        AudioFile audioFile = audioFileService.getAudioFile(audioFileId);
        if (audioFile == null) {
            return ResponseEntity.badRequest().body("Audio file not found");
        }

        try {
            Playlist updated = playlistService.removeAudioFileFromPlaylist(playlist, audioFile);
            return ResponseEntity.ok(Map.of("message", "Track removed from playlist", "playlist", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove track from playlist");
        }
    }

    /**
     * Deletes a playlist.
     * 
     * @param id             the playlist ID
     * @param authentication the authentication object
     * @return success or error response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long id, Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        Optional<Playlist> playlistOpt = playlistService.getPlaylistById(id);
        if (!playlistOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Playlist playlist = playlistOpt.get();
        if (!playlistService.canUserModifyPlaylist(playlist, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try {
            playlistService.deletePlaylist(playlist);
            return ResponseEntity.ok(Map.of("message", "Playlist deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete playlist");
        }
    }

    /**
     * Request class for playlist creation and updates.
     */
    public static class PlaylistRequest {
        private String name;
        private String description;
        private boolean isPublic;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public void setPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }
    }
}