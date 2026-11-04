package coit13235.assignment_3_project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles browser-generated resource requests to avoid logging them as errors.
     * @param ex the NoResourceFoundException
     * @param request the HTTP request
     * @return 404 status without error page
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleBrowserResourceNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        // Check if it's a browser-generated request - don't log these as errors
        String requestURI = request.getRequestURI();
        if (requestURI != null && (
            requestURI.contains("/.well-known/") ||
            requestURI.contains("com.chrome.devtools") ||
            requestURI.contains("apple-touch-icon") ||
            requestURI.contains("browserconfig.xml") ||
            requestURI.contains("manifest.json")
        )) {
            // Silently return 404 for browser-generated requests without logging
            return;
        }
        // For other resource not found errors, log them
        System.err.println("[GLOBAL EXCEPTION HANDLER] Resource not found: " + requestURI);
        System.err.println("[GLOBAL EXCEPTION HANDLER] Message: " + ex.getMessage());
    }

    /**
     * Handles all other uncaught exceptions and displays a generic error page.
     * @param ex the exception
     * @param model the model
     * @param request the HTTP request
     * @return the error view name
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex, Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        if (requestURI != null && (
            requestURI.contains("/.well-known/") ||
            requestURI.contains("com.chrome.devtools") ||
            requestURI.contains("apple-touch-icon") ||
            requestURI.contains("browserconfig.xml") ||
            requestURI.contains("manifest.json")
        )) {
            model.addAttribute("errorMessage", "Resource not found");
            return "error";
        }
        
        System.err.println("[GLOBAL EXCEPTION HANDLER] Caught exception: " + ex.getClass().getSimpleName());
        System.err.println("[GLOBAL EXCEPTION HANDLER] Message: " + ex.getMessage());
        ex.printStackTrace();
        
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
        return "error";
    }
}
