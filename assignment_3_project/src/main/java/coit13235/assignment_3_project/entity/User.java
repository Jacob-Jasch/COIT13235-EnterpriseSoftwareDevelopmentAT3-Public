
package coit13235.assignment_3_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
 * JPA Entity representing a user.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false)
    private String password;

    /**
     * Default constructor.
     */
    public User() {}

    /**
     * Constructor with username and password.
     * @param username the username
     * @param password the password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the user ID.
     * @return the ID
     */
    public Long getId() { return id; }
    /**
     * Sets the user ID.
     * @param id the ID
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Gets the username.
     * @return the username
     */
    public String getUsername() { return username; }
    /**
     * Sets the username.
     * @param username the username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword() { return password; }
    /**
     * Sets the password.
     * @param password the password
     */
    public void setPassword(String password) { this.password = password; }
}
