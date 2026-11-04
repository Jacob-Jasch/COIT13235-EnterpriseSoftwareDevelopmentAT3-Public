package coit13235.assignment_3_project.service;

import coit13235.assignment_3_project.entity.Favorite;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user favorites (tracks and artists).
 */
@Service
public class FavoriteService {
    
    private final FavoriteRepository favoriteRepository;
    
    /**
     * Constructor for dependency injection.
     * @param favoriteRepository the favorite repository
     */
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }
    
    /**
     * Adds an audio file to user's favorites.
     * @param user the user
     * @param audioFile the audio file to favorite
     * @return the favorite entity, or existing one if already favorited
     */
    @Transactional
    public Favorite addTrackToFavorites(User user, AudioFile audioFile) {
        Optional<Favorite> existing = favoriteRepository.findByUserAndAudioFile(user, audioFile);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Favorite favorite = new Favorite(user, audioFile);
        return favoriteRepository.save(favorite);
    }
    
    /**
     * Removes an audio file from user's favorites.
     * @param user the user
     * @param audioFile the audio file to unfavorite
     * @return true if removed, false if not found
     */
    @Transactional
    public boolean removeTrackFromFavorites(User user, AudioFile audioFile) {
        Optional<Favorite> existing = favoriteRepository.findByUserAndAudioFile(user, audioFile);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return true;
        }
        return false;
    }
    
    /**
     * Adds an artist to user's favorites.
     * @param user the user
     * @param artistName the artist name to favorite
     * @return the favorite entity, or existing one if already favorited
     */
    @Transactional
    public Favorite addArtistToFavorites(User user, String artistName) {
        Optional<Favorite> existing = favoriteRepository.findByUserAndArtistName(user, artistName);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        Favorite favorite = new Favorite(user, artistName);
        return favoriteRepository.save(favorite);
    }
    
    /**
     * Removes an artist from user's favorites.
     * @param user the user
     * @param artistName the artist name to unfavorite
     * @return true if removed, false if not found
     */
    @Transactional
    public boolean removeArtistFromFavorites(User user, String artistName) {
        Optional<Favorite> existing = favoriteRepository.findByUserAndArtistName(user, artistName);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return true;
        }
        return false;
    }
    
    /**
     * Gets all favorites for a user.
     * @param user the user
     * @return list of all favorites
     */
    public List<Favorite> getAllFavoritesByUser(User user) {
        return favoriteRepository.findByUser(user);
    }
    
    /**
     * Gets all track favorites for a user.
     * @param user the user
     * @return list of track favorites
     */
    public List<Favorite> getTrackFavoritesByUser(User user) {
        return favoriteRepository.findTrackFavoritesByUser(user);
    }
    
    /**
     * Gets all artist favorites for a user.
     * @param user the user
     * @return list of artist favorites
     */
    public List<Favorite> getArtistFavoritesByUser(User user) {
        return favoriteRepository.findArtistFavoritesByUser(user);
    }
    
    /**
     * Checks if a user has favorited a specific track.
     * @param user the user
     * @param audioFile the audio file
     * @return true if favorited, false otherwise
     */
    public boolean isTrackFavorited(User user, AudioFile audioFile) {
        return favoriteRepository.existsByUserAndAudioFile(user, audioFile);
    }
    
    /**
     * Checks if a user has favorited a specific artist.
     * @param user the user
     * @param artistName the artist name
     * @return true if favorited, false otherwise
     */
    public boolean isArtistFavorited(User user, String artistName) {
        return favoriteRepository.existsByUserAndArtistName(user, artistName);
    }
    
    /**
     * Toggles the favorite status of a track.
     * @param user the user
     * @param audioFile the audio file
     * @return true if now favorited, false if unfavorited
     */
    @Transactional
    public boolean toggleTrackFavorite(User user, AudioFile audioFile) {
        if (isTrackFavorited(user, audioFile)) {
            removeTrackFromFavorites(user, audioFile);
            return false;
        } else {
            addTrackToFavorites(user, audioFile);
            return true;
        }
    }
    
    /**
     * Toggles the favorite status of an artist.
     * @param user the user
     * @param artistName the artist name
     * @return true if now favorited, false if unfavorited
     */
    @Transactional
    public boolean toggleArtistFavorite(User user, String artistName) {
        if (isArtistFavorited(user, artistName)) {
            removeArtistFromFavorites(user, artistName);
            return false;
        } else {
            addArtistToFavorites(user, artistName);
            return true;
        }
    }
}