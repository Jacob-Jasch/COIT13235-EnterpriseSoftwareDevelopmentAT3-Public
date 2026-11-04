package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.Favorite;
import coit13235.assignment_3_project.entity.Rating;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.AudioFileRepository;
import coit13235.assignment_3_project.repository.FavoriteRepository;
import coit13235.assignment_3_project.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MusicSearchService.
 */
@ExtendWith(MockitoExtension.class)
class MusicSearchServiceTests {

    @Mock
    private AudioFileRepository audioFileRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MusicSearchService musicSearchService;

    private User testUser;
    private AudioFile testAudioFile;
    private Favorite testTrackFavorite;
    private Favorite testArtistFavorite;
    private Rating testRating;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testAudioFile = new AudioFile();
        testAudioFile.setId(1L);
        testAudioFile.setTrackName("Test Track");
        testAudioFile.setArtistName("Test Artist");
        testAudioFile.setItunesTrackId("123456789");
        testAudioFile.setPreviewUrl("https://audio-ssl.itunes.apple.com/preview.m4a");

        testTrackFavorite = new Favorite(testUser, testAudioFile);
        testTrackFavorite.setId(1L);

        testArtistFavorite = new Favorite(testUser, "Test Artist");
        testArtistFavorite.setId(2L);

        testRating = new Rating(testUser, testAudioFile, 5);
        testRating.setId(1L);
    }

    @Test
    void testSearchItunes_Success() {
        // Given
        Map<String, Object> mockResponse = createMockItunesResponse();
        when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(mockResponse);

        // When
        Object[] results = musicSearchService.searchItunes("test song");

        // Then
        assertThat(results).hasSize(2);
        
        // We'll test using reflection or cast to Object and check properties through the service
        assertThat(results[0]).isNotNull();
        assertThat(results[1]).isNotNull();

        verify(restTemplate).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void testSearchItunes_EmptyResponse() {
        // Given
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("results", new ArrayList<>());
        when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(mockResponse);

        // When
        Object[] results = musicSearchService.searchItunes("nonexistent song");

        // Then
        assertThat(results).isEmpty();
        verify(restTemplate).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void testSearchItunes_NullResponse() {
        // Given
        when(restTemplate.getForObject(any(String.class), eq(Map.class))).thenReturn(null);

        // When
        Object[] results = musicSearchService.searchItunes("test song");

        // Then
        assertThat(results).isEmpty();
        verify(restTemplate).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void testSearchItunes_Exception() {
        // Given
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
                .thenThrow(new RestClientException("Connection error"));

        // When
        Object[] results = musicSearchService.searchItunes("test song");

        // Then
        assertThat(results).isEmpty();
        verify(restTemplate).getForObject(any(String.class), eq(Map.class));
    }

    @Test
    void testGetUserPreferences_WithFavoritesAndRatings() {
        // Given
        List<String> trackIds = Arrays.asList("123456789", "987654321");
        List<AudioFile> audioFiles = Arrays.asList(testAudioFile);
        List<Favorite> favorites = Arrays.asList(testTrackFavorite, testArtistFavorite);
        List<Rating> ratings = Arrays.asList(testRating);

        when(audioFileRepository.findByItunesTrackIdIn(trackIds)).thenReturn(audioFiles);
        when(favoriteRepository.findByUser(testUser)).thenReturn(favorites);
        when(ratingRepository.findByUser(testUser)).thenReturn(ratings);

        // When
        Map<String, MusicSearchService.UserTrackPreferences> result = 
                musicSearchService.getUserPreferences(trackIds, testUser);

        // Then
        assertThat(result).hasSize(2);
        
        // Check preferences for favorited track
        MusicSearchService.UserTrackPreferences prefs1 = result.get("123456789");
        assertThat(prefs1.isFavorited()).isTrue();
        assertThat(prefs1.isArtistFavorited()).isTrue();
        assertThat(prefs1.getRating()).isEqualTo(5);
        
        // Check preferences for non-favorited track
        MusicSearchService.UserTrackPreferences prefs2 = result.get("987654321");
        assertThat(prefs2.isFavorited()).isFalse();
        assertThat(prefs2.isArtistFavorited()).isFalse();
        assertThat(prefs2.getRating()).isNull();

        verify(audioFileRepository).findByItunesTrackIdIn(trackIds);
        verify(favoriteRepository).findByUser(testUser);
        verify(ratingRepository).findByUser(testUser);
    }

    @Test
    void testGetUserPreferences_NullUser() {
        // Given
        List<String> trackIds = Arrays.asList("123456789");

        // When
        Map<String, MusicSearchService.UserTrackPreferences> result = 
                musicSearchService.getUserPreferences(trackIds, null);

        // Then
        assertThat(result).isEmpty();
        verify(audioFileRepository, never()).findByItunesTrackIdIn(any());
        verify(favoriteRepository, never()).findByUser(any());
        verify(ratingRepository, never()).findByUser(any());
    }

    @Test
    void testGetUserPreferences_NullTrackIds() {
        // When
        Map<String, MusicSearchService.UserTrackPreferences> result = 
                musicSearchService.getUserPreferences(null, testUser);

        // Then
        assertThat(result).isEmpty();
        verify(audioFileRepository, never()).findByItunesTrackIdIn(any());
        verify(favoriteRepository, never()).findByUser(any());
        verify(ratingRepository, never()).findByUser(any());
    }

    @Test
    void testGetUserPreferences_EmptyTrackIds() {
        // When
        Map<String, MusicSearchService.UserTrackPreferences> result = 
                musicSearchService.getUserPreferences(new ArrayList<>(), testUser);

        // Then
        assertThat(result).isEmpty();
        verify(audioFileRepository, never()).findByItunesTrackIdIn(any());
        verify(favoriteRepository, never()).findByUser(any());
        verify(ratingRepository, never()).findByUser(any());
    }

    @Test
    void testGetUserPreferences_NoExistingData() {
        // Given
        List<String> trackIds = Arrays.asList("123456789");
        when(audioFileRepository.findByItunesTrackIdIn(trackIds)).thenReturn(new ArrayList<>());
        when(favoriteRepository.findByUser(testUser)).thenReturn(new ArrayList<>());
        when(ratingRepository.findByUser(testUser)).thenReturn(new ArrayList<>());

        // When
        Map<String, MusicSearchService.UserTrackPreferences> result = 
                musicSearchService.getUserPreferences(trackIds, testUser);

        // Then
        assertThat(result).hasSize(1);
        MusicSearchService.UserTrackPreferences prefs = result.get("123456789");
        assertThat(prefs.isFavorited()).isFalse();
        assertThat(prefs.isArtistFavorited()).isFalse();
        assertThat(prefs.getRating()).isNull();

        verify(audioFileRepository).findByItunesTrackIdIn(trackIds);
        verify(favoriteRepository).findByUser(testUser);
        verify(ratingRepository).findByUser(testUser);
    }

    @Test
    void testGetUserPreferences_OnlyArtistFavorites() {
        // Given
        List<String> trackIds = Arrays.asList("123456789");
        List<AudioFile> audioFiles = Arrays.asList(testAudioFile);
        List<Favorite> favorites = Arrays.asList(testArtistFavorite); // Only artist favorite, no track favorite

        when(audioFileRepository.findByItunesTrackIdIn(trackIds)).thenReturn(audioFiles);
        when(favoriteRepository.findByUser(testUser)).thenReturn(favorites);
        when(ratingRepository.findByUser(testUser)).thenReturn(new ArrayList<>());

        // When
        Map<String, MusicSearchService.UserTrackPreferences> result = 
                musicSearchService.getUserPreferences(trackIds, testUser);

        // Then
        assertThat(result).hasSize(1);
        MusicSearchService.UserTrackPreferences prefs = result.get("123456789");
        assertThat(prefs.isFavorited()).isFalse();
        assertThat(prefs.isArtistFavorited()).isTrue();
        assertThat(prefs.getRating()).isNull();
    }

    @Test
    void testGetUserPreferences_Exception() {
        // Given
        List<String> trackIds = Arrays.asList("123456789");
        when(audioFileRepository.findByItunesTrackIdIn(trackIds))
                .thenThrow(new RuntimeException("Database error"));

        // When
        Map<String, MusicSearchService.UserTrackPreferences> result = 
                musicSearchService.getUserPreferences(trackIds, testUser);

        // Then
        assertThat(result).isEmpty();
        verify(audioFileRepository).findByItunesTrackIdIn(trackIds);
    }

    @Test
    void testUserTrackPreferencesClass() {
        // Test the UserTrackPreferences class directly
        MusicSearchService.UserTrackPreferences prefs = new MusicSearchService.UserTrackPreferences();
        
        // Test defaults
        assertThat(prefs.isFavorited()).isFalse();
        assertThat(prefs.isArtistFavorited()).isFalse();
        assertThat(prefs.getRating()).isNull();
        
        // Test setters
        prefs.setFavorited(true);
        prefs.setArtistFavorited(true);
        prefs.setRating(4);
        
        assertThat(prefs.isFavorited()).isTrue();
        assertThat(prefs.isArtistFavorited()).isTrue();
        assertThat(prefs.getRating()).isEqualTo(4);
    }

    /**
     * Helper method to create a mock iTunes API response.
     */
    private Map<String, Object> createMockItunesResponse() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        
        // First track
        Map<String, Object> track1 = new HashMap<>();
        track1.put("trackName", "Test Song 1");
        track1.put("artistName", "Test Artist 1");
        track1.put("trackId", 123456789);
        track1.put("previewUrl", "https://audio-ssl.itunes.apple.com/preview1.m4a");
        results.add(track1);
        
        // Second track
        Map<String, Object> track2 = new HashMap<>();
        track2.put("trackName", "Test Song 2");
        track2.put("artistName", "Test Artist 2");
        track2.put("trackId", 987654321);
        track2.put("previewUrl", "https://audio-ssl.itunes.apple.com/preview2.m4a");
        results.add(track2);
        
        response.put("results", results);
        return response;
    }
}