package coit13235.assignment_3_project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
// ...existing code...
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Additional integration tests for error handling, edge cases, and security.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ChatExtraTests {
    @Autowired
    MockMvc mvc;


    /**
     * Posting a very long message content should succeed (if within DB limit).
     */
    @Test
    void can_post_long_message() throws Exception {
        String longMsg = "a".repeat(900);
        mvc.perform(post("/ui/chat")
                .with(user("user").roles("USER"))
                .param("content", longMsg))
           .andExpect(status().is3xxRedirection());
    }

    /**
     * Unauthenticated users cannot access /ui/chat and are redirected to login.
     */
    @Test
    void unauthenticated_redirected_to_login() throws Exception {
        mvc.perform(get("/ui/chat"))
           .andExpect(status().is3xxRedirection())
           .andExpect(header().string("Location", containsString("/login")));
    }

    /**
     * Logout redirects to login page with logout param.
     */
    @Test
    void logout_redirects_to_login() throws Exception {
        mvc.perform(post("/logout").with(user("user").roles("USER")))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/login?logout"));
    }
}
