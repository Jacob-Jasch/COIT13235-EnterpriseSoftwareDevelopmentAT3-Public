package coit13235.assignment_3_project.console;

import java.util.Scanner;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import coit13235.assignment_3_project.entity.Message;
import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.entity.AudioFile;
import coit13235.assignment_3_project.entity.Playlist;
import coit13235.assignment_3_project.repository.MessageRepository;
import coit13235.assignment_3_project.repository.UserRepository;
import coit13235.assignment_3_project.repository.AudioFileRepository;
import coit13235.assignment_3_project.repository.PlaylistRepository;
import coit13235.assignment_3_project.service.MusicSearchService;

/**
 * Console interface for the Music Chat application.
 * Provides command-line access to chat, music, playlist, and rating functionality.
 * Used for automated testing and console-based interaction.
 */
@Component
public class ConsoleInterface implements CommandLineRunner {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AudioFileRepository audioFileRepository;
    
    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private MusicSearchService musicSearchService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private Scanner scanner;
    private User currentUser;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if console mode is requested via args OR profile
        boolean consoleMode = false;
        
        // Check command line arguments
        for (String arg : args) {
            if ("--console".equals(arg) || "-c".equals(arg) || "--console-mode".equals(arg)) {
                consoleMode = true;
                break;
            }
        }
        
        // Also check if running with console profile
        // This allows: mvn spring-boot:run -Dspring-boot.run.profiles=console
        if (System.getProperty("spring.profiles.active") != null && 
            System.getProperty("spring.profiles.active").contains("console")) {
            consoleMode = true;
        }
        
        if (consoleMode) {
            startConsoleInterface();
        }
    }
    
    /**
     * Starts the console interface main loop.
     */
    public void startConsoleInterface() {
        scanner = new Scanner(System.in);
        
        System.out.println("=".repeat(60));
        System.out.println("    MUSIC CHAT CONSOLE INTERFACE");
        System.out.println("    X/Twitter + Spotify System");
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Login process
        if (!performLogin()) {
            System.out.println("Login failed. Exiting...");
            return;
        }
        
        // Main menu loop
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice.toLowerCase()) {
                case "1":
                case "chat":
                    handleChatMenu();
                    break;
                case "2":
                case "music":
                    handleMusicMenu();
                    break;
                case "3":
                case "data":
                    handleDataMenu();
                    break;
                case "4":
                case "test":
                    runAutomatedTests();
                    break;
                case "5":
                case "exit":
                case "quit":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Handles user login process.
     * @return true if login successful, false otherwise
     */
    private boolean performLogin() {
        System.out.println("Please login to continue:");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + username + "!");
            System.out.println();
            return true;
        }
        
        System.out.println("Invalid username or password.");
        return false;
    }
    
    /**
     * Displays the main menu.
     */
    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("MAIN MENU - User: " + currentUser.getUsername());
        System.out.println("=".repeat(40));
        System.out.println("1. Chat Management");
        System.out.println("2. Music Search");
        System.out.println("3. View Data");
        System.out.println("4. Run Automated Tests");
        System.out.println("5. Exit");
        System.out.println("=".repeat(40));
        System.out.print("Enter your choice (1-5): ");
    }
    
    /**
     * Handles chat-related operations.
     */
    private void handleChatMenu() {
        System.out.println("\n--- CHAT MANAGEMENT ---");
        System.out.println("1. Send Message");
        System.out.println("2. View All Messages");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                sendMessage();
                break;
            case "2":
                viewAllMessages();
                break;
            case "3":
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    /**
     * Sends a new chat message.
     */
    private void sendMessage() {
        System.out.print("Enter your message: ");
        String content = scanner.nextLine().trim();
        
        if (!content.isEmpty()) {
            Message message = new Message(currentUser.getUsername(), content);
            messageRepository.save(message);
            System.out.println("Message sent successfully!");
        } else {
            System.out.println("Message cannot be empty.");
        }
    }
    
    /**
     * Views all messages.
     */
    private void viewAllMessages() {
        List<Message> messages = messageRepository.findAll();
        
        System.out.println("\n--- ALL MESSAGES ---");
        if (messages.isEmpty()) {
            System.out.println("No messages found.");
        } else {
            for (Message msg : messages) {
                System.out.printf("[%s] %s: %s%n", 
                    msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : "No timestamp", 
                    msg.getSender(), 
                    msg.getContent());
            }
        }
    }
    
    /**
     * Handles music search operations.
     */
    private void handleMusicMenu() {
        System.out.println("\n--- MUSIC SEARCH ---");
        System.out.println("1. Search iTunes Music");
        System.out.println("2. Back to Main Menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                searchMusic();
                break;
            case "2":
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    /**
     * Searches for music using iTunes API.
     */
    private void searchMusic() {
        System.out.print("Enter search term (artist or song): ");
        String searchTerm = scanner.nextLine().trim();
        
        if (!searchTerm.isEmpty()) {
            try {
                Object[] results = musicSearchService.searchItunes(searchTerm);
                
                System.out.println("\n--- SEARCH RESULTS ---");
                if (results == null || results.length == 0) {
                    System.out.println("No music found for: " + searchTerm);
                } else {
                    System.out.println("Found " + results.length + " tracks for: " + searchTerm);
                    System.out.println("(iTunes API search completed successfully)");
                }
            } catch (Exception e) {
                System.out.println("Error searching music: " + e.getMessage());
            }
        } else {
            System.out.println("Search term cannot be empty.");
        }
    }
    
    /**
     * Handles data viewing operations.
     */
    private void handleDataMenu() {
        System.out.println("\n--- DATA VIEWER ---");
        System.out.println("1. View Users");
        System.out.println("2. View Audio Files");
        System.out.println("3. View Playlists");
        System.out.println("4. Back to Main Menu");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                viewUsers();
                break;
            case "2":
                viewAudioFiles();
                break;
            case "3":
                viewPlaylists();
                break;
            case "4":
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    /**
     * Views all users.
     */
    private void viewUsers() {
        List<User> users = userRepository.findAll();
        
        System.out.println("\n--- ALL USERS ---");
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (User user : users) {
                System.out.printf("ID: %d - Username: %s%n", user.getId(), user.getUsername());
            }
        }
    }
    
    /**
     * Views all audio files.
     */
    private void viewAudioFiles() {
        List<AudioFile> audioFiles = audioFileRepository.findAll();
        
        System.out.println("\n--- ALL AUDIO FILES ---");
        if (audioFiles.isEmpty()) {
            System.out.println("No audio files found.");
        } else {
            for (AudioFile audio : audioFiles) {
                System.out.printf("ID: %d - %s by %s%n", 
                    audio.getId(), 
                    audio.getTrackName() != null ? audio.getTrackName() : "Unknown Track", 
                    audio.getArtistName() != null ? audio.getArtistName() : "Unknown Artist");
            }
        }
    }
    
    /**
     * Views all playlists.
     */
    private void viewPlaylists() {
        List<Playlist> playlists = playlistRepository.findAll();
        
        System.out.println("\n--- ALL PLAYLISTS ---");
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
        } else {
            for (Playlist playlist : playlists) {
                System.out.printf("ID: %d - %s (%s)%n", 
                    playlist.getId(), 
                    playlist.getName(), 
                    playlist.isPublic() ? "Public" : "Private");
            }
        }
    }
    
    /**
     * Runs automated tests for the console interface.
     */
    private void runAutomatedTests() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    RUNNING AUTOMATED TESTS");
        System.out.println("=".repeat(50));
        
        int totalTests = 0;
        int passedTests = 0;
        
        // Test 1: Message creation
        totalTests++;
        System.out.print("Test 1: Message Creation... ");
        try {
            Message testMessage = new Message(currentUser.getUsername(), "Test message from console");
            messageRepository.save(testMessage);
            System.out.println("PASSED");
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        
        // Test 2: User authentication data
        totalTests++;
        System.out.print("Test 2: User Authentication... ");
        try {
            if (currentUser != null && currentUser.getUsername() != null) {
                System.out.println("PASSED");
                passedTests++;
            } else {
                System.out.println("FAILED: Current user is null");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        
        // Test 3: Database connectivity
        totalTests++;
        System.out.print("Test 3: Database Connectivity... ");
        try {
            long userCount = userRepository.count();
            long messageCount = messageRepository.count();
            long audioCount = audioFileRepository.count();
            long playlistCount = playlistRepository.count();
            System.out.println("PASSED (Users: " + userCount + ", Messages: " + messageCount + 
                             ", Audio: " + audioCount + ", Playlists: " + playlistCount + ")");
            passedTests++;
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        
        // Test 4: iTunes search functionality
        totalTests++;
        System.out.print("Test 4: iTunes Search Functionality... ");
        try {
            Object[] results = musicSearchService.searchItunes("Beatles");
            if (results != null) {
                System.out.println("PASSED (Found " + results.length + " tracks)");
                passedTests++;
            } else {
                System.out.println("FAILED: Search returned null");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        
        // Test 5: User data integrity
        totalTests++;
        System.out.print("Test 5: User Data Integrity... ");
        try {
            User testUser = userRepository.findByUsername(currentUser.getUsername());
            if (testUser != null && testUser.getId().equals(currentUser.getId())) {
                System.out.println("PASSED");
                passedTests++;
            } else {
                System.out.println("FAILED: User data mismatch");
            }
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
        
        // Test summary
        System.out.println("=".repeat(50));
        System.out.printf("TEST RESULTS: %d/%d passed (%.1f%%)%n", 
            passedTests, totalTests, (double)passedTests/totalTests * 100);
        System.out.println("=".repeat(50));
        
        if (passedTests == totalTests) {
            System.out.println("✓ All tests passed! System is functioning correctly.");
        } else {
            System.out.println("⚠ Some tests failed. Please check the system configuration.");
        }
    }
}