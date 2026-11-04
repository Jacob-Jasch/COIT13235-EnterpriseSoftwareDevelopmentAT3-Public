package coit13235.assignment_3_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import coit13235.assignment_3_project.entity.User;

/**
 * Repository interface for User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by username.
     * @param username the username
     * @return the user, or null if not found
     */
    User findByUsername(String username);
}
