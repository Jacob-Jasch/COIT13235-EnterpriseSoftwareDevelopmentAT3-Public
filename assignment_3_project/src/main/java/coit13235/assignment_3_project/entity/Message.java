package coit13235.assignment_3_project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity representing a chat message.
 */
@Entity
@Table(name = "message")
public class Message {

	/**
	 * Unique identifier for the message.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Username of the sender.
	 */
	private String sender;

	/**
	 * Content of the message.
	 */
	@Column(length = 1000)
	private String content;

	/**
	 * Timestamp when the message was created.
	 */
	private LocalDateTime createdAt;

	/**
	 * Default constructor.
	 */
	public Message() {}

	/**
	 * Constructs a message with sender and content.
	 * @param sender the sender's username
	 * @param content the message content
	 */
	public Message(String sender, String content) {
		this.sender = sender;
		this.content = content;
		this.createdAt = LocalDateTime.now();
	}

	/**
	 * Gets the message ID.
	 * @return the ID
	 */
	public Long getId() { return id; }
	/**
	 * Sets the message ID.
	 * @param id the ID
	 */
	public void setId(Long id) { this.id = id; }

	/**
	 * Gets the sender's username.
	 * @return the sender
	 */
	/**
	 * Gets the sender's username.
	 * @return the sender's username
	 */
	public String getSender() { return sender; }

	/**
	 * Sets the sender's username.
	 * @param sender the sender's username
	 */
	public void setSender(String sender) { this.sender = sender; }

	/**
	 * Gets the message content.
	 * @return the message content
	 */
	public String getContent() { return content; }

	/**
	 * Sets the message content.
	 * @param content the message content
	 */
	public void setContent(String content) { this.content = content; }

	/**
	 * Gets the creation timestamp of the message.
	 * @return the creation timestamp
	 */
	public LocalDateTime getCreatedAt() { return createdAt; }

	/**
	 * Sets the creation timestamp of the message.
	 * @param createdAt the creation timestamp
	 */
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
