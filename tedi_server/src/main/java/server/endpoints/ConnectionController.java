package server.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.auth.SecurityService;
import server.endpoints.outputmodels.UserListOutputModel;
import server.entities.ConnectionEntity;
import server.entities.UserEntity;
import server.repositories.ConnectionRepository;
import server.repositories.UserRepository;
import server.services.NotificationService;
import server.services.UserEntityService;
import server.utilities.StorageManager;
import server.utilities.Validator;

import java.io.IOException;
import java.util.List;

@RestController
public class ConnectionController {
	
	@Autowired
	private StorageManager sm;
	
	@Autowired 
	private SecurityService secService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ConnectionRepository connRepo;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserEntityService userEntityService;

	//add a new connection between active user and the one specified by the parameter
	//returns 201 (created) if this creates a friend request
	//returns 200 (ok) if this accepts a friend request
	@PostMapping("/connect")
	public ResponseEntity<Object> connect(@RequestParam Long id) {
		
		UserEntity currUser = secService.currentUser();
		UserEntity connUser = userRepo.findById(id).orElse(null);
		if (connUser == null) {
			return new ResponseEntity<>("Not existing user with such id", HttpStatus.NOT_FOUND);
		}
		//if the user we are trying to connect is admin, quietly reject with "not existing user" error
		//has to be done in separate if because connUser.getRoles() can throw NullPointerException
		if (!Validator.validateUserAuth(connUser)) {
			return new ResponseEntity<>("Not existing user with such id", HttpStatus.NOT_FOUND);
		}
		//cant make yourself a connected person to you
		if (currUser.getId() == connUser.getId()) {
			return new ResponseEntity<>("Can't connect to self", HttpStatus.BAD_REQUEST);
		}
		if (connRepo.findByUserAndConnectedAndIsPending(currUser, connUser, false) != null ||
				connRepo.findByUserAndConnectedAndIsPending(connUser, currUser, false) != null) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		else if (connRepo.findByUserAndConnectedAndIsPending(connUser, currUser, true) != null) {
			ConnectionEntity connection = connRepo.findByUserAndConnectedAndIsPending(connUser, currUser, true);
			connection.setIsPending(false);
			connRepo.save(connection);
			String notificationMessage = "Connection request accepted from ? ?";
			notificationService.addNotification(connUser, currUser, notificationMessage);

			return new ResponseEntity<>(HttpStatus.OK);
		}
		else if (connRepo.findByUserAndConnectedAndIsPending(currUser, connUser, true) != null) {
			//can't resend pending request
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		else {
			ConnectionEntity connection = new ConnectionEntity(currUser, connUser, true);
			connRepo.save(connection);
			String notificationMessage = "Connection request sent from ? ?";
			notificationService.addNotification(connUser, currUser, notificationMessage);
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
		
	}
	
	//this can be used to delete connections or to reject connection requests
	//if needed, we can split the 2 functionalities later
	@DeleteMapping("/connection/delete")
	public ResponseEntity<Object> deleteConnection(@RequestParam Long id) {
		
		UserEntity currUser = secService.currentUser();
		UserEntity connUser = userRepo.findById(id).orElse(null);
		if (connUser == null) {
			return new ResponseEntity<>("Not existing user with such id", HttpStatus.NOT_FOUND);
		}
		connRepo.deleteByUserAndConnectedInversible(currUser, connUser);
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	//gets a user list with the connected or the pending to connect users of the active user
	//parameter specified which of the 2
	@GetMapping("/connections")
	public ResponseEntity<Object> getConnection(@RequestParam(defaultValue = "connected") String type) {
		
		UserEntity currUser = secService.currentUser();
		UserListOutputModel output = new UserListOutputModel();
		List<ConnectionEntity> requestedConnections;
		if (type.equals("connected")) {
			requestedConnections = connRepo.findByUserInversibleAndIsPending(currUser, false);
		}
		else if (type.equals("pending")) {
			requestedConnections = connRepo.findByConnectedAndIsPending(currUser, true);
		}
		else if (type.equals("sent")) {
			requestedConnections = connRepo.findByUserAndIsPending(currUser, true);
		}
		else {
			return new ResponseEntity<>("Parameter must be connected, pending or sent", HttpStatus.BAD_REQUEST);
		}
		try {
			for (ConnectionEntity c : requestedConnections) {
				UserEntity u;
				if (c.getUser().equals(currUser)) {
					u = c.getConnected();
				}
				else {
					u = c.getUser();
				}
				output.addUser(userEntityService.getUserOutputModelFromUser(u));
			}
		} catch (IOException e) {
			return new ResponseEntity<>("Could not load profile pictures", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(output, HttpStatus.OK);
		
	}
	
}
