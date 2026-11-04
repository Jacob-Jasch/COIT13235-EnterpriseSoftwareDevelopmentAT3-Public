package coit13235.assignment_3_project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.UserRepository;

/**
 * Service for user management and authentication.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Loads user by username for Spring Security.
     * @param username the username
     * @return UserDetails for authentication
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }

    /**
     * Registers a new user.
     * @param username the username
     * @param password the plain text password
     * @return the created user
     * @throws IllegalArgumentException if username already exists
     */
    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        
        return userRepository.save(user);
    }

    /**
     * Checks if a username already exists.
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    /**
     * Gets a user by username.
     * @param username the username
     * @return the user or null if not found
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Creates default users if the database is empty.
     */
    public void createDefaultUsers() {
        if (userRepository.count() == 0) {
            // Create default users
            registerUser("user", "password");
            registerUser("alice", "password");
            registerUser("bob", "password");
            registerUser("charlie", "password");
            registerUser("diana", "password");
        }
    }
}