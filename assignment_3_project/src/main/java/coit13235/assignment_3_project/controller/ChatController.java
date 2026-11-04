package coit13235.assignment_3_project.controller;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.Playlist;
import coit13235.assignment_3_project.entity.Favorite;
import coit13235.assignment_3_project.entity.Rating;
import coit13235.assignment_3_project.repository.UserRepository;
import coit13235.assignment_3_project.repository.PlaylistRepository;
import coit13235.assignment_3_project.repository.FavoriteRepository;
import coit13235.assignment_3_project.service.FavoriteService;
import coit13235.assignment_3_project.service.PlaylistService;
import coit13235.assignment_3_project.repository.RatingRepository;
import coit13235.assignment_3_project.service.AudioFileService;
import coit13235.assignment_3_project.service.MusicSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import coit13235.assignment_3_project.entity.Message;
import coit13235.assignment_3_project.repository.MessageRepository;
import coit13235.assignment_3_project.service.RatingService;

// OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for chat endpoints and UI.
 * Handles chat messages, music search, favorites, playlists, and ratings.
 * Provides both API endpoints (JSON) and UI endpoints (Thymeleaf).
 */
@Controller
@RequestMapping
@Tag(name = "Music Chat API", description = "REST API for music chat application providing messaging, music search, and playlist management")
public class ChatController {
    
    private final MessageRepository repo;
    private final AudioFileService audioFileService;
    private final UserRepository userRepository;
    private final MusicSearchService musicSearchService;
    private final PlaylistRepository playlistRepository;
    private final PlaylistService playlistService;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteService favoriteService;
    private final RatingRepository ratingRepository;
    private final RatingService ratingService;

    /**
     * Constructor for dependency injection.
     * 
     * @param repo               the message repository
     * @param audioFileService   the audio file service
     * @param userRepository     the user repository
     * @param musicSearchService the music search service
     * @param playlistRepository the playlist repository
     * @param playlistService    the playlist service
     * @param favoriteRepository the favorite repository
     * @param ratingRepository   the rating repository
     * @param favoriteService    the favorite service
     * @param ratingService      the rating service
     */
    @Autowired
    public ChatController(MessageRepository repo, AudioFileService audioFileService,
            UserRepository userRepository, MusicSearchService musicSearchService,
            PlaylistRepository playlistRepository, PlaylistService playlistService,
            FavoriteRepository favoriteRepository,
            RatingRepository ratingRepository, FavoriteService favoriteService, RatingService ratingService) {
        this.repo = repo;
        this.audioFileService = audioFileService;
        this.userRepository = userRepository;
        this.musicSearchService = musicSearchService;
        this.playlistRepository = playlistRepository;
        this.playlistService = playlistService;
        this.favoriteRepository = favoriteRepository;
        this.ratingRepository = ratingRepository;
        this.favoriteService = favoriteService;
        this.ratingService = ratingService;
    }

    /**
     * Returns all chat messages as JSON.
     * 
     * @return list of messages
     */
    @GetMapping("/api/chat")
    @ResponseBody
    public List<Message> _listJson() {
        return repo.findAll();
    }

    /**
     * Gets the current authenticated user info.
     * 
     * @param authentication the authentication object
     * @return the current user info
     * @throws Unauthorized if not authenticated
     */
    @Operation(
        summary = "Get current user information",
        description = "Retrieves information about the currently authenticated user",
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/api/user/current")
    @ResponseBody
    public ResponseEntity<?> _getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {
            {
                put("username", authentication.getName());
                put("authorities", authentication.getAuthorities());
            }
        });
    }

    /**
     * Adds a new message via API. Accepts both JSON body and form parameters.
     * 
     * @param authentication the authentication object
     * @param request        the message request containing content (for JSON)
     * @param content        the content parameter (for form data)
     * @return the saved message
     * @throws Unauthorized if not authenticated
     * @throws BadRequest   if content is missing
     */
    @Operation(
        summary = "Send a chat message",
        description = "Creates a new chat message from the authenticated user. Accepts content in JSON body or form parameter.",
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message sent successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Message.class))),
        @ApiResponse(responseCode = "400", description = "Missing or empty message content"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PostMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<Message> _addJson(Authentication authentication,
            @Parameter(description = "Message content in JSON format") @RequestBody(required = false) java.util.Map<String, String> request,
            @Parameter(description = "Message content as form parameter") @RequestParam(required = false) String content) {
        String messageContent = null;

        if (request != null && request.get("content") != null) {
            messageContent = request.get("content");
        } else if (content != null) {
            messageContent = content;
        }

        if (messageContent == null || messageContent.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String sender = authentication.getName();
        Message saved = repo.save(new Message(sender, messageContent));
        return ResponseEntity.ok(saved);
    }

    /**
     * Renders the chat page with messages and music data.
     * 
     * @param model          the model to populate
     * @param authentication the authentication object
     * @return the chat view
     */
    @GetMapping({ "/", "/ui/chat" })
    public String _chatPage(Model model, Authentication authentication) {
        try {
            List<Message> messages = repo.findAll();
            List<Message> processedMessages = processMessagesForInteractiveContent(messages);
            model.addAttribute("messages", processedMessages != null ? processedMessages : List.of());
            model.addAttribute("playlists", List.of());
            model.addAttribute("favorites", List.of());
            model.addAttribute("ratings", List.of());
            model.addAttribute("audioFiles", List.of());
            model.addAttribute("favoriteTrackIds", List.of());
            model.addAttribute("favoriteArtistNames", List.of());
            model.addAttribute("favoriteArtists", List.of());

            if (authentication != null && authentication.getName() != null) {
                try {
                    User user = userRepository.findByUsername(authentication.getName());
                    if (user != null) {
                        try {
                            List<Playlist> playlists = playlistRepository.findByOwner(user);
                            model.addAttribute("playlists", playlists != null ? playlists : List.of());
                        } catch (Exception e) {
                        }
                        try {
                            List<Favorite> favorites = favoriteRepository.findByUser(user);
                            model.addAttribute("favorites", favorites != null ? favorites : List.of());
                            // Separate favorites by type and provide data for dynamic button rendering
                            java.util.List<String> favoriteTrackIds = new java.util.ArrayList<>();
                            java.util.List<String> favoriteArtistNames = new java.util.ArrayList<>();
                            java.util.List<Favorite> favoriteArtists = new java.util.ArrayList<>();
                            if (favorites != null) {
                                for (Favorite fav : favorites) {
                                    if (fav.getAudioFile() != null && fav.getAudioFile().getItunesTrackId() != null) {
                                        // Always store as string for robust Thymeleaf comparison
                                        favoriteTrackIds.add(fav.getAudioFile().getItunesTrackId().toString());
                                    }
                                    if (fav.getArtistName() != null) {
                                        favoriteArtistNames.add(fav.getArtistName());
                                        favoriteArtists.add(fav);
                                    }
                                }
                            }
                            model.addAttribute("favoriteTrackIds", favoriteTrackIds);
                            model.addAttribute("favoriteArtistNames", favoriteArtistNames);
                            model.addAttribute("favoriteArtists", favoriteArtists);
                        } catch (Exception e) {
                        }
                        try {
                            List<Rating> ratings = ratingRepository.findAll();
                            model.addAttribute("ratings", ratings != null ? ratings : List.of());
                        } catch (Exception e) {
                        }
                        try {
                            List<AudioFile> audioFiles = audioFileService.getAudioFilesByUser(user.getId());
                            model.addAttribute("audioFiles", audioFiles != null ? audioFiles : List.of());
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] ChatController._chatPage failed: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("messages", List.of());
            model.addAttribute("playlists", List.of());
            model.addAttribute("favorites", List.of());
            model.addAttribute("ratings", List.of());
            model.addAttribute("audioFiles", List.of());
            model.addAttribute("favoriteTrackIds", List.of());
            model.addAttribute("favoriteArtistNames", List.of());
            model.addAttribute("favoriteArtists", List.of());
        }

        return "chat";
    }

    /**
     * Handles adding a new message or performing a search from the chat UI form.
     * Redirects back to chat page after processing.
     * 
     * @param authentication the authentication object
     * @param content        the message content (optional)
     * @param query          the search query (optional)
     * @param model          the model to populate for search results
     * @return redirect to chat view or search results
     */
    @PostMapping("/ui/chat")
    public String _addFromForm(Authentication authentication,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String query,
            Model model) {
        if (content != null && !content.trim().isEmpty()) {
            String sender = authentication.getName();
            repo.save(new Message(sender, content));
        }

        // Handle search query
        if (query != null && !query.trim().isEmpty()) {
            return searchMusic(query, model, authentication);
        }

        return "redirect:/ui/chat";
    }

    /**
     * Handles music search from the chat UI form.
     * Populates model with search results and user data.
     * 
     * @param query          the search query
     * @param model          the model to populate
     * @param authentication the authentication object
     * @return the chat view with search results
     */
    @PostMapping("/ui/chat/search-music")
    public String searchMusic(@RequestParam String query, Model model, Authentication authentication) {
        try {
            try {
                List<Message> messages = repo.findAll();
                List<Message> processedMessages = processMessagesForInteractiveContent(messages);
                model.addAttribute("messages", processedMessages != null ? processedMessages : List.of());
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to load messages in search: " + e.getMessage());
                model.addAttribute("messages", List.of());
            }

            model.addAttribute("playlists", List.of());
            model.addAttribute("favorites", List.of());
            model.addAttribute("ratings", List.of());
            model.addAttribute("audioFiles", List.of());
            model.addAttribute("favoriteTrackIds", List.of());
            model.addAttribute("favoriteArtistNames", List.of());
            model.addAttribute("favoriteArtists", List.of());

            if (authentication != null && authentication.getName() != null) {
                try {
                    User user = userRepository.findByUsername(authentication.getName());
                    if (user != null) {
                        try {
                            List<Playlist> playlists = playlistRepository.findByOwner(user);
                            model.addAttribute("playlists", playlists != null ? playlists : List.of());
                        } catch (Exception e) {
                            System.err.println("[ERROR] Failed to load playlists in search: " + e.getMessage());
                        }

                        java.util.List<String> favoriteTrackIds = new java.util.ArrayList<>();
                        java.util.List<String> favoriteArtistNames = new java.util.ArrayList<>();
                        java.util.List<Favorite> favoriteArtists = new java.util.ArrayList<>();
                        try {
                            List<Favorite> favorites = favoriteRepository.findByUser(user);
                            model.addAttribute("favorites", favorites != null ? favorites : List.of());
                            if (favorites != null) {
                                for (Favorite fav : favorites) {
                                    if (fav.getAudioFile() != null && fav.getAudioFile().getItunesTrackId() != null) {
                                        favoriteTrackIds.add(fav.getAudioFile().getItunesTrackId().toString());
                                    }
                                    if (fav.getArtistName() != null) {
                                        favoriteArtistNames.add(fav.getArtistName());
                                        favoriteArtists.add(fav);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("[ERROR] Failed to load favorites in search: " + e.getMessage());
                        }
                        model.addAttribute("favoriteTrackIds", favoriteTrackIds);
                        model.addAttribute("favoriteArtistNames", favoriteArtistNames);
                        model.addAttribute("favoriteArtists", favoriteArtists);

                        try {
                            List<Rating> ratings = ratingRepository.findAll();
                            model.addAttribute("ratings", ratings != null ? ratings : List.of());
                        } catch (Exception e) {
                            System.err.println("[ERROR] Failed to load ratings in search: " + e.getMessage());
                        }
                        try {
                            List<AudioFile> audioFiles = audioFileService.getAudioFilesByUser(user.getId());
                            model.addAttribute("audioFiles", audioFiles != null ? audioFiles : List.of());
                        } catch (Exception e) {
                            System.err.println("[ERROR] Failed to load audio files in search: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to find user in search: " + e.getMessage());
                }
            }

            if (query != null && !query.trim().isEmpty()) {
                try {
                    Object[] searchResults = musicSearchService.searchItunes(query);
                    for (Object obj : searchResults) {
                        if (obj instanceof java.util.Map) {
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                            Object id = map.get("trackId");
                            if (id != null && !(id instanceof String)) {
                                map.put("trackId", id.toString());
                            }
                        } else if (obj != null) {
                            try {
                                java.lang.reflect.Field field = obj.getClass().getDeclaredField("trackId");
                                field.setAccessible(true);
                                Object id = field.get(obj);
                                if (id != null && !(id instanceof String)) {
                                    field.set(obj, id.toString());
                                }
                            } catch (Exception ignore) {
                            }
                        }
                    }
                    model.addAttribute("searchResults", searchResults);
                    System.out.println("Search completed. Found " + searchResults.length + " results for: " + query);
                } catch (Exception e) {
                    model.addAttribute("searchError", "Search failed. Please try again.");
                    System.err.println("Search failed: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] searchMusic method failed: " + e.getMessage());
            e.printStackTrace();
        }

        return "chat";
    }

    /**
     * Handles sending a track message from the UI form.
     * Redirects back to chat page after sending.
     * 
     * @param trackName      the track name
     * @param artistName     the artist name
     * @param previewUrl     the preview URL
     * @param authentication the authentication object
     * @return redirect to chat view
     */
    @PostMapping("/ui/chat/send-track")
    public String sendTrack(@RequestParam String trackName, @RequestParam String artistName,
            @RequestParam String previewUrl, Authentication authentication) {
        String sender = authentication.getName();
        String message = String.format("[audio:%s] %s by %s", previewUrl, trackName, artistName);
        repo.save(new Message(sender, message));
        return "redirect:/ui/chat";
    }

    /**
     * Process messages to add interactive content for artist shares
     */
    private List<Message> processMessagesForInteractiveContent(List<Message> messages) {
        if (messages == null) return List.of();
        
        return messages.stream().map(message -> {
            String content = message.getContent();
            
            // Check if this is an artist share message
            if (content != null && content.startsWith("🎤 Check out this artist: ")) {
                // Extract artist name
                String artistName = content.substring("🎤 Check out this artist: ".length());
                
                // Add search button to the message
                String enhancedContent = content + 
                    String.format("<div class='shared-artist-actions'>" +
                        "<button type='button' class='btn btn-sm btn-primary search-artist-btn' " +
                        "data-artist-name='%s' onclick=\"openSearchForArtist('%s')\">" +
                        "🔍 Search %s</button>" +
                        "</div>", 
                        artistName.replace("\"", "&quot;").replace("'", "&#39;"),
                        artistName.replace("'", "\\'").replace("\"", "\\\""),
                        artistName);
                        
                // Create new message with enhanced content
                Message enhancedMessage = new Message(message.getSender(), enhancedContent);
                enhancedMessage.setId(message.getId());
                enhancedMessage.setCreatedAt(message.getCreatedAt());
                return enhancedMessage;
            }
            
            return message;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Toggles favorite status for a track from the UI (AJAX, returns JSON).
     * 
     * @param trackName      the track name
     * @param artistName     the artist name
     * @param previewUrl     the preview URL
     * @param itunesTrackId  the iTunes track ID
     * @param authentication the authentication object
     * @return JSON result with success and favorite status
     */
    @PostMapping("/ui/chat/toggle-favorite")
    @ResponseBody
    public java.util.Map<String, Object> toggleFavoriteUI(
            @RequestParam String trackName,
            @RequestParam String artistName,
            @RequestParam String previewUrl,
            @RequestParam String itunesTrackId,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        User user = userRepository.findByUsername(authentication.getName());
        boolean isNowFavorite = false;
        if (user != null) {
            AudioFile audioFile = null;
            
            // Check if this is an uploaded file (itunesTrackId starts with "uploaded-")
            if (itunesTrackId.startsWith("uploaded-")) {
                // Extract the actual file ID from "uploaded-{fileId}"
                try {
                    Long fileId = Long.parseLong(itunesTrackId.substring("uploaded-".length()));
                    audioFile = audioFileService.getAudioFile(fileId);
                    if (audioFile == null) {
                        result.put("success", false);
                        result.put("error", "Uploaded file not found");
                        return result;
                    }
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("error", "Invalid uploaded file ID");
                    return result;
                }
            } else {
                // Handle iTunes tracks as before
                java.util.List<AudioFile> found = audioFileService.findByItunesTrackId(itunesTrackId);
                if (found != null && !found.isEmpty()) {
                    audioFile = found.get(0);
                } else {
                    audioFile = audioFileService.saveFromItunes(trackName, artistName, previewUrl, itunesTrackId, user);
                }
            }
            
            try {
                isNowFavorite = favoriteService.toggleTrackFavorite(user, audioFile);
                result.put("success", true);
                result.put("favorite", isNowFavorite);
            } catch (Exception ex) {
                result.put("success", false);
                result.put("error", ex.getMessage());
            }
        } else {
            result.put("success", false);
            result.put("error", "User not found");
        }
        return result;
    }

    /**
     * Removes a favorite track from the UI (AJAX, returns JSON).
     * 
     * @param itunesTrackId  the iTunes track ID
     * @param authentication the authentication object
     * @return JSON result with success status
     */
    @PostMapping("/ui/chat/remove-favorite")
    @ResponseBody
    public java.util.Map<String, Object> removeFavoriteUI(@RequestParam String itunesTrackId,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        User user = userRepository.findByUsername(authentication.getName());
        if (user != null) {
            AudioFile audioFile = null;
            
            // Check if this is an uploaded file (itunesTrackId starts with "uploaded-")
            if (itunesTrackId.startsWith("uploaded-")) {
                // Extract the actual file ID from "uploaded-{fileId}"
                try {
                    Long fileId = Long.parseLong(itunesTrackId.substring("uploaded-".length()));
                    audioFile = audioFileService.getAudioFile(fileId);
                    if (audioFile == null) {
                        result.put("success", false);
                        result.put("error", "Uploaded file not found");
                        return result;
                    }
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("error", "Invalid uploaded file ID");
                    return result;
                }
            } else {
                // Handle iTunes tracks as before
                java.util.List<AudioFile> found = audioFileService.findByItunesTrackId(itunesTrackId);
                if (found != null && !found.isEmpty()) {
                    audioFile = found.get(0);
                } else {
                    result.put("success", false);
                    result.put("error", "Track not found");
                    return result;
                }
            }
            
            boolean removed = favoriteService.removeTrackFromFavorites(user, audioFile);
            result.put("success", removed);
            if (!removed) {
                result.put("error", "Favorite not found for this user");
            }
        } else {
            result.put("success", false);
            result.put("error", "User not found");
        }
        return result;
    }

    /**
     * Removes a favorite track from the UI (form, redirects to chat page).
     * 
     * @param itunesTrackId  the iTunes track ID
     * @param authentication the authentication object
     * @return redirect to chat page
     */
    @PostMapping("/ui/chat/remove-favorite-simple")
    public String removeFavoriteSimpleUI(@RequestParam String itunesTrackId, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user != null) {
            java.util.List<AudioFile> found = audioFileService.findByItunesTrackId(itunesTrackId);
            if (found != null && !found.isEmpty()) {
                AudioFile audioFile = found.get(0);
                favoriteService.removeTrackFromFavorites(user, audioFile);
            }
        }
        return "redirect:/ui/chat#favorites-tab";
    }

    /**
     * Handles playlist creation from the UI form (Thymeleaf).
     * Redirects to chat page after creation.
     *
     * @param name           the playlist name
     * @param authentication the authentication object
     * @return redirect to chat page
     */
    @PostMapping("/ui/chat/create-playlist")
    public String createPlaylistUI(@RequestParam String name, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user != null && name != null && !name.trim().isEmpty()) {
            playlistService.createPlaylist(name.trim(), null, false, user);
        }
        return "redirect:/ui/chat";
    }

    /**
     * Toggles favorite status for an artist from the UI form (Thymeleaf).
     * Redirects to chat page after toggling.
     *
     * @param artistName     the artist name
     * @param authentication the authentication object
     * @return redirect to chat page
     */
    @PostMapping("/ui/chat/favorite-artist")
    public String favoriteArtistUI(@RequestParam String artistName, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user != null && artistName != null && !artistName.trim().isEmpty()) {
            try {
                favoriteService.toggleArtistFavorite(user, artistName.trim());
            } catch (Exception e) {
                // Log error but continue with redirect
                System.err.println("Error toggling artist favorite: " + e.getMessage());
            }
        }
        return "redirect:/ui/chat";
    }

    /**
     * Toggles favorite status for an artist from the UI (AJAX, returns JSON).
     *
     * @param artistName     the artist name
     * @param authentication the authentication object
     * @return JSON result with success status and new favorite state
     */
    @PostMapping("/ui/chat/toggle-favorite-artist")
    @ResponseBody
    public java.util.Map<String, Object> toggleFavoriteArtistUI(@RequestParam String artistName,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        User user = userRepository.findByUsername(authentication.getName());
        if (user != null && artistName != null && !artistName.trim().isEmpty()) {
            try {
                boolean isNowFavorite = favoriteService.toggleArtistFavorite(user, artistName.trim());
                result.put("success", true);
                result.put("favorite", isNowFavorite);
            } catch (Exception e) {
                result.put("success", false);
                result.put("error", e.getMessage());
            }
        } else {
            result.put("success", false);
            result.put("error", "Invalid user or artist name");
        }
        return result;
    }

    /**
     * Rate a track from the UI (AJAX, returns JSON).
     * 
     * @param trackName      the track name
     * @param artistName     the artist name
     * @param previewUrl     the preview URL (optional)
     * @param rating         the rating value
     * @param authentication the authentication object
     * @return JSON result with success status
     */
    @PostMapping("/ui/chat/rate-track")
    @ResponseBody
    public java.util.Map<String, Object> rateTrackUI(
            @RequestParam String trackName,
            @RequestParam String artistName,
            @RequestParam(required = false) String previewUrl,
            @RequestParam int rating,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        if (authentication == null || authentication.getName() == null) {
            result.put("success", false);
            result.put("error", "Not authenticated");
            return result;
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            result.put("success", false);
            result.put("error", "User not found");
            return result;
        }
        AudioFile audioFile = audioFileService.findByTrackNameAndArtistName(trackName, artistName);
        if (audioFile == null) {
            audioFile = audioFileService.saveFromItunes(trackName, artistName, previewUrl, null, user);
        }
        try {
            ratingService.rateAudioFile(user, audioFile, rating, null);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * Add track to playlist from the UI (AJAX, returns JSON).
     * 
     * @param itunesTrackId  the iTunes track ID
     * @param playlistId     the playlist ID
     * @param trackName      the track name
     * @param artistName     the artist name
     * @param previewUrl     the preview URL
     * @param authentication the authentication object
     * @return JSON result with success status
     */
    @PostMapping("/ui/chat/add-track-to-playlist")
    @ResponseBody
    public java.util.Map<String, Object> addTrackToPlaylistUI(
            @RequestParam String itunesTrackId,
            @RequestParam Long playlistId,
            @RequestParam String trackName,
            @RequestParam String artistName,
            @RequestParam(required = false) String previewUrl,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        if (authentication == null || authentication.getName() == null) {
            result.put("success", false);
            result.put("error", "Not authenticated");
            return result;
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            result.put("success", false);
            result.put("error", "User not found");
            return result;
        }

        try {
            // Get or create the audio file
            List<AudioFile> audioFiles = audioFileService.findByItunesTrackId(itunesTrackId);
            AudioFile audioFile = null;
            if (audioFiles != null && !audioFiles.isEmpty()) {
                audioFile = audioFiles.get(0);
            }

            if (audioFile == null) {
                audioFile = audioFileService.saveFromItunes(trackName, artistName, previewUrl, itunesTrackId, user);
            }

            // Get the playlist
            java.util.Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (!playlistOpt.isPresent()) {
                result.put("success", false);
                result.put("error", "Playlist not found");
                return result;
            }

            Playlist playlist = playlistOpt.get();

            // Check if user can modify the playlist
            if (!playlistService.canUserModifyPlaylist(playlist, user)) {
                result.put("success", false);
                result.put("error", "Access denied");
                return result;
            }

            // Add track to playlist
            playlistService.addAudioFileToPlaylist(playlist, audioFile);
            result.put("success", true);
            result.put("message", "Track added to playlist");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to add track to playlist: " + e.getMessage());
        }

        return result;
    }

    /**
     * Remove track from playlist from the UI (AJAX, returns JSON).
     * 
     * @param playlistId     the playlist ID
     * @param audioFileId    the audio file ID
     * @param authentication the authentication object
     * @return JSON result with success status
     */
    @PostMapping("/ui/chat/remove-track-from-playlist")
    @ResponseBody
    public java.util.Map<String, Object> removeTrackFromPlaylistUI(
            @RequestParam Long playlistId,
            @RequestParam Long audioFileId,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        if (authentication == null || authentication.getName() == null) {
            result.put("success", false);
            result.put("error", "Not authenticated");
            return result;
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            result.put("success", false);
            result.put("error", "User not found");
            return result;
        }

        try {
            java.util.Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (!playlistOpt.isPresent()) {
                result.put("success", false);
                result.put("error", "Playlist not found");
                return result;
            }

            Playlist playlist = playlistOpt.get();

            if (!playlistService.canUserModifyPlaylist(playlist, user)) {
                result.put("success", false);
                result.put("error", "Access denied");
                return result;
            }

            AudioFile audioFile = audioFileService.getAudioFile(audioFileId);
            if (audioFile == null) {
                result.put("success", false);
                result.put("error", "Audio file not found");
                return result;
            }

            playlistService.removeAudioFileFromPlaylist(playlist, audioFile);
            result.put("success", true);
            result.put("message", "Track removed from playlist");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to remove track from playlist: " + e.getMessage());
        }

        return result;
    }

    /**
     * Delete playlist from the UI (AJAX, returns JSON).
     * 
     * @param playlistId     the playlist ID
     * @param authentication the authentication object
     * @return JSON result with success status
     */
    @PostMapping("/ui/chat/delete-playlist")
    @ResponseBody
    public java.util.Map<String, Object> deletePlaylistUI(
            @RequestParam Long playlistId,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        if (authentication == null || authentication.getName() == null) {
            result.put("success", false);
            result.put("error", "Not authenticated");
            return result;
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            result.put("success", false);
            result.put("error", "User not found");
            return result;
        }

        try {
            java.util.Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (!playlistOpt.isPresent()) {
                result.put("success", false);
                result.put("error", "Playlist not found");
                return result;
            }

            Playlist playlist = playlistOpt.get();

            if (!playlistService.canUserModifyPlaylist(playlist, user)) {
                result.put("success", false);
                result.put("error", "Access denied");
                return result;
            }

            playlistService.deletePlaylist(playlist);
            result.put("success", true);
            result.put("message", "Playlist deleted");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to delete playlist: " + e.getMessage());
        }

        return result;
    }

    /**
     * Toggle playlist public/private status from the UI (AJAX, returns JSON).
     * 
     * @param playlistId     the playlist ID
     * @param authentication the authentication object
     * @return JSON result with success status and new public state
     */
    @PostMapping("/ui/chat/toggle-playlist-public")
    @ResponseBody
    public java.util.Map<String, Object> togglePlaylistPublicUI(
            @RequestParam Long playlistId,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        if (authentication == null || authentication.getName() == null) {
            result.put("success", false);
            result.put("error", "Not authenticated");
            return result;
        }

        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            result.put("success", false);
            result.put("error", "User not found");
            return result;
        }

        try {
            java.util.Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (!playlistOpt.isPresent()) {
                result.put("success", false);
                result.put("error", "Playlist not found");
                return result;
            }

            Playlist playlist = playlistOpt.get();

            if (!playlistService.canUserModifyPlaylist(playlist, user)) {
                result.put("success", false);
                result.put("error", "Access denied");
                return result;
            }

            boolean newPublicState = !playlist.isPublic();
            playlistService.updatePlaylist(playlist, playlist.getName(), playlist.getDescription(), newPublicState);

            result.put("success", true);
            result.put("isPublic", newPublicState);
            result.put("message", newPublicState ? "Playlist is now public" : "Playlist is now private");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to update playlist: " + e.getMessage());
        }

        return result;
    }

    /**
     * Share playlist in chat endpoint.
     * 
     * @param playlistId     the playlist ID to share
     * @param authentication the authentication object
     * @return JSON result
     */
    @PostMapping("/ui/chat/share-playlist")
    @ResponseBody
    public java.util.Map<String, Object> sharePlaylistUI(@RequestParam Long playlistId, Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        try {
            User user = userRepository.findByUsername(authentication.getName());
            if (user == null) {
                result.put("success", false);
                result.put("error", "User not found");
                return result;
            }

            Optional<Playlist> playlistOpt = playlistService.getPlaylistById(playlistId);
            if (!playlistOpt.isPresent()
                    || (!playlistOpt.get().getOwner().equals(user) && !playlistOpt.get().isPublic())) {
                result.put("success", false);
                result.put("error", "Playlist not found or access denied");
                return result;
            }

            Playlist playlist = playlistOpt.get();

            String playlistInfo = String.format(
                    "<div class='shared-playlist' data-playlist-id='%d' data-playlist-name='%s' data-is-public='%b'>" +
                            "<div class='shared-content-header'>Shared playlist: <strong>\"%s\"</strong> (%d tracks)%s</div>"
                            +
                            "<div class='shared-playlist-actions'>" +
                            "<button class='btn btn-sm btn-info view-shared-playlist-btn' data-playlist-id='%d'>View Tracks</button>"
                            +
                            "%s" +
                            "</div>" +
                            "</div>",
                    playlist.getId(),
                    playlist.getName(),
                    playlist.isPublic(),
                    playlist.getName(),
                    playlist.getAudioFiles() != null ? playlist.getAudioFiles().size() : 0,
                    playlist.isPublic() ? " [Public]" : " [Private]",
                    playlist.getId(),
                    playlist.isPublic() ? String.format(
                            "<button class='btn btn-sm btn-success copy-shared-playlist-btn' data-playlist-id='%d'>Copy to My Playlists</button>",
                            playlist.getId()) : "");

            Message message = new Message(user.getUsername(), playlistInfo);
            repo.save(message);

            result.put("success", true);
            result.put("message", "Playlist shared in chat");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to share playlist: " + e.getMessage());
        }

        return result;
    }

    /**
     * Copy public playlist endpoint.
     * 
     * @param sourcePlaylistId the source playlist ID
     * @param newName          the new playlist name
     * @param authentication   the authentication object
     * @return JSON result
     */
    @PostMapping("/ui/chat/copy-playlist")
    @ResponseBody
    public java.util.Map<String, Object> copyPlaylistUI(@RequestParam Long sourcePlaylistId,
            @RequestParam String newName,
            Authentication authentication) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();

        try {
            User user = userRepository.findByUsername(authentication.getName());
            if (user == null) {
                result.put("success", false);
                result.put("error", "User not found");
                return result;
            }

            Optional<Playlist> sourcePlaylistOpt = playlistService.getPlaylistById(sourcePlaylistId);
            if (!sourcePlaylistOpt.isPresent() || !sourcePlaylistOpt.get().isPublic()) {
                result.put("success", false);
                result.put("error", "Source playlist not found or not public");
                return result;
            }

            Playlist sourcePlaylist = sourcePlaylistOpt.get();

            // Create new playlist
            Playlist newPlaylist = playlistService.createPlaylist(newName.trim(),
                    "Copied from " + sourcePlaylist.getName(), false, user);

            // Copy tracks from source playlist
            if (sourcePlaylist.getAudioFiles() != null) {
                for (AudioFile audioFile : sourcePlaylist.getAudioFiles()) {
                    playlistService.addAudioFileToPlaylist(newPlaylist, audioFile);
                }
            }

            result.put("success", true);
            result.put("message", "Playlist copied successfully");
            result.put("newPlaylistId", newPlaylist.getId());

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to copy playlist: " + e.getMessage());
        }

        return result;
    }

    /**
     * Check if a track exists in user's playlists.
     * 
     * @param trackName      the track name
     * @param artistName     the artist name
     * @param authentication the authentication object
     * @return JSON with playlist information
     */
    @Operation(
        summary = "Check track in playlists",
        description = "Checks if a specific track exists in any of the user's playlists",
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Track search completed successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/api/check-track-in-playlists")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkTrackInPlaylists(
            @Parameter(description = "Name of the track to search for") @RequestParam("trackName") String trackName,
            @Parameter(description = "Name of the artist") @RequestParam("artistName") String artistName,
            Authentication authentication) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userRepository.findByUsername(authentication.getName());
            if (user == null) {
                result.put("inPlaylists", false);
                result.put("playlists", new ArrayList<>());
                return ResponseEntity.ok(result);
            }

            List<Playlist> userPlaylists = playlistService.getPlaylistsByOwner(user);
            List<String> playlistsWithTrack = new ArrayList<>();

            for (Playlist playlist : userPlaylists) {
                if (playlist.getAudioFiles() != null) {
                    boolean hasTrack = playlist.getAudioFiles().stream()
                            .anyMatch(track -> track.getTrackName().equalsIgnoreCase(trackName.trim()) &&
                                    track.getArtistName().equalsIgnoreCase(artistName.trim()));
                    if (hasTrack) {
                        playlistsWithTrack.add(playlist.getName());
                    }
                }
            }

            result.put("inPlaylists", !playlistsWithTrack.isEmpty());
            result.put("playlists", playlistsWithTrack);

        } catch (Exception e) {
            result.put("inPlaylists", false);
            result.put("playlists", new ArrayList<>());
        }

        return ResponseEntity.ok(result);
    }
}
