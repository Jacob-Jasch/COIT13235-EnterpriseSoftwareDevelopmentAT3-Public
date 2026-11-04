package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.AudioFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AudioFileService.
 */
@ExtendWith(MockitoExtension.class)
class AudioFileServiceTests {

    @Mock
    private AudioFileRepository audioFileRepository;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private AudioFileService audioFileService;

    private User testUser;
    private AudioFile uploadedAudioFile;
    private AudioFile itunesAudioFile;
    private AudioFile duplicateAudioFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Uploaded file
        uploadedAudioFile = new AudioFile();
        uploadedAudioFile.setId(1L);
        uploadedAudioFile.setFilename("uuid_test.mp3");
        uploadedAudioFile.setOriginalFilename("test.mp3");
        uploadedAudioFile.setTrackName("Test Track");
        uploadedAudioFile.setArtistName("Test Artist");
        uploadedAudioFile.setContentType("audio/mpeg");
        uploadedAudioFile.setSize(1024L);
        uploadedAudioFile.setUser(testUser);
        uploadedAudioFile.setFileData(new byte[]{1, 2, 3, 4, 5});
        uploadedAudioFile.setUploadedAt(LocalDateTime.now());

        // iTunes file
        itunesAudioFile = new AudioFile();
        itunesAudioFile.setId(2L);
        itunesAudioFile.setTrackName("iTunes Track");
        itunesAudioFile.setArtistName("iTunes Artist");
        itunesAudioFile.setPreviewUrl("https://audio-ssl.itunes.apple.com/preview.m4a");
        itunesAudioFile.setItunesTrackId("123456789");
        itunesAudioFile.setUser(testUser);
        itunesAudioFile.setContentType("audio/mp4");
        itunesAudioFile.setSize(0L);

        // Duplicate uploaded file record
        duplicateAudioFile = new AudioFile();
        duplicateAudioFile.setId(3L);
        duplicateAudioFile.setTrackName("Duplicate Track");
        duplicateAudioFile.setArtistName("Duplicate Artist");
        duplicateAudioFile.setItunesTrackId("uploaded-uuid-123");
        duplicateAudioFile.setUser(testUser);
        duplicateAudioFile.setFileData(null); // No file data indicates duplicate
    }

    @Test
    void testSaveAudioFile() throws IOException {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn("test.mp3");
        when(mockFile.getContentType()).thenReturn("audio/mpeg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3, 4, 5});
        when(audioFileRepository.save(any(AudioFile.class))).thenReturn(uploadedAudioFile);

        // When
        AudioFile result = audioFileService.saveAudioFile(mockFile, testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOriginalFilename()).isEqualTo("test.mp3");
        assertThat(result.getContentType()).isEqualTo("audio/mpeg");
        assertThat(result.getSize()).isEqualTo(1024L);
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getFileData()).isNotNull();
        verify(audioFileRepository).save(any(AudioFile.class));
        verify(mockFile, atLeastOnce()).getOriginalFilename();
        verify(mockFile).getContentType();
        verify(mockFile).getSize();
        verify(mockFile).getBytes();
    }

    @Test
    void testSaveAudioFile_ThrowsIOException() throws IOException {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn("test.mp3");
        when(mockFile.getContentType()).thenReturn("audio/mpeg");
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getBytes()).thenThrow(new IOException("File read error"));

        // When & Then
        assertThrows(IOException.class, () -> {
            audioFileService.saveAudioFile(mockFile, testUser);
        });
        verify(audioFileRepository, never()).save(any(AudioFile.class));
    }

    @Test
    void testGetAudioFilesByUser() {
        // Given
        List<AudioFile> expectedFiles = Arrays.asList(uploadedAudioFile, itunesAudioFile);
        when(audioFileRepository.findByUserId(1L)).thenReturn(expectedFiles);

        // When
        List<AudioFile> result = audioFileService.getAudioFilesByUser(1L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(uploadedAudioFile, itunesAudioFile);
        verify(audioFileRepository).findByUserId(1L);
    }

    @Test
    void testGetUploadedFilesByUser() {
        // Given
        List<AudioFile> expectedFiles = Arrays.asList(uploadedAudioFile);
        when(audioFileRepository.findByUserIdAndFileDataIsNotNull(1L)).thenReturn(expectedFiles);

        // When
        List<AudioFile> result = audioFileService.getUploadedFilesByUser(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(uploadedAudioFile);
        verify(audioFileRepository).findByUserIdAndFileDataIsNotNull(1L);
    }

    @Test
    void testGetAudioFile_Found() {
        // Given
        when(audioFileRepository.findById(1L)).thenReturn(Optional.of(uploadedAudioFile));

        // When
        AudioFile result = audioFileService.getAudioFile(1L);

        // Then
        assertThat(result).isEqualTo(uploadedAudioFile);
        verify(audioFileRepository).findById(1L);
    }

    @Test
    void testGetAudioFile_NotFound() {
        // Given
        when(audioFileRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        AudioFile result = audioFileService.getAudioFile(999L);

        // Then
        assertThat(result).isNull();
        verify(audioFileRepository).findById(999L);
    }

    @Test
    void testFindByTrackNameAndArtistName_Found() {
        // Given
        List<AudioFile> foundFiles = Arrays.asList(uploadedAudioFile);
        when(audioFileRepository.findByTrackNameAndArtistName("Test Track", "Test Artist"))
                .thenReturn(foundFiles);

        // When
        AudioFile result = audioFileService.findByTrackNameAndArtistName("Test Track", "Test Artist");

        // Then
        assertThat(result).isEqualTo(uploadedAudioFile);
        verify(audioFileRepository).findByTrackNameAndArtistName("Test Track", "Test Artist");
    }

    @Test
    void testFindByTrackNameAndArtistName_NotFound() {
        // Given
        when(audioFileRepository.findByTrackNameAndArtistName("Nonexistent", "Artist"))
                .thenReturn(Arrays.asList());

        // When
        AudioFile result = audioFileService.findByTrackNameAndArtistName("Nonexistent", "Artist");

        // Then
        assertThat(result).isNull();
        verify(audioFileRepository).findByTrackNameAndArtistName("Nonexistent", "Artist");
    }

    @Test
    void testFindByTrackNameAndArtistName_NullInput() {
        // Given
        when(audioFileRepository.findByTrackNameAndArtistName(null, null))
                .thenReturn(null);

        // When
        AudioFile result = audioFileService.findByTrackNameAndArtistName(null, null);

        // Then
        assertThat(result).isNull();
        verify(audioFileRepository).findByTrackNameAndArtistName(null, null);
    }

    @Test
    void testFindByTrackNameAndArtistName_MultipleFound() {
        // Given
        AudioFile secondFile = new AudioFile();
        secondFile.setId(4L);
        secondFile.setTrackName("Test Track");
        secondFile.setArtistName("Test Artist");
        
        List<AudioFile> foundFiles = Arrays.asList(uploadedAudioFile, secondFile);
        when(audioFileRepository.findByTrackNameAndArtistName("Test Track", "Test Artist"))
                .thenReturn(foundFiles);

        // When
        AudioFile result = audioFileService.findByTrackNameAndArtistName("Test Track", "Test Artist");

        // Then
        assertThat(result).isEqualTo(uploadedAudioFile); // Should return first one
        verify(audioFileRepository).findByTrackNameAndArtistName("Test Track", "Test Artist");
    }

    @Test
    void testSaveFromItunes() {
        // Given
        when(audioFileRepository.save(any(AudioFile.class))).thenReturn(itunesAudioFile);

        // When
        AudioFile result = audioFileService.saveFromItunes(
                "iTunes Track", "iTunes Artist", 
                "https://audio-ssl.itunes.apple.com/preview.m4a", 
                "123456789", testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTrackName()).isEqualTo("iTunes Track");
        assertThat(result.getArtistName()).isEqualTo("iTunes Artist");
        assertThat(result.getPreviewUrl()).isEqualTo("https://audio-ssl.itunes.apple.com/preview.m4a");
        assertThat(result.getItunesTrackId()).isEqualTo("123456789");
        assertThat(result.getUser()).isEqualTo(testUser);
        verify(audioFileRepository).save(any(AudioFile.class));
    }

    @Test
    void testFindByItunesIds() {
        // Given
        List<String> itunesIds = Arrays.asList("123456789", "987654321");
        List<AudioFile> expectedFiles = Arrays.asList(itunesAudioFile);
        when(audioFileRepository.findByItunesTrackIdIn(itunesIds)).thenReturn(expectedFiles);

        // When
        List<AudioFile> result = audioFileService.findByItunesIds(itunesIds);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(itunesAudioFile);
        verify(audioFileRepository).findByItunesTrackIdIn(itunesIds);
    }

    @Test
    void testFindByItunesTrackId() {
        // Given
        List<AudioFile> expectedFiles = Arrays.asList(itunesAudioFile);
        when(audioFileRepository.findByItunesTrackIdIn(Arrays.asList("123456789")))
                .thenReturn(expectedFiles);

        // When
        List<AudioFile> result = audioFileService.findByItunesTrackId("123456789");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(itunesAudioFile);
        verify(audioFileRepository).findByItunesTrackIdIn(Arrays.asList("123456789"));
    }

    @Test
    void testFindByItunesTrackId_NullInput() {
        // When
        List<AudioFile> result = audioFileService.findByItunesTrackId(null);

        // Then
        assertThat(result).isEmpty();
        verify(audioFileRepository, never()).findByItunesTrackIdIn(any());
    }

    @Test
    void testDeleteAudioFile_Found() {
        // Given
        when(audioFileRepository.findById(1L)).thenReturn(Optional.of(uploadedAudioFile));

        // When
        audioFileService.deleteAudioFile(1L);

        // Then
        verify(audioFileRepository).findById(1L);
        verify(audioFileRepository).deleteById(1L);
    }

    @Test
    void testDeleteAudioFile_NotFound() {
        // Given
        when(audioFileRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        audioFileService.deleteAudioFile(999L);

        // Then
        verify(audioFileRepository).findById(999L);
        verify(audioFileRepository, never()).deleteById(any());
    }

    @Test
    void testCleanupDuplicateUploadedFileRecords() {
        // Given
        List<AudioFile> duplicates = Arrays.asList(duplicateAudioFile);
        when(audioFileRepository.findByItunesTrackIdStartingWith("uploaded-")).thenReturn(duplicates);

        // When
        int result = audioFileService.cleanupDuplicateUploadedFileRecords();

        // Then
        assertThat(result).isEqualTo(1);
        verify(audioFileRepository).findByItunesTrackIdStartingWith("uploaded-");
        verify(audioFileRepository).delete(duplicateAudioFile);
    }

    @Test
    void testCleanupDuplicateUploadedFileRecords_WithFileData() {
        // Given
        duplicateAudioFile.setFileData(new byte[]{1, 2, 3}); // Has file data, shouldn't be deleted
        List<AudioFile> duplicates = Arrays.asList(duplicateAudioFile);
        when(audioFileRepository.findByItunesTrackIdStartingWith("uploaded-")).thenReturn(duplicates);

        // When
        int result = audioFileService.cleanupDuplicateUploadedFileRecords();

        // Then
        assertThat(result).isEqualTo(0);
        verify(audioFileRepository).findByItunesTrackIdStartingWith("uploaded-");
        verify(audioFileRepository, never()).delete(any(AudioFile.class));
    }

    @Test
    void testCleanupDuplicateUploadedFileRecords_NoDuplicates() {
        // Given
        when(audioFileRepository.findByItunesTrackIdStartingWith("uploaded-")).thenReturn(Arrays.asList());

        // When
        int result = audioFileService.cleanupDuplicateUploadedFileRecords();

        // Then
        assertThat(result).isEqualTo(0);
        verify(audioFileRepository).findByItunesTrackIdStartingWith("uploaded-");
        verify(audioFileRepository, never()).delete(any(AudioFile.class));
    }

    @Test
    void testGetAudioFilesByUser_EmptyResult() {
        // Given
        when(audioFileRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        // When
        List<AudioFile> result = audioFileService.getAudioFilesByUser(1L);

        // Then
        assertThat(result).isEmpty();
        verify(audioFileRepository).findByUserId(1L);
    }

    @Test
    void testGetUploadedFilesByUser_EmptyResult() {
        // Given
        when(audioFileRepository.findByUserIdAndFileDataIsNotNull(1L)).thenReturn(Arrays.asList());

        // When
        List<AudioFile> result = audioFileService.getUploadedFilesByUser(1L);

        // Then
        assertThat(result).isEmpty();
        verify(audioFileRepository).findByUserIdAndFileDataIsNotNull(1L);
    }
}