package coit13235.assignment_3_project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class AudioControllerApiTest {

  @Autowired MockMvc mvc;

  @Test
  void upload_audio_ok() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "song.mp3", "audio/mpeg", "abc".getBytes());

    mvc.perform(multipart("/api/audio/upload")
            .file(file)
            .with(httpBasic("user","password")))
       .andExpect(status().isOk())
       .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void upload_audio_rejects_bad_mime() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "note.txt", "text/plain", "oops".getBytes());

    mvc.perform(multipart("/api/audio/upload")
            .file(file)
            .with(httpBasic("user","password")))
       .andExpect(status().isBadRequest());
  }

  @Test
  void upload_requires_auth() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "song.mp3", "audio/mpeg", "abc".getBytes());

    mvc.perform(multipart("/api/audio/upload").file(file))
       .andExpect(status().isUnauthorized());
  }

  @Test
  void list_my_audio_ok() throws Exception {
    mvc.perform(get("/api/audio/list")
            .with(httpBasic("user","password")))
       .andExpect(status().isOk())
       .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}
