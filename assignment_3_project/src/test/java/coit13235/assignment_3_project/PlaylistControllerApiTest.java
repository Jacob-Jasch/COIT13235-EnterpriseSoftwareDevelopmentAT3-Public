package coit13235.assignment_3_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PlaylistControllerApiTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper objectMapper;

  // Accept 200 OK or 201 Created for create endpoints
  private static ResultMatcher isOkOrCreated() {
    return result -> {
      int sc = result.getResponse().getStatus();
      assertTrue(sc == 200 || sc == 201,
          "Expected 200 OK or 201 Created, but got " + sc);
    };
  }

  @Test
  public void create_playlist_and_list_mine() throws Exception {
    // Create playlist (note: JSON boolean key is "public")
    String body = "{\"name\":\"MyList\",\"public\":true}";
    mvc.perform(post("/api/playlists")
            .with(httpBasic("user","password"))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
       .andExpect(isOkOrCreated())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.name").value("MyList"));

    // List my playlists
    mvc.perform(get("/api/playlists/my")
            .with(httpBasic("user","password")))
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$[*].name").isArray())
       .andExpect(jsonPath("$[*].name").value(org.hamcrest.Matchers.hasItem("MyList")));
  }

  @Test
  public void add_and_remove_track_in_playlist() throws Exception {
    // 1) Create playlist (owner = 'user')
    String pJson = mvc.perform(post("/api/playlists")
            .with(httpBasic("user","password"))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Rock\",\"public\":true}"))
        .andExpect(isOkOrCreated())
        .andReturn().getResponse().getContentAsString();
    long playlistId = objectMapper.readTree(pJson).path("id").asLong();

    // 2) Upload an audio file -> audioId
    MockMultipartFile f = new MockMultipartFile(
        "file", "x.mp3", "audio/mpeg", "abc".getBytes());
    String aJson = mvc.perform(multipart("/api/audio/upload")
            .file(f)
            .with(httpBasic("user","password"))
            .with(csrf()))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    long audioId = objectMapper.readTree(aJson).path("id").asLong();

    // 3) Add track
    mvc.perform(post("/api/playlists/{id}/tracks/{audioId}", playlistId, audioId)
            .with(httpBasic("user","password"))
            .with(csrf()))
       .andExpect(status().isOk());

    // 4) List tracks
    mvc.perform(get("/api/playlists/{id}/tracks", playlistId)
            .with(httpBasic("user","password")))
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$[*].id").value(org.hamcrest.Matchers.hasItem((int) audioId)));

    // 5) Remove track
    mvc.perform(delete("/api/playlists/{id}/tracks/{audioId}", playlistId, audioId)
            .with(httpBasic("user","password"))
            .with(csrf()))
       .andExpect(status().isOk());
  }

  @Test
  public void rename_playlist_not_owner_forbidden() throws Exception {
    // Create playlist as 'user'
    String created = mvc.perform(post("/api/playlists")
            .with(httpBasic("user","password"))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"BobList\",\"public\":false}"))
        .andExpect(isOkOrCreated())
        .andReturn().getResponse().getContentAsString();
    long id = objectMapper.readTree(created).path("id").asLong();

    // Try to rename as a different (non-owner) user -> expect 403
    mvc.perform(put("/api/playlists/{id}", id)
            .with(httpBasic("bob","password"))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Hacked\"}"))
       .andExpect(status().isForbidden());
  }

  @Test
  public void get_public_playlist_ok_and_private_denied() throws Exception {
    // Public playlist (owned by 'user')
    String pubJson = mvc.perform(post("/api/playlists")
            .with(httpBasic("user","password"))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Pub\",\"public\":true}"))
        .andExpect(isOkOrCreated())
        .andReturn().getResponse().getContentAsString();
    long pubId = objectMapper.readTree(pubJson).path("id").asLong();

    // Private playlist (owned by 'user')
    String privJson = mvc.perform(post("/api/playlists")
            .with(httpBasic("user","password"))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Priv\",\"public\":false}"))
        .andExpect(isOkOrCreated())
        .andReturn().getResponse().getContentAsString();
    long privId = objectMapper.readTree(privJson).path("id").asLong();

    // Access public as 'user' => 200 OK
    mvc.perform(get("/api/playlists/{id}", pubId)
            .with(httpBasic("user","password")))
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    // Access private as another user (bob) => 403
    mvc.perform(get("/api/playlists/{id}", privId)
            .with(user("bob").roles("USER")))
       .andExpect(status().isForbidden());
  }

  @Test
  public void list_mine_requires_auth_401() throws Exception {
    // Unauthenticated request must return 401
    mvc.perform(get("/api/playlists/my"))
       .andExpect(status().isUnauthorized());
  }
}
