package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.Playlist;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.repository.PlaylistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing playlists and playlist operations.
 */
@Service
public class PlaylistService {
    
    private final PlaylistRepository playlistRepository;
    
    /**
     * Constructor for dependency injection.
     * @param playlistRepository the playlist repository
     */
    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }
    
    /**
     * Creates a new playlist for a user.
     * @param name the playlist name
     * @param description the playlist description
     * @param isPublic whether the playlist is public
     * @param owner the playlist owner
     * @return the created playlist
     */
    @Transactional
    public Playlist createPlaylist(String name, String description, boolean isPublic, User owner) {
        Playlist playlist = new Playlist(name, owner);
        playlist.setDescription(description);
        playlist.setPublic(isPublic);
        return playlistRepository.save(playlist);
    }
    
    /**
     * Gets all playlists owned by a user.
     * @param owner the playlist owner
     * @return list of playlists owned by the user
     */
    public List<Playlist> getPlaylistsByOwner(User owner) {
        return playlistRepository.findByOwner(owner);
    }
    
    /**
     * Gets all public playlists.
     * @return list of public playlists
     */
    public List<Playlist> getPublicPlaylists() {
        return playlistRepository.findByIsPublicTrue();
    }
    
    /**
     * Searches for playlists by name.
     * @param name the search term
     * @return list of matching playlists
     */
    public List<Playlist> searchPlaylistsByName(String name) {
        return playlistRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     * Searches for public playlists by name.
     * @param name the search term
     * @return list of matching public playlists
     */
    public List<Playlist> searchPublicPlaylistsByName(String name) {
        return playlistRepository.findPublicPlaylistsByNameContaining(name);
    }
    
    /**
     * Gets a playlist by ID.
     * @param id the playlist ID
     * @return the playlist if found
     */
    public Optional<Playlist> getPlaylistById(Long id) {
        return playlistRepository.findById(id);
    }
    
    /**
     * Updates a playlist's basic information.
     * @param playlist the playlist to update
     * @param name the new name
     * @param description the new description
     * @param isPublic the new public status
     * @return the updated playlist
     */
    @Transactional
    public Playlist updatePlaylist(Playlist playlist, String name, String description, boolean isPublic) {
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPublic(isPublic);
        playlist.setUpdatedAt(LocalDateTime.now());
        return playlistRepository.save(playlist);
    }
    
    /**
     * Adds an audio file to a playlist.
     * @param playlist the playlist
     * @param audioFile the audio file to add
     * @return the updated playlist
     */
    @Transactional
    public Playlist addAudioFileToPlaylist(Playlist playlist, AudioFile audioFile) {
        // Initialize audioFiles list if it's null
        if (playlist.getAudioFiles() == null) {
            playlist.setAudioFiles(new ArrayList<>());
        }
        
        if (!playlist.getAudioFiles().contains(audioFile)) {
            playlist.getAudioFiles().add(audioFile);
            playlist.setUpdatedAt(LocalDateTime.now());
            return playlistRepository.save(playlist);
        }
        return playlist;
    }
    
    /**
     * Removes an audio file from a playlist.
     * @param playlist the playlist
     * @param audioFile the audio file to remove
     * @return the updated playlist
     */
    @Transactional
    public Playlist removeAudioFileFromPlaylist(Playlist playlist, AudioFile audioFile) {
        // Initialize audioFiles list if it's null (though this shouldn't happen for removal)
        if (playlist.getAudioFiles() == null) {
            playlist.setAudioFiles(new ArrayList<>());
        }
        
        if (playlist.getAudioFiles().remove(audioFile)) {
            playlist.setUpdatedAt(LocalDateTime.now());
            return playlistRepository.save(playlist);
        }
        return playlist;
    }
    
    /**
     * Deletes a playlist.
     * @param playlist the playlist to delete
     */
    @Transactional
    public void deletePlaylist(Playlist playlist) {
        playlistRepository.delete(playlist);
    }
    
    /**
     * Checks if a user can access a playlist (owner or public).
     * @param playlist the playlist
     * @param user the user
     * @return true if the user can access the playlist
     */
    public boolean canUserAccessPlaylist(Playlist playlist, User user) {
        return playlist.isPublic() || playlist.getOwner().equals(user);
    }
    
    /**
     * Checks if a user can modify a playlist (only owner).
     * @param playlist the playlist
     * @param user the user
     * @return true if the user can modify the playlist
     */
    public boolean canUserModifyPlaylist(Playlist playlist, User user) {
        return playlist.getOwner().equals(user);
    }
}