package coit13235.assignment_3_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import coit13235.assignment_3_project.entity.User;
import coit13235.assignment_3_project.repository.UserRepository;
import coit13235.assignment_3_project.repository.FavoriteRepository;
import coit13235.assignment_3_project.repository.PlaylistRepository;
import coit13235.assignment_3_project.repository.RatingRepository;
import coit13235.assignment_3_project.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication and login page.
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private RatingRepository ratingRepository;

    /**
     * Serves the login page for Spring Security.
     * @return the login template name
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Serves the user registration page.
     * @return the registration template name
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Handles user registration.
     * @param username the username
     * @param password the password
     * @param confirmPassword the password confirmation
     * @param redirectAttributes redirect attributes for flash messages
     * @return redirect to login page or back to registration with errors
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, 
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              RedirectAttributes redirectAttributes) {
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Username cannot be empty");
            return "redirect:/register";
        }
        
        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Password cannot be empty");
            return "redirect:/register";
        }
        
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/register";
        }
        
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long");
            return "redirect:/register";
        }
        
        try {
            userService.registerUser(username.trim(), password);
            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    /**
     * Debug endpoint to show current user's data counts.
     * @param authentication the authentication object
     * @return JSON with user data counts
     */
    @GetMapping("/debug/user-data")
    @ResponseBody
    public Map<String, Object> getUserData(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        
        if (authentication == null) {
            result.put("error", "Not authenticated");
            return result;
        }
        
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            result.put("error", "User not found");
            return result;
        }
        
        result.put("username", user.getUsername());
        result.put("userId", user.getId());
        result.put("favoritesCount", favoriteRepository.findByUser(user).size());
        result.put("playlistsCount", playlistRepository.findByOwner(user).size());
        result.put("ratingsCount", ratingRepository.findByUser(user).size());
        
        return result;
    }
}
