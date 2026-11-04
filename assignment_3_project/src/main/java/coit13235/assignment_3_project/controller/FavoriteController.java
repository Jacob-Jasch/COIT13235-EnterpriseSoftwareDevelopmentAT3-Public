package coit13235.assignment_3_project.controller;

import coit13235.assignment_3_project.entity.Favorite;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.service.FavoriteService;
import coit13235.assignment_3_project.service.AudioFileService;
import coit13235.assignment_3_project.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for favorite management endpoints.
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AudioFileService audioFileService;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     * 
     * @param favoriteService  the favorite service
     * @param audioFileService the audio file service
     * @param userRepository   the user repository
     */
    @Autowired
    public FavoriteController(FavoriteService favoriteService, AudioFileService audioFileService,
            UserRepository userRepository) {
        this.favoriteService = favoriteService;
        this.audioFileService = audioFileService;
        this.userRepository = userRepository;
    }

    /**
     * Toggles favorite status for an audio track or artist.
     * 
     * @param authentication the authentication object
     * @param request        the favorite toggle request
     * @return success message or error response
     */
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(Authentication authentication, @RequestBody FavoriteToggleRequest request) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        if (request.getAudioFileId() == null
                && (request.getArtistName() == null || request.getArtistName().trim().isEmpty())) {
            return ResponseEntity.badRequest().body("Either audio file ID or artist name must be provided");
        }

        try {
            boolean isFavorited;
            if (request.getAudioFileId() != null) {
                AudioFile audioFile = audioFileService.getAudioFile(request.getAudioFileId());
                if (audioFile == null) {
                    return ResponseEntity.badRequest().body("Audio file not found");
                }
                isFavorited = favoriteService.toggleTrackFavorite(user, audioFile);
            } else {
                isFavorited = favoriteService.toggleArtistFavorite(user, request.getArtistName().trim());
            }

            String action = isFavorited ? "added" : "removed";
            String type = request.getAudioFileId() != null ? "track" : "artist";
            return ResponseEntity.ok(Map.of(
                    "message", String.format("Favorite %s %s successfully", action, type),
                    "isFavorited", isFavorited));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to toggle favorite");
        }
    }

    /**
     * Gets all favorites for the authenticated user.
     * 
     * @param authentication the authentication object
     * @return list of all user's favorites
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyFavorites(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Favorite> favorites = favoriteService.getAllFavoritesByUser(user);
        return ResponseEntity.ok(favorites);
    }

    /**
     * Gets all favorite tracks for the authenticated user.
     * 
     * @param authentication the authentication object
     * @return list of favorite tracks
     */
    @GetMapping("/tracks")
    public ResponseEntity<?> getFavoriteTracks(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Favorite> trackFavorites = favoriteService.getTrackFavoritesByUser(user);
        List<AudioFile> favoriteTracks = trackFavorites.stream()
                .map(Favorite::getAudioFile)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(favoriteTracks);
    }

    /**
     * Gets all favorite artists for the authenticated user.
     * 
     * @param authentication the authentication object
     * @return list of favorite artists
     */
    @GetMapping("/artists")
    public ResponseEntity<?> getFavoriteArtists(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Favorite> artistFavorites = favoriteService.getArtistFavoritesByUser(user);
        List<String> favoriteArtists = artistFavorites.stream()
                .map(Favorite::getArtistName)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(favoriteArtists);
    }

    /**
     * Checks if an audio file is favorited by the authenticated user.
     * 
     * @param audioFileId    the audio file ID
     * @param authentication the authentication object
     * @return favorite status
     */
    @GetMapping("/tracks/{audioFileId}/status")
    public ResponseEntity<?> isTrackFavorited(@PathVariable Long audioFileId, Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        AudioFile audioFile = audioFileService.getAudioFile(audioFileId);
        if (audioFile == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isFavorited = favoriteService.isTrackFavorited(user, audioFile);
        return ResponseEntity.ok(Map.of("isFavorited", isFavorited));
    }

    /**
     * Checks if an artist is favorited by the authenticated user.
     * 
     * @param artistName     the artist name
     * @param authentication the authentication object
     * @return favorite status
     */
    @GetMapping("/artists/{artistName}/status")
    public ResponseEntity<?> isArtistFavorited(@PathVariable String artistName, Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        boolean isFavorited = favoriteService.isArtistFavorited(user, artistName);
        return ResponseEntity.ok(Map.of("isFavorited", isFavorited));
    }

    /**
     * Removes a specific audio file from favorites.
     * 
     * @param audioFileId    the audio file ID
     * @param authentication the authentication object
     * @return success or error response
     */
    @DeleteMapping("/tracks/{audioFileId}")
    public ResponseEntity<?> removeTrackFromFavorites(@PathVariable Long audioFileId, Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        AudioFile audioFile = audioFileService.getAudioFile(audioFileId);
        if (audioFile == null) {
            return ResponseEntity.badRequest().body("Audio file not found");
        }

        try {
            boolean wasRemoved = favoriteService.removeTrackFromFavorites(user, audioFile);
            if (wasRemoved) {
                return ResponseEntity.ok(Map.of("message", "Track removed from favorites"));
            } else {
                return ResponseEntity.badRequest().body("Track was not in favorites");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove track from favorites");
        }
    }

    /**
     * Removes a specific artist from favorites.
     * 
     * @param artistName     the artist name
     * @param authentication the authentication object
     * @return success or error response
     */
    @DeleteMapping("/artists/{artistName}")
    public ResponseEntity<?> removeArtistFromFavorites(@PathVariable String artistName, Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        try {
            boolean wasRemoved = favoriteService.removeArtistFromFavorites(user, artistName);
            if (wasRemoved) {
                return ResponseEntity.ok(Map.of("message", "Artist removed from favorites"));
            } else {
                return ResponseEntity.badRequest().body("Artist was not in favorites");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove artist from favorites");
        }
    }

    /**
     * Gets count of favorites for the authenticated user.
     * 
     * @param authentication the authentication object
     * @return favorite counts
     */
    @GetMapping("/count")
    public ResponseEntity<?> getFavoriteCounts(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        long trackCount = favoriteService.getTrackFavoritesByUser(user).size();
        long artistCount = favoriteService.getArtistFavoritesByUser(user).size();

        return ResponseEntity.ok(Map.of(
                "favoriteTracksCount", trackCount,
                "favoriteArtistsCount", artistCount,
                "totalFavoritesCount", trackCount + artistCount));
    }

    /**
     * Request class for favorite toggle operations.
     * Contains the audio file ID and artist name for the favorite action.
     */
    public static class FavoriteToggleRequest {
        private Long audioFileId;
        private String artistName;

        // Getters and setters
        public Long getAudioFileId() {
            return audioFileId;
        }

        public void setAudioFileId(Long audioFileId) {
            this.audioFileId = audioFileId;
        }

        public String getArtistName() {
            return artistName;
        }

        public void setArtistName(String artistName) {
            this.artistName = artistName;
        }
    }
}