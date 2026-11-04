package coit13235.assignment_3_project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Integration tests for chat API and web endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ChatApiTests {

   @Autowired
   MockMvc mvc;

   /**
    * Unauthenticated users are redirected to the login page when accessing root.
    */
   @Test
   void root_redirects_to_login_when_unauthenticated() throws Exception {
      mvc.perform(get("/"))
         .andExpect(status().is3xxRedirection())
         .andExpect(header().string("Location", containsString("/login")));
   }

   /**
    * API endpoints require authentication.
    */
   @Test
   void api_requires_auth() throws Exception {
      mvc.perform(get("/api/chat"))
         .andExpect(status().isUnauthorized());
   }

   /**
    * Authenticated users can post and then list messages over the API.
    */
   @Test
   void can_post_and_then_list_messages_over_api() throws Exception {
      mvc.perform(post("/api/chat")
            .with(httpBasic("user","password"))
            .param("content", "Hello from test"))
         .andExpect(status().isOk());

      mvc.perform(get("/api/chat").with(httpBasic("user","password")))
         .andExpect(status().isOk())
         .andExpect(content().string(containsString("Hello from test")));
   }

   /**
    * Form POST persists a message and redirects to the chat page.
    */
   @Test
   void form_post_persists_and_redirects() throws Exception {
      mvc.perform(post("/ui/chat")
            .with(user("user").roles("USER"))
            .param("content", "Form message"))
         .andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrl("/ui/chat"));

      mvc.perform(get("/api/chat").with(httpBasic("user","password")))
         .andExpect(status().isOk())
         .andExpect(content().string(containsString("Form message")));
   }

   /**
    * The login page loads for unauthenticated users.
    */
   @Test
   void login_page_loads() throws Exception {
      mvc.perform(get("/login"))
         .andExpect(status().isOk())
         .andExpect(content().string(containsString("Sign in")));
   }
}
