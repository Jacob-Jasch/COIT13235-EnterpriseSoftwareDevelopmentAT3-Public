package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.Rating;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RatingService.
 */
@ExtendWith(MockitoExtension.class)
class RatingServiceTests {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private User testUser;
    private User anotherUser;
    private AudioFile testAudioFile;
    private Rating testRating;
    private Rating anotherRating;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");

        testAudioFile = new AudioFile();
        testAudioFile.setId(1L);
        testAudioFile.setFilename("test.mp3");
        testAudioFile.setTrackName("Test Track");
        testAudioFile.setArtistName("Test Artist");

        testRating = new Rating(testUser, testAudioFile, 4);
        testRating.setId(1L);
        testRating.setCreatedAt(LocalDateTime.now());

        anotherRating = new Rating(anotherUser, testAudioFile, 5);
        anotherRating.setId(2L);
        anotherRating.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testRateAudioFile_NewRating() {
        // Given
        when(ratingRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        // When
        Rating result = ratingService.rateAudioFile(testUser, testAudioFile, 4, "comment");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getAudioFile()).isEqualTo(testAudioFile);
        assertThat(result.getRating()).isEqualTo(4);
        verify(ratingRepository).findByUserAndAudioFile(testUser, testAudioFile);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void testRateAudioFile_UpdateExistingRating() {
        // Given
        when(ratingRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.of(testRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        // When
        Rating result = ratingService.rateAudioFile(testUser, testAudioFile, 5, "updated comment");

        // Then
        assertThat(result).isNotNull();
        assertThat(testRating.getRating()).isEqualTo(5);
        assertThat(testRating.getUpdatedAt()).isNotNull();
        verify(ratingRepository).findByUserAndAudioFile(testUser, testAudioFile);
        verify(ratingRepository).save(testRating);
    }

    @Test
    void testRateAudioFile_InvalidRatingTooLow() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.rateAudioFile(testUser, testAudioFile, 0, "comment");
        });
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void testRateAudioFile_InvalidRatingTooHigh() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.rateAudioFile(testUser, testAudioFile, 6, "comment");
        });
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void testRateAudioFile_ValidRatingBoundaries() {
        // Given
        when(ratingRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        // Test minimum valid rating (1)
        ratingService.rateAudioFile(testUser, testAudioFile, 1, "comment");
        verify(ratingRepository, times(1)).save(any(Rating.class));

        // Reset mock
        reset(ratingRepository);
        when(ratingRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        // Test maximum valid rating (5)
        ratingService.rateAudioFile(testUser, testAudioFile, 5, "comment");
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void testGetRatingsByUser() {
        // Given
        List<Rating> expectedRatings = Arrays.asList(testRating);
        when(ratingRepository.findByUser(testUser)).thenReturn(expectedRatings);

        // When
        List<Rating> result = ratingService.getRatingsByUser(testUser);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testRating);
        verify(ratingRepository).findByUser(testUser);
    }

    @Test
    void testGetRatingsForAudioFile() {
        // Given
        List<Rating> expectedRatings = Arrays.asList(testRating, anotherRating);
        when(ratingRepository.findByAudioFile(testAudioFile)).thenReturn(expectedRatings);

        // When
        List<Rating> result = ratingService.getRatingsForAudioFile(testAudioFile);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testRating, anotherRating);
        verify(ratingRepository).findByAudioFile(testAudioFile);
    }

    @Test
    void testGetUserRatingForAudioFile_Found() {
        // Given
        when(ratingRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.of(testRating));

        // When
        Optional<Rating> result = ratingService.getUserRatingForAudioFile(testUser, testAudioFile);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testRating);
        verify(ratingRepository).findByUserAndAudioFile(testUser, testAudioFile);
    }

    @Test
    void testGetUserRatingForAudioFile_NotFound() {
        // Given
        when(ratingRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.empty());

        // When
        Optional<Rating> result = ratingService.getUserRatingForAudioFile(testUser, testAudioFile);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository).findByUserAndAudioFile(testUser, testAudioFile);
    }

    @Test
    void testGetAverageRating() {
        // Given
        when(ratingRepository.getAverageRatingForAudioFile(testAudioFile.getId())).thenReturn(4.5);

        // When
        Double result = ratingService.getAverageRating(testAudioFile);

        // Then
        assertThat(result).isEqualTo(4.5);
        verify(ratingRepository).getAverageRatingForAudioFile(testAudioFile.getId());
    }

    @Test
    void testGetAverageRating_NoRatings() {
        // Given
        when(ratingRepository.getAverageRatingForAudioFile(testAudioFile.getId())).thenReturn(null);

        // When
        Double result = ratingService.getAverageRating(testAudioFile);

        // Then
        assertThat(result).isNull();
        verify(ratingRepository).getAverageRatingForAudioFile(testAudioFile.getId());
    }

    @Test
    void testGetRatingCount() {
        // Given
        when(ratingRepository.getRatingCountForAudioFile(testAudioFile.getId())).thenReturn(5L);

        // When
        Long result = ratingService.getRatingCount(testAudioFile);

        // Then
        assertThat(result).isEqualTo(5L);
        verify(ratingRepository).getRatingCountForAudioFile(testAudioFile.getId());
    }

    @Test
    void testDeleteRating() {
        // When
        ratingService.deleteRating(testRating);

        // Then
        verify(ratingRepository).delete(testRating);
    }

    @Test
    void testGetRatingStatistics_WithRatings() {
        // Given
        when(ratingRepository.getAverageRatingForAudioFile(testAudioFile.getId())).thenReturn(4.5);
        when(ratingRepository.getRatingCountForAudioFile(testAudioFile.getId())).thenReturn(10L);

        // When
        RatingService.RatingStatistics result = ratingService.getRatingStatistics(testAudioFile);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(4.5);
        assertThat(result.getRatingCount()).isEqualTo(10L);
        verify(ratingRepository).getAverageRatingForAudioFile(testAudioFile.getId());
        verify(ratingRepository).getRatingCountForAudioFile(testAudioFile.getId());
    }

    @Test
    void testGetRatingStatistics_NoRatings() {
        // Given
        when(ratingRepository.getAverageRatingForAudioFile(testAudioFile.getId())).thenReturn(null);
        when(ratingRepository.getRatingCountForAudioFile(testAudioFile.getId())).thenReturn(0L);

        // When
        RatingService.RatingStatistics result = ratingService.getRatingStatistics(testAudioFile);

        // Then
        assertThat(result.getAverageRating()).isEqualTo(0.0);
        assertThat(result.getRatingCount()).isEqualTo(0L);
        verify(ratingRepository).getAverageRatingForAudioFile(testAudioFile.getId());
        verify(ratingRepository).getRatingCountForAudioFile(testAudioFile.getId());
    }

    @Test
    void testGetAllRatings() {
        // Given
        List<Rating> expectedRatings = Arrays.asList(testRating, anotherRating);
        when(ratingRepository.findAll()).thenReturn(expectedRatings);

        // When
        List<Rating> result = ratingService.getAllRatings();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testRating, anotherRating);
        verify(ratingRepository).findAll();
    }

    @Test
    void testGetRatingsByUser_EmptyResult() {
        // Given
        when(ratingRepository.findByUser(testUser)).thenReturn(Arrays.asList());

        // When
        List<Rating> result = ratingService.getRatingsByUser(testUser);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository).findByUser(testUser);
    }

    @Test
    void testGetRatingsForAudioFile_EmptyResult() {
        // Given
        when(ratingRepository.findByAudioFile(testAudioFile)).thenReturn(Arrays.asList());

        // When
        List<Rating> result = ratingService.getRatingsForAudioFile(testAudioFile);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository).findByAudioFile(testAudioFile);
    }

    @Test
    void testRatingStatisticsClass() {
        // Test the inner RatingStatistics class
        RatingService.RatingStatistics stats = new RatingService.RatingStatistics(4.2, 15L);
        
        assertThat(stats.getAverageRating()).isEqualTo(4.2);
        assertThat(stats.getRatingCount()).isEqualTo(15L);
    }
}