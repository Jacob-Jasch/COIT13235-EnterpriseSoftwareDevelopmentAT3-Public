package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.Favorite;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.FavoriteRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FavoriteService.
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTests {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private User testUser;
    private AudioFile testAudioFile;
    private Favorite testTrackFavorite;
    private Favorite testArtistFavorite;
    private String testArtistName;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testAudioFile = new AudioFile();
        testAudioFile.setId(1L);
        testAudioFile.setFilename("test.mp3");
        testAudioFile.setTrackName("Test Track");
        testAudioFile.setArtistName("Test Artist");

        testArtistName = "Test Artist";

        testTrackFavorite = new Favorite(testUser, testAudioFile);
        testTrackFavorite.setId(1L);
        testTrackFavorite.setCreatedAt(LocalDateTime.now());

        testArtistFavorite = new Favorite(testUser, testArtistName);
        testArtistFavorite.setId(2L);
        testArtistFavorite.setCreatedAt(LocalDateTime.now());
    }

    // Track favorite tests

    @Test
    void testAddTrackToFavorites_NewFavorite() {
        // Given
        when(favoriteRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testTrackFavorite);

        // When
        Favorite result = favoriteService.addTrackToFavorites(testUser, testAudioFile);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getAudioFile()).isEqualTo(testAudioFile);
        assertThat(result.getType()).isEqualTo(Favorite.FavoriteType.TRACK);
        verify(favoriteRepository).findByUserAndAudioFile(testUser, testAudioFile);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void testAddTrackToFavorites_ExistingFavorite() {
        // Given
        when(favoriteRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.of(testTrackFavorite));

        // When
        Favorite result = favoriteService.addTrackToFavorites(testUser, testAudioFile);

        // Then
        assertThat(result).isEqualTo(testTrackFavorite);
        verify(favoriteRepository).findByUserAndAudioFile(testUser, testAudioFile);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void testRemoveTrackFromFavorites_ExistingFavorite() {
        // Given
        when(favoriteRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.of(testTrackFavorite));

        // When
        boolean result = favoriteService.removeTrackFromFavorites(testUser, testAudioFile);

        // Then
        assertThat(result).isTrue();
        verify(favoriteRepository).findByUserAndAudioFile(testUser, testAudioFile);
        verify(favoriteRepository).delete(testTrackFavorite);
    }

    @Test
    void testRemoveTrackFromFavorites_NonExistingFavorite() {
        // Given
        when(favoriteRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.empty());

        // When
        boolean result = favoriteService.removeTrackFromFavorites(testUser, testAudioFile);

        // Then
        assertThat(result).isFalse();
        verify(favoriteRepository).findByUserAndAudioFile(testUser, testAudioFile);
        verify(favoriteRepository, never()).delete(any(Favorite.class));
    }

    @Test
    void testIsTrackFavorited_True() {
        // Given
        when(favoriteRepository.existsByUserAndAudioFile(testUser, testAudioFile)).thenReturn(true);

        // When
        boolean result = favoriteService.isTrackFavorited(testUser, testAudioFile);

        // Then
        assertThat(result).isTrue();
        verify(favoriteRepository).existsByUserAndAudioFile(testUser, testAudioFile);
    }

    @Test
    void testIsTrackFavorited_False() {
        // Given
        when(favoriteRepository.existsByUserAndAudioFile(testUser, testAudioFile)).thenReturn(false);

        // When
        boolean result = favoriteService.isTrackFavorited(testUser, testAudioFile);

        // Then
        assertThat(result).isFalse();
        verify(favoriteRepository).existsByUserAndAudioFile(testUser, testAudioFile);
    }

    @Test
    void testToggleTrackFavorite_AddToFavorites() {
        // Given
        when(favoriteRepository.existsByUserAndAudioFile(testUser, testAudioFile)).thenReturn(false);
        when(favoriteRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testTrackFavorite);

        // When
        boolean result = favoriteService.toggleTrackFavorite(testUser, testAudioFile);

        // Then
        assertThat(result).isTrue();
        verify(favoriteRepository).existsByUserAndAudioFile(testUser, testAudioFile);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void testToggleTrackFavorite_RemoveFromFavorites() {
        // Given
        when(favoriteRepository.existsByUserAndAudioFile(testUser, testAudioFile)).thenReturn(true);
        when(favoriteRepository.findByUserAndAudioFile(testUser, testAudioFile)).thenReturn(Optional.of(testTrackFavorite));

        // When
        boolean result = favoriteService.toggleTrackFavorite(testUser, testAudioFile);

        // Then
        assertThat(result).isFalse();
        verify(favoriteRepository).existsByUserAndAudioFile(testUser, testAudioFile);
        verify(favoriteRepository).delete(testTrackFavorite);
    }

    // Artist favorite tests

    @Test
    void testAddArtistToFavorites_NewFavorite() {
        // Given
        when(favoriteRepository.findByUserAndArtistName(testUser, testArtistName)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testArtistFavorite);

        // When
        Favorite result = favoriteService.addArtistToFavorites(testUser, testArtistName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getArtistName()).isEqualTo(testArtistName);
        assertThat(result.getType()).isEqualTo(Favorite.FavoriteType.ARTIST);
        verify(favoriteRepository).findByUserAndArtistName(testUser, testArtistName);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void testAddArtistToFavorites_ExistingFavorite() {
        // Given
        when(favoriteRepository.findByUserAndArtistName(testUser, testArtistName)).thenReturn(Optional.of(testArtistFavorite));

        // When
        Favorite result = favoriteService.addArtistToFavorites(testUser, testArtistName);

        // Then
        assertThat(result).isEqualTo(testArtistFavorite);
        verify(favoriteRepository).findByUserAndArtistName(testUser, testArtistName);
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void testRemoveArtistFromFavorites_ExistingFavorite() {
        // Given
        when(favoriteRepository.findByUserAndArtistName(testUser, testArtistName)).thenReturn(Optional.of(testArtistFavorite));

        // When
        boolean result = favoriteService.removeArtistFromFavorites(testUser, testArtistName);

        // Then
        assertThat(result).isTrue();
        verify(favoriteRepository).findByUserAndArtistName(testUser, testArtistName);
        verify(favoriteRepository).delete(testArtistFavorite);
    }

    @Test
    void testRemoveArtistFromFavorites_NonExistingFavorite() {
        // Given
        when(favoriteRepository.findByUserAndArtistName(testUser, testArtistName)).thenReturn(Optional.empty());

        // When
        boolean result = favoriteService.removeArtistFromFavorites(testUser, testArtistName);

        // Then
        assertThat(result).isFalse();
        verify(favoriteRepository).findByUserAndArtistName(testUser, testArtistName);
        verify(favoriteRepository, never()).delete(any(Favorite.class));
    }

    @Test
    void testIsArtistFavorited_True() {
        // Given
        when(favoriteRepository.existsByUserAndArtistName(testUser, testArtistName)).thenReturn(true);

        // When
        boolean result = favoriteService.isArtistFavorited(testUser, testArtistName);

        // Then
        assertThat(result).isTrue();
        verify(favoriteRepository).existsByUserAndArtistName(testUser, testArtistName);
    }

    @Test
    void testIsArtistFavorited_False() {
        // Given
        when(favoriteRepository.existsByUserAndArtistName(testUser, testArtistName)).thenReturn(false);

        // When
        boolean result = favoriteService.isArtistFavorited(testUser, testArtistName);

        // Then
        assertThat(result).isFalse();
        verify(favoriteRepository).existsByUserAndArtistName(testUser, testArtistName);
    }

    @Test
    void testToggleArtistFavorite_AddToFavorites() {
        // Given
        when(favoriteRepository.existsByUserAndArtistName(testUser, testArtistName)).thenReturn(false);
        when(favoriteRepository.findByUserAndArtistName(testUser, testArtistName)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testArtistFavorite);

        // When
        boolean result = favoriteService.toggleArtistFavorite(testUser, testArtistName);

        // Then
        assertThat(result).isTrue();
        verify(favoriteRepository).existsByUserAndArtistName(testUser, testArtistName);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void testToggleArtistFavorite_RemoveFromFavorites() {
        // Given
        when(favoriteRepository.existsByUserAndArtistName(testUser, testArtistName)).thenReturn(true);
        when(favoriteRepository.findByUserAndArtistName(testUser, testArtistName)).thenReturn(Optional.of(testArtistFavorite));

        // When
        boolean result = favoriteService.toggleArtistFavorite(testUser, testArtistName);

        // Then
        assertThat(result).isFalse();
        verify(favoriteRepository).existsByUserAndArtistName(testUser, testArtistName);
        verify(favoriteRepository).delete(testArtistFavorite);
    }

    // Get favorites tests

    @Test
    void testGetAllFavoritesByUser() {
        // Given
        List<Favorite> expectedFavorites = Arrays.asList(testTrackFavorite, testArtistFavorite);
        when(favoriteRepository.findByUser(testUser)).thenReturn(expectedFavorites);

        // When
        List<Favorite> result = favoriteService.getAllFavoritesByUser(testUser);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testTrackFavorite, testArtistFavorite);
        verify(favoriteRepository).findByUser(testUser);
    }

    @Test
    void testGetTrackFavoritesByUser() {
        // Given
        List<Favorite> expectedFavorites = Arrays.asList(testTrackFavorite);
        when(favoriteRepository.findTrackFavoritesByUser(testUser)).thenReturn(expectedFavorites);

        // When
        List<Favorite> result = favoriteService.getTrackFavoritesByUser(testUser);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testTrackFavorite);
        assertThat(result.get(0).getType()).isEqualTo(Favorite.FavoriteType.TRACK);
        verify(favoriteRepository).findTrackFavoritesByUser(testUser);
    }

    @Test
    void testGetArtistFavoritesByUser() {
        // Given
        List<Favorite> expectedFavorites = Arrays.asList(testArtistFavorite);
        when(favoriteRepository.findArtistFavoritesByUser(testUser)).thenReturn(expectedFavorites);

        // When
        List<Favorite> result = favoriteService.getArtistFavoritesByUser(testUser);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testArtistFavorite);
        assertThat(result.get(0).getType()).isEqualTo(Favorite.FavoriteType.ARTIST);
        verify(favoriteRepository).findArtistFavoritesByUser(testUser);
    }

    @Test
    void testGetAllFavoritesByUser_EmptyResult() {
        // Given
        when(favoriteRepository.findByUser(testUser)).thenReturn(Arrays.asList());

        // When
        List<Favorite> result = favoriteService.getAllFavoritesByUser(testUser);

        // Then
        assertThat(result).isEmpty();
        verify(favoriteRepository).findByUser(testUser);
    }
}