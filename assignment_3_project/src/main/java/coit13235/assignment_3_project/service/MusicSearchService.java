package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.Favorite;
import coit13235.assignment_3_project.entity.Rating;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.AudioFileRepository;
import coit13235.assignment_3_project.repository.FavoriteRepository;
import coit13235.assignment_3_project.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simple Track class for iTunes search results.
 */
class Track {
    private String trackName;
    private String artistName;
    private String previewUrl;
    private String trackId;
    
    public String getTrackName() { return trackName; }
    public void setTrackName(String trackName) { this.trackName = trackName; }
    
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    
    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
}

/**
 * Service for managing music search and user preferences.
 */
@Service
public class MusicSearchService {

    private final AudioFileRepository audioFileRepository;
    private final FavoriteRepository favoriteRepository;
    private final RatingRepository ratingRepository;
    private final RestTemplate restTemplate;

    /**
     * Constructor for dependency injection.
     */
    public MusicSearchService(AudioFileRepository audioFileRepository,
                            FavoriteRepository favoriteRepository,
                            RatingRepository ratingRepository,
                            RestTemplate restTemplate) {
        this.audioFileRepository = audioFileRepository;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Searches iTunes for music tracks.
     * @param term the search term
     * @return list of track results
     */
    public Track[] searchItunes(String term) {
        try {
            String url = String.format(
                "https://itunes.apple.com/search?term=%s&media=music&entity=song&limit=20",
                java.net.URLEncoder.encode(term, "UTF-8")
            );
            
            // Use getForObject with proper error handling
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("results")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                
                if (results != null) {
                    System.out.println("iTunes search successful. Found " + results.size() + " results");
                    return results.stream()
                        .map(this::mapToTrack)
                        .toArray(Track[]::new);
                }
            }
        } catch (Exception e) {
            System.err.println("iTunes search failed: " + e.getMessage());
            e.printStackTrace();
        }
        return new Track[0];
    }
    
    /**
     * Maps a raw iTunes API result to a Track object.
     */
    private Track mapToTrack(Map<String, Object> result) {
        Track track = new Track();
        track.setTrackName((String) result.get("trackName"));
        track.setArtistName((String) result.get("artistName"));
        track.setPreviewUrl((String) result.get("previewUrl"));
        track.setTrackId(String.valueOf(result.get("trackId")));
        return track;
    }

    /**
     * Gets user preferences for a list of track IDs.
     * @param trackIds the iTunes track IDs
     * @param user the user
     * @return map of track ID to user preferences
     */
    public Map<String, UserTrackPreferences> getUserPreferences(List<String> trackIds, User user) {
        Map<String, UserTrackPreferences> preferences = new HashMap<>();
        
        if (user == null || trackIds == null || trackIds.isEmpty()) {
            return preferences;
        }
        
        try {
            // Get existing audio files for these tracks
            List<AudioFile> audioFiles = audioFileRepository.findByItunesTrackIdIn(trackIds);
            Map<String, AudioFile> audioFileMap = audioFiles.stream()
                .collect(Collectors.toMap(AudioFile::getItunesTrackId, af -> af));
            
            // Get user's favorites
            List<Favorite> userFavorites = favoriteRepository.findByUser(user);
            Set<Long> favoriteAudioFileIds = userFavorites.stream()
                .filter(f -> f.getAudioFile() != null)
                .map(f -> f.getAudioFile().getId())
                .collect(Collectors.toSet());
            
            Set<String> favoriteArtists = userFavorites.stream()
                .filter(f -> f.getArtistName() != null)
                .map(Favorite::getArtistName)
                .collect(Collectors.toSet());
            
            // Get user's ratings
            List<Rating> userRatings = ratingRepository.findByUser(user);
            Map<Long, Integer> ratingMap = userRatings.stream()
                .filter(r -> r.getAudioFile() != null)
                .collect(Collectors.toMap(r -> r.getAudioFile().getId(), Rating::getRating));
            
            // Build preferences for each track
            for (String trackId : trackIds) {
                UserTrackPreferences prefs = new UserTrackPreferences();
                AudioFile audioFile = audioFileMap.get(trackId);
                
                if (audioFile != null) {
                    prefs.setFavorited(favoriteAudioFileIds.contains(audioFile.getId()));
                    prefs.setRating(ratingMap.get(audioFile.getId()));
                    // Check if this track's artist is favorited
                    prefs.setArtistFavorited(favoriteArtists.contains(audioFile.getArtistName()));
                }
                
                preferences.put(trackId, prefs);
            }
        } catch (Exception e) {
            System.err.println("Failed to load user preferences: " + e.getMessage());
        }
        
        return preferences;
    }

    /**
     * Data transfer object for user track preferences.
     */
    public static class UserTrackPreferences {
        private boolean isFavorited = false;
        private boolean isArtistFavorited = false;
        private Integer rating = null;

        public boolean isFavorited() { return isFavorited; }
        public void setFavorited(boolean favorited) { isFavorited = favorited; }

        public boolean isArtistFavorited() { return isArtistFavorited; }
        public void setArtistFavorited(boolean artistFavorited) { isArtistFavorited = artistFavorited; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
    }
}