package br.com.arquivolivre.otelcrudapi.service;

import br.com.arquivolivre.otelcrudapi.model.User;
import br.com.arquivolivre.otelcrudapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setBio("Software Engineer");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setBio("Product Manager");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        testUsers = Arrays.asList(testUser, user2);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(testUsers);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testUser, testUsers.get(1));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("John Doe");
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldReturnEmpty() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getUserByEmail_WithValidEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("john.doe@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.get().getName()).isEqualTo("John Doe");
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void getUserByEmail_WithInvalidEmail_ShouldReturnEmpty() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("nonexistent@example.com");

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void createUser_WithValidUser_ShouldReturnSavedUser() {
        User newUser = new User();
        newUser.setName("Alice Johnson");
        newUser.setEmail("alice.johnson@example.com");
        newUser.setBio("DevOps Engineer");

        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setName("Alice Johnson");
        savedUser.setEmail("alice.johnson@example.com");
        savedUser.setBio("DevOps Engineer");
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail("alice.johnson@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(newUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("Alice Johnson");
        assertThat(result.getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(result.getBio()).isEqualTo("DevOps Engineer");
        verify(userRepository, times(1)).existsByEmail("alice.johnson@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        User newUser = new User();
        newUser.setName("Alice Johnson");
        newUser.setEmail("john.doe@example.com"); // Email already exists
        newUser.setBio("DevOps Engineer");

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User with email john.doe@example.com already exists");

        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        User updatedData = new User();
        updatedData.setName("John Updated");
        updatedData.setEmail("john.updated@example.com");
        updatedData.setBio("Senior Software Engineer");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setBio("Senior Software Engineer");
        updatedUser.setCreatedAt(testUser.getCreatedAt());
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedData);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Updated");
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(result.getBio()).isEqualTo("Senior Software Engineer");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("john.updated@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowException() {
        User updatedData = new User();
        updatedData.setName("John Updated");
        updatedData.setEmail("john.updated@example.com");
        updatedData.setBio("Senior Software Engineer");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, updatedData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with id: 999");

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithExistingEmail_ShouldThrowException() {
        User updatedData = new User();
        updatedData.setName("John Updated");
        updatedData.setEmail("jane.smith@example.com"); // Email already exists for another user
        updatedData.setBio("Senior Software Engineer");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, updatedData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email jane.smith@example.com already exists");

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("jane.smith@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with id: 999");

        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(999L);
    }

    @Test
    void searchUsersByName_WithValidName_ShouldReturnMatchingUsers() {
        when(userRepository.findByNameContainingIgnoreCase("John")).thenReturn(Arrays.asList(testUser));

        List<User> result = userService.searchUsersByName("John");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        verify(userRepository, times(1)).findByNameContainingIgnoreCase("John");
    }

    @Test
    void searchUsersByName_WithNoMatches_ShouldReturnEmptyList() {
        when(userRepository.findByNameContainingIgnoreCase("Nonexistent")).thenReturn(Arrays.asList());

        List<User> result = userService.searchUsersByName("Nonexistent");

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByNameContainingIgnoreCase("Nonexistent");
    }

    @Test
    void getRecentUsers_ShouldReturnUsersFromSpecifiedDays() {
        when(userRepository.findUsersCreatedAfter(any(LocalDateTime.class))).thenReturn(testUsers);

        List<User> result = userService.getRecentUsers(7);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testUser, testUsers.get(1));
        verify(userRepository, times(1)).findUsersCreatedAfter(any(LocalDateTime.class));
    }

    @Test
    void getRecentUsers_WithZeroDays_ShouldReturnAllUsers() {
        when(userRepository.findUsersCreatedAfter(any(LocalDateTime.class))).thenReturn(testUsers);

        List<User> result = userService.getRecentUsers(0);

        assertThat(result).hasSize(2);
        verify(userRepository, times(1)).findUsersCreatedAfter(any(LocalDateTime.class));
    }

    @Test
    void getRecentUsers_WithNegativeDays_ShouldHandleGracefully() {
        when(userRepository.findUsersCreatedAfter(any(LocalDateTime.class))).thenReturn(testUsers);

        List<User> result = userService.getRecentUsers(-1);

        assertThat(result).hasSize(2);
        verify(userRepository, times(1)).findUsersCreatedAfter(any(LocalDateTime.class));
    }
} 