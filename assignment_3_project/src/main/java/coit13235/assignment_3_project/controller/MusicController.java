package coit13235.assignment_3_project.controller;

import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.UserRepository;
import coit13235.assignment_3_project.service.MusicSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;

/**
 * Controller for searching iTunes music tracks via the iTunes API.
 */
@Controller
public class MusicController {

    private final MusicSearchService musicSearchService;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     */
    public MusicController(MusicSearchService musicSearchService, UserRepository userRepository) {
        this.musicSearchService = musicSearchService;
        this.userRepository = userRepository;
    }

    /**
     * Searches iTunes using the new service layer.
     * 
     * @param term the search term
     * @return search results
     */
    @GetMapping("/api/music/search")
    @ResponseBody
    public ResponseEntity<Object[]> searchMusic(@RequestParam String term) {
        try {
            Object[] results = musicSearchService.searchItunes(term);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            System.err.println("Music search failed: " + e.getMessage());
            return ResponseEntity.ok(new Object[0]);
        }
    }

    /**
     * Gets user preferences for a list of track IDs.
     * 
     * @param trackIds       the iTunes track IDs
     * @param authentication the authentication
     * @return user preferences map
     */
    @PostMapping("/api/music/user-preferences")
    @ResponseBody
    public ResponseEntity<Map<String, MusicSearchService.UserTrackPreferences>> getUserPreferences(
            @RequestBody List<String> trackIds,
            Authentication authentication) {

        User user = null;
        if (authentication != null) {
            user = userRepository.findByUsername(authentication.getName());
        }

        Map<String, MusicSearchService.UserTrackPreferences> preferences = musicSearchService
                .getUserPreferences(trackIds, user);

        return ResponseEntity.ok(preferences);
    }
}
