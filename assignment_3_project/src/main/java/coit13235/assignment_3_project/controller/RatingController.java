package coit13235.assignment_3_project.controller;

import coit13235.assignment_3_project.entity.Rating;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.service.RatingService;
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
 * REST controller for rating management endpoints.
 */
@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final AudioFileService audioFileService;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     * 
     * @param ratingService    the rating service
     * @param audioFileService the audio file service
     * @param userRepository   the user repository
     */
    @Autowired
    public RatingController(RatingService ratingService, AudioFileService audioFileService,
            UserRepository userRepository) {
        this.ratingService = ratingService;
        this.audioFileService = audioFileService;
        this.userRepository = userRepository;
    }

    /**
     * Rates an audio file or updates existing rating.
     * 
     * @param authentication the authentication object
     * @param request        the rating request
     * @return the rating or error response
     */
    @PostMapping
    public ResponseEntity<?> rateAudioFile(Authentication authentication, @RequestBody RatingRequest request) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        if (request.getAudioFileId() == null) {
            return ResponseEntity.badRequest().body("Audio file ID is required");
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        AudioFile audioFile = audioFileService.getAudioFile(request.getAudioFileId());
        if (audioFile == null) {
            return ResponseEntity.badRequest().body("Audio file not found");
        }

        try {
            Rating rating = ratingService.rateAudioFile(user, audioFile, request.getRating(), null);
            return ResponseEntity.ok(rating);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save rating");
        }
    }

    /**
     * Gets all ratings by the authenticated user.
     * 
     * @param authentication the authentication object
     * @return list of user's ratings
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyRatings(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Rating> ratings = ratingService.getRatingsByUser(user);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Gets all ratings for a specific audio file.
     * 
     * @param audioFileId the audio file ID
     * @return list of ratings for the audio file
     */
    @GetMapping("/audiofile/{audioFileId}")
    public ResponseEntity<?> getRatingsForAudioFile(@PathVariable Long audioFileId) {
        AudioFile audioFile = audioFileService.getAudioFile(audioFileId);
        if (audioFile == null) {
            return ResponseEntity.notFound().build();
        }

        List<Rating> ratings = ratingService.getRatingsForAudioFile(audioFile);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Gets the authenticated user's rating for a specific audio file.
     * 
     * @param audioFileId    the audio file ID
     * @param authentication the authentication object
     * @return the user's rating if found
     */
    @GetMapping("/audiofile/{audioFileId}/my")
    public ResponseEntity<?> getMyRatingForAudioFile(@PathVariable Long audioFileId, Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        AudioFile audioFile = audioFileService.getAudioFile(audioFileId);
        if (audioFile == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<Rating> rating = ratingService.getUserRatingForAudioFile(user, audioFile);
        if (rating.isPresent()) {
            return ResponseEntity.ok(rating.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gets rating statistics for an audio file.
     * 
     * @param audioFileId the audio file ID
     * @return rating statistics
     */
    @GetMapping("/audiofile/{audioFileId}/stats")
    public ResponseEntity<?> getRatingStatistics(@PathVariable Long audioFileId) {
        AudioFile audioFile = audioFileService.getAudioFile(audioFileId);
        if (audioFile == null) {
            return ResponseEntity.notFound().build();
        }

        RatingService.RatingStatistics stats = ratingService.getRatingStatistics(audioFile);
        return ResponseEntity.ok(Map.of(
                "averageRating", stats.getAverageRating(),
                "ratingCount", stats.getRatingCount(),
                "audioFileId", audioFileId));
    }

    /**
     * Gets all ratings with track information for public viewing.
     * 
     * @return list of all ratings with track details
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllRatings() {
        try {
            List<Rating> allRatings = ratingService.getAllRatings();

            List<Map<String, Object>> ratingViews = allRatings.stream()
                    .map(rating -> {
                        AudioFile audioFile = rating.getAudioFile();
                        return Map.<String, Object>of(
                                "id", rating.getId(),
                                "rating", rating.getRating(),
                                "username", rating.getUser().getUsername(),
                                "trackName", audioFile.getTrackName(),
                                "artistName", audioFile.getArtistName(),
                                "createdAt", rating.getCreatedAt());
                    })
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(ratingViews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to load ratings");
        }
    }

    /**
     * Updates an existing rating.
     * 
     * @param ratingId       the rating ID
     * @param request        the rating update request
     * @param authentication the authentication object
     * @return the updated rating or error response
     */
    @PutMapping("/{ratingId}")
    public ResponseEntity<?> updateRating(@PathVariable Long ratingId, @RequestBody RatingRequest request, 
            Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        if (request.getRating() < 1 || request.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }

        try {
            // Find the rating and verify ownership
            Optional<Rating> ratingOpt = ratingService.getRatingsByUser(user).stream()
                    .filter(r -> r.getId().equals(ratingId))
                    .findFirst();

            if (!ratingOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Rating rating = ratingOpt.get();
            if (!rating.getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            // Update the rating
            rating.setRating(request.getRating());
            rating.setUpdatedAt(java.time.LocalDateTime.now());
            Rating updatedRating = ratingService.rateAudioFile(rating.getUser(), rating.getAudioFile(), 
                    request.getRating(), null);
            
            return ResponseEntity.ok(updatedRating);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update rating");
        }
    }

    /**
     * Deletes a rating.
     * 
     * @param ratingId       the rating ID
     * @param authentication the authentication object
     * @return success or error response
     */
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long ratingId, Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");

        Optional<Rating> ratingOpt = ratingService.getRatingsByUser(user).stream()
                .filter(r -> r.getId().equals(ratingId))
                .findFirst();

        if (!ratingOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Rating rating = ratingOpt.get();
        if (!rating.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try {
            ratingService.deleteRating(rating);
            return ResponseEntity.ok(Map.of("message", "Rating deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete rating");
        }
    }

    /**
     * Request class for rating creation and updates.
     */
    public static class RatingRequest {
        private Long audioFileId;
        private int rating;

        // Getters and setters
        public Long getAudioFileId() {
            return audioFileId;
        }

        public void setAudioFileId(Long audioFileId) {
            this.audioFileId = audioFileId;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }
}