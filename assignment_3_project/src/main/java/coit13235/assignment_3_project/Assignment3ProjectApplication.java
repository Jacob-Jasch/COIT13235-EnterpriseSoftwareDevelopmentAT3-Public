package coit13235.assignment_3_project;

import org.springframework.core.annotation.Order;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import coit13235.assignment_3_project.entity.Message;

import coit13235.assignment_3_project.repository.MessageRepository;
import coit13235.assignment_3_project.service.UserService;


/**
 * Main Spring Boot application class.
 */
@SpringBootApplication
public class Assignment3ProjectApplication {

	/**
	 * Application entry point.
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Assignment3ProjectApplication.class, args);
	}

}

/**
 * Loads seed data into the database on startup if empty.
 */
@Component
@Order(1)
class DataLoader implements ApplicationRunner {
	private final MessageRepository messageRepo;
	private final UserService userService;

	/**
     * Constructor for dependency injection.
     * @param messageRepo the message repository
     * @param userService the user service
     */
	public DataLoader(MessageRepository messageRepo, UserService userService) {
		this.messageRepo = messageRepo;
		this.userService = userService;
	}

	/**
     * Runs the data loader to insert seed data if the database is empty.
     * @param args application arguments
     */
	@Override
	public void run(ApplicationArguments args) {
		// Create default users if database is empty
		userService.createDefaultUsers();
		
		// Create seed messages if database is empty
		if (messageRepo.count() == 0) {
			messageRepo.save(new Message("Alice", "Hello! This is a seed message."));
			messageRepo.save(new Message("Bob", "Hi Alice welcome to chat."));
		}
	}
}