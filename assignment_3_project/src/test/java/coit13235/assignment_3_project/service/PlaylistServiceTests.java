package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.Playlist;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.PlaylistRepository;
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
 * Unit tests for PlaylistService.
 */
@ExtendWith(MockitoExtension.class)
class PlaylistServiceTests {

    @Mock
    private PlaylistRepository playlistRepository;

    @InjectMocks
    private PlaylistService playlistService;

    private User testUser;
    private Playlist testPlaylist;
    private AudioFile testAudioFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testPlaylist = new Playlist("Test Playlist", testUser);
        testPlaylist.setId(1L);
        testPlaylist.setDescription("Test Description");
        testPlaylist.setPublic(true);
        testPlaylist.setCreatedAt(LocalDateTime.now());

        testAudioFile = new AudioFile();
        testAudioFile.setId(1L);
        testAudioFile.setFilename("test.mp3");
        testAudioFile.setTrackName("Test Track");
        testAudioFile.setArtistName("Test Artist");
    }

    @Test
    void testCreatePlaylist() {
        // Given
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        // When
        Playlist result = playlistService.createPlaylist("Test Playlist", "Test Description", true, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Playlist");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.isPublic()).isTrue();
        assertThat(result.getOwner()).isEqualTo(testUser);
        verify(playlistRepository).save(any(Playlist.class));
    }

    @Test
    void testGetPlaylistsByOwner() {
        // Given
        List<Playlist> expectedPlaylists = Arrays.asList(testPlaylist);
        when(playlistRepository.findByOwner(testUser)).thenReturn(expectedPlaylists);

        // When
        List<Playlist> result = playlistService.getPlaylistsByOwner(testUser);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPlaylist);
        verify(playlistRepository).findByOwner(testUser);
    }

    @Test
    void testGetPublicPlaylists() {
        // Given
        List<Playlist> expectedPlaylists = Arrays.asList(testPlaylist);
        when(playlistRepository.findByIsPublicTrue()).thenReturn(expectedPlaylists);

        // When
        List<Playlist> result = playlistService.getPublicPlaylists();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPlaylist);
        verify(playlistRepository).findByIsPublicTrue();
    }

    @Test
    void testGetPlaylistById_WhenPlaylistExists() {
        // Given
        when(playlistRepository.findById(1L)).thenReturn(Optional.of(testPlaylist));

        // When
        Optional<Playlist> result = playlistService.getPlaylistById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testPlaylist);
        verify(playlistRepository).findById(1L);
    }

    @Test
    void testGetPlaylistById_WhenPlaylistDoesNotExist() {
        // Given
        when(playlistRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Playlist> result = playlistService.getPlaylistById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(playlistRepository).findById(999L);
    }

    @Test
    void testAddAudioFileToPlaylist() {
        // Given
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        // When
        Playlist result = playlistService.addAudioFileToPlaylist(testPlaylist, testAudioFile);

        // Then
        assertThat(result).isNotNull();
        assertThat(testPlaylist.getAudioFiles()).contains(testAudioFile);
        verify(playlistRepository).save(testPlaylist);
    }

    @Test
    void testAddAudioFileToPlaylist_WhenFileAlreadyExists() {
        // Given
        testPlaylist.getAudioFiles().add(testAudioFile);

        // When
        Playlist result = playlistService.addAudioFileToPlaylist(testPlaylist, testAudioFile);

        // Then
        assertThat(result).isNotNull();
        // Should not add duplicate
        verify(playlistRepository, never()).save(any(Playlist.class));
    }

    @Test
    void testRemoveAudioFileFromPlaylist() {
        // Given
        testPlaylist.getAudioFiles().add(testAudioFile);
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        // When
        Playlist result = playlistService.removeAudioFileFromPlaylist(testPlaylist, testAudioFile);

        // Then
        assertThat(result).isNotNull();
        assertThat(testPlaylist.getAudioFiles()).doesNotContain(testAudioFile);
        verify(playlistRepository).save(testPlaylist);
    }

    @Test
    void testRemoveAudioFileFromPlaylist_WhenFileNotInPlaylist() {
        // Given
        // testPlaylist.getAudioFiles() is empty by default

        // When
        Playlist result = playlistService.removeAudioFileFromPlaylist(testPlaylist, testAudioFile);

        // Then
        assertThat(result).isNotNull();
        // Should not save if no change
        verify(playlistRepository, never()).save(any(Playlist.class));
    }

    @Test
    void testUpdatePlaylist() {
        // Given
        when(playlistRepository.save(any(Playlist.class))).thenReturn(testPlaylist);

        // When
        Playlist result = playlistService.updatePlaylist(testPlaylist, "Updated Name", "Updated Description", false);

        // Then
        assertThat(result).isNotNull();
        assertThat(testPlaylist.getName()).isEqualTo("Updated Name");
        assertThat(testPlaylist.getDescription()).isEqualTo("Updated Description");
        assertThat(testPlaylist.isPublic()).isFalse();
        assertThat(testPlaylist.getUpdatedAt()).isNotNull();
        verify(playlistRepository).save(testPlaylist);
    }

    @Test
    void testDeletePlaylist() {
        // When
        playlistService.deletePlaylist(testPlaylist);

        // Then
        verify(playlistRepository).delete(testPlaylist);
    }

    @Test
    void testSearchPlaylistsByName() {
        // Given
        List<Playlist> expectedPlaylists = Arrays.asList(testPlaylist);
        when(playlistRepository.findByNameContainingIgnoreCase("Test")).thenReturn(expectedPlaylists);

        // When
        List<Playlist> result = playlistService.searchPlaylistsByName("Test");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPlaylist);
        verify(playlistRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void testSearchPublicPlaylistsByName() {
        // Given
        List<Playlist> expectedPlaylists = Arrays.asList(testPlaylist);
        when(playlistRepository.findPublicPlaylistsByNameContaining("Test")).thenReturn(expectedPlaylists);

        // When
        List<Playlist> result = playlistService.searchPublicPlaylistsByName("Test");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPlaylist);
        verify(playlistRepository).findPublicPlaylistsByNameContaining("Test");
    }

    @Test
    void testCanUserAccessPlaylist_WhenUserIsOwner() {
        // Given
        testPlaylist.setPublic(false);

        // When
        boolean result = playlistService.canUserAccessPlaylist(testPlaylist, testUser);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testCanUserAccessPlaylist_WhenPlaylistIsPublic() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        testPlaylist.setPublic(true);

        // When
        boolean result = playlistService.canUserAccessPlaylist(testPlaylist, anotherUser);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testCanUserAccessPlaylist_WhenUserIsNotOwnerAndPlaylistIsPrivate() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        testPlaylist.setPublic(false);

        // When
        boolean result = playlistService.canUserAccessPlaylist(testPlaylist, anotherUser);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testCanUserModifyPlaylist_WhenUserIsOwner() {
        // When
        boolean result = playlistService.canUserModifyPlaylist(testPlaylist, testUser);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testCanUserModifyPlaylist_WhenUserIsNotOwner() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");

        // When
        boolean result = playlistService.canUserModifyPlaylist(testPlaylist, anotherUser);

        // Then
        assertThat(result).isFalse();
    }
}