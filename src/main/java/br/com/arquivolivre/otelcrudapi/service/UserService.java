package br.com.arquivolivre.otelcrudapi.service;

import br.com.arquivolivre.otelcrudapi.model.User;
import br.com.arquivolivre.otelcrudapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();
        logger.info("Retrieved {} users", users.size());
        return users;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.info("User found: {}", user.get().getEmail());
        } else {
            logger.warn("User not found with id: {}", id);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        logger.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        logger.info("Creating new user: {}", user.getEmail());
        
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("User with email {} already exists", user.getEmail());
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    public User updateUser(Long id, User userDetails) {
        logger.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        // Check if email is being changed and if the new email already exists
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            logger.warn("Email {} already exists", userDetails.getEmail());
            throw new IllegalArgumentException("Email " + userDetails.getEmail() + " already exists");
        }
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setBio(userDetails.getBio());
        
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", updatedUser.getEmail());
        return updatedUser;
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        
        if (!userRepository.existsById(id)) {
            logger.warn("User not found with id: {}", id);
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
        logger.info("User deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        logger.info("Searching users by name: {}", name);
        List<User> users = userRepository.findByNameContainingIgnoreCase(name);
        logger.info("Found {} users matching name: {}", users.size(), name);
        return users;
    }

    @Transactional(readOnly = true)
    public List<User> getRecentUsers(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        logger.info("Fetching users created after: {}", cutoffDate);
        List<User> users = userRepository.findUsersCreatedAfter(cutoffDate);
        logger.info("Found {} recent users", users.size());
        return users;
    }
} 