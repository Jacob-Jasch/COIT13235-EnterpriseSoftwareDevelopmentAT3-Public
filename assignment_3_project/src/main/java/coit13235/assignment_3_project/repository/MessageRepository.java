package coit13235.assignment_3_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import coit13235.assignment_3_project.entity.Message;

/**
 * Repository interface for Message entities.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {}
