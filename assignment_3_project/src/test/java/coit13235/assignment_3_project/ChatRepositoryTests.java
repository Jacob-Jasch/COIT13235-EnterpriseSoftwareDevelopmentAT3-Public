package coit13235.assignment_3_project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import coit13235.assignment_3_project.entity.Message;
import coit13235.assignment_3_project.repository.MessageRepository;


/**
 * Tests for the MessageRepository JPA interface.
 */
@DataJpaTest
class ChatRepositoryTests {

    @Autowired
    MessageRepository repo;

    /**
     * Saving a message should set its ID and createdAt timestamp.
     */
    @Test
    void save_sets_id_and_createdAt() {
        Message m = new Message("TestUser", "Okay");
        Message saved = repo.save(m);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
