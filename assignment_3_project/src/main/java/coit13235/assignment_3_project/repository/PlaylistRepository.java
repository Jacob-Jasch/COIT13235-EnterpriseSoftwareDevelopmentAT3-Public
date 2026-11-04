package coit13235.assignment_3_project.repository;

import coit13235.assignment_3_project.entity.Playlist;
import coit13235.assignment_3_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for Playlist entities.
 */
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    
    /**
     * Finds all playlists owned by a specific user.
     * @param owner the playlist owner
     * @return list of playlists owned by the user
     */
    List<Playlist> findByOwner(User owner);
    
    /**
     * Finds all public playlists.
     * @return list of public playlists
     */
    List<Playlist> findByIsPublicTrue();
    
    /**
     * Finds playlists by name containing the search term (case insensitive).
     * @param name the search term
     * @return list of matching playlists
     */
    @Query("SELECT p FROM Playlist p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Playlist> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Finds public playlists by name containing the search term (case insensitive).
     * @param name the search term
     * @return list of matching public playlists
     */
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Playlist> findPublicPlaylistsByNameContaining(@Param("name") String name);
}