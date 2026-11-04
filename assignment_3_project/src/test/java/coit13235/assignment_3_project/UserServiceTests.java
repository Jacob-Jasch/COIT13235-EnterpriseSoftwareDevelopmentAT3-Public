package coit13235.assignment_3_project;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.UserRepository;
import coit13235.assignment_3_project.repository.AudioFileRepository;
import coit13235.assignment_3_project.repository.PlaylistRepository;
import coit13235.assignment_3_project.repository.FavoriteRepository;
import coit13235.assignment_3_project.repository.RatingRepository;
import coit13235.assignment_3_project.service.UserService;

/**
 * Tests for multi-user authentication functionality.
 */
@SpringBootTest
@Transactional
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AudioFileRepository audioFileRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testUserServiceRegistration() {
        // Test user registration through service
        User user = userService.registerUser("testuser", "testpassword123");
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());

        // Verify user was created in database
        User dbUser = userRepository.findByUsername("testuser");
        assertNotNull(dbUser);
        assertEquals("testuser", dbUser.getUsername());
        assertTrue(passwordEncoder.matches("testpassword123", dbUser.getPassword()));
    }

    @Test
    public void testDuplicateUsernameRejection() {
        // First registration
        userService.registerUser("duplicate", "password123");

        // Try to register with same username
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser("duplicate", "newpassword123");
        });

        // Verify only one user exists
        assertEquals(1, userRepository.findAll().stream()
                .filter(u -> "duplicate".equals(u.getUsername()))
                .count());
    }

    @Test
    public void testUserServiceFunctionality() {
        // Test user registration through service
        User user = userService.registerUser("servicetest", "password123");
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("servicetest", user.getUsername());

        // Test username exists check
        assertTrue(userService.usernameExists("servicetest"));
        assertFalse(userService.usernameExists("nonexistent"));

        // Test get user by username
        User retrieved = userService.getUserByUsername("servicetest");
        assertNotNull(retrieved);
        assertEquals("servicetest", retrieved.getUsername());
    }

    @Test
    public void testMultipleUserCreation() {
        // Create multiple users
        userService.registerUser("user1", "password1");
        userService.registerUser("user2", "password2");
        userService.registerUser("user3", "password3");

        // Verify all users exist
        assertTrue(userService.usernameExists("user1"));
        assertTrue(userService.usernameExists("user2"));
        assertTrue(userService.usernameExists("user3"));

        // Verify passwords are encrypted
        User user1 = userService.getUserByUsername("user1");
        assertNotEquals("password1", user1.getPassword()); // Should be encrypted
        assertTrue(passwordEncoder.matches("password1", user1.getPassword()));
    }

    @Test
    @Transactional
    public void testDefaultUsersCreation() {
        // Clear all dependent data first to avoid foreign key constraints
        ratingRepository.deleteAll();
        favoriteRepository.deleteAll();
        playlistRepository.deleteAll();
        audioFileRepository.deleteAll();
        userRepository.deleteAll();

        // Run the default user creation
        userService.createDefaultUsers();

        // Verify default users were created
        assertTrue(userService.usernameExists("user"));
        assertTrue(userService.usernameExists("alice"));
        assertTrue(userService.usernameExists("bob"));
        assertTrue(userService.usernameExists("charlie"));
        assertTrue(userService.usernameExists("diana"));

        // Verify total count (should be exactly 5 default users)
        assertEquals(5, userRepository.count());
    }

    @Test
    public void testUserEntityConstructors() {
        // Test default constructor
        User user1 = new User();
        assertNull(user1.getUsername());
        assertNull(user1.getPassword());

        // Test parameterized constructor
        User user2 = new User("testuser", "testpass");
        assertEquals("testuser", user2.getUsername());
        assertEquals("testpass", user2.getPassword());
    }

    @Test
    public void testPasswordEncryption() {
        // Register a user with a known password
        String plainPassword = "mySecretPassword123";
        User user = userService.registerUser("testencrypt", plainPassword);

        // Verify password is encrypted (not stored in plain text)
        assertNotEquals(plainPassword, user.getPassword());
        
        // Verify password can be verified correctly
        assertTrue(passwordEncoder.matches(plainPassword, user.getPassword()));
        
        // Verify wrong password doesn't match
        assertFalse(passwordEncoder.matches("wrongPassword", user.getPassword()));
    }

    @Test
    public void testTenUsersCapacity() {
        // Test that we can create exactly 10 users as requested
        for (int i = 1; i <= 10; i++) {
            String username = "user" + i;
            String password = "password" + i;
            User user = userService.registerUser(username, password);
            assertNotNull(user);
            assertEquals(username, user.getUsername());
        }

        // Verify all 10 users exist
        for (int i = 1; i <= 10; i++) {
            String username = "user" + i;
            assertTrue(userService.usernameExists(username));
            User user = userService.getUserByUsername(username);
            assertNotNull(user);
            assertTrue(passwordEncoder.matches("password" + i, user.getPassword()));
        }

        // Verify we have at least 10 users
        assertTrue(userRepository.count() >= 10);
    }
}