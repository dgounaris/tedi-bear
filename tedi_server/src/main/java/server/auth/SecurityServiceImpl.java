package server.auth;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import server.entities.UserEntity;
import server.repositories.UserRepository;

@Service
public class SecurityServiceImpl implements SecurityService {

	private final Logger mylogger = Logger.getLogger(SecurityServiceImpl.class.getName());
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserEntity currentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		try {
			UserEntity authUser =  userRepo.findByEmail((String)auth.getPrincipal());
			return authUser;
		} catch (Exception e) {
			mylogger.log(Level.INFO, "Exception", e);
			return null;
		}
	}
	
}
