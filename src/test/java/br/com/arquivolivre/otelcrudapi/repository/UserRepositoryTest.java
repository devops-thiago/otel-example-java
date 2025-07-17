package br.com.arquivolivre.otelcrudapi.repository;

import br.com.arquivolivre.otelcrudapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "/test-data-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User johnDoe;
    private User janeSmith;

    @BeforeEach
    void setUp() {
        // Insert test data with specific timestamps using native SQL
        // This bypasses the @CreationTimestamp and @UpdateTimestamp annotations
        
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        
        // Insert John Doe (older user)
        entityManager.getEntityManager().createNativeQuery(
            "INSERT INTO users (name, email, bio, created_at, updated_at) VALUES (?, ?, ?, ?, ?)"
        ).setParameter(1, "John Doe")
         .setParameter(2, "john.doe@example.com")
         .setParameter(3, "Software Engineer")
         .setParameter(4, fiveDaysAgo)
         .setParameter(5, fiveDaysAgo)
         .executeUpdate();
        
        // Insert Jane Smith (more recent user)
        entityManager.getEntityManager().createNativeQuery(
            "INSERT INTO users (name, email, bio, created_at, updated_at) VALUES (?, ?, ?, ?, ?)"
        ).setParameter(1, "Jane Smith")
         .setParameter(2, "jane.smith@example.com")
         .setParameter(3, "DevOps Engineer")
         .setParameter(4, twoDaysAgo)
         .setParameter(5, twoDaysAgo)
         .executeUpdate();
        
        entityManager.flush();
        
        // Retrieve the entities for use in tests
        johnDoe = userRepository.findByEmail("john.doe@example.com").orElseThrow();
        janeSmith = userRepository.findByEmail("jane.smith@example.com").orElseThrow();
    }

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnUser() {
        Optional<User> result = userRepository.findByEmail("john.doe@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John Doe");
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        boolean result = userRepository.existsByEmail("john.doe@example.com");

        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        boolean result = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(result).isFalse();
    }

    // Note: existsByEmailAndIdNot method would be useful but doesn't exist in the repository
    // It would be needed for proper unique email validation during updates

    @Test
    void findByNameContainingIgnoreCase_WithExistingName_ShouldReturnMatchingUsers() {
        List<User> result = userRepository.findByNameContainingIgnoreCase("john");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void findByNameContainingIgnoreCase_WithPartialName_ShouldReturnMatchingUsers() {
        List<User> result = userRepository.findByNameContainingIgnoreCase("doe");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void findByNameContainingIgnoreCase_WithDifferentCase_ShouldReturnMatchingUsers() {
        List<User> result = userRepository.findByNameContainingIgnoreCase("JOHN");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void findByNameContainingIgnoreCase_WithNonExistingName_ShouldReturnEmptyList() {
        List<User> result = userRepository.findByNameContainingIgnoreCase("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findUsersCreatedAfter_WithValidDate_ShouldReturnRecentUsers() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        
        List<User> result = userRepository.findUsersCreatedAfter(threeDaysAgo);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void findUsersCreatedAfter_WithVeryOldDate_ShouldReturnAllUsers() {
        LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);
        
        List<User> result = userRepository.findUsersCreatedAfter(tenDaysAgo);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    @Test
    void findUsersCreatedAfter_WithFutureDate_ShouldReturnEmptyList() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        
        List<User> result = userRepository.findUsersCreatedAfter(futureDate);

        assertThat(result).isEmpty();
    }

    @Test
    void saveUser_ShouldPersistUser() {
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("test@example.com");
        newUser.setBio("Test Bio");
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getBio()).isEqualTo("Test Bio");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void saveUser_ShouldUpdateExistingUser() {
        johnDoe.setName("John Updated");
        johnDoe.setBio("Updated Bio");
        johnDoe.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(johnDoe);

        assertThat(updatedUser.getName()).isEqualTo("John Updated");
        assertThat(updatedUser.getBio()).isEqualTo("Updated Bio");
        assertThat(updatedUser.getId()).isEqualTo(johnDoe.getId());
    }

    @Test
    void deleteUser_ShouldRemoveUser() {
        Long userId = johnDoe.getId();
        
        userRepository.delete(johnDoe);
        
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
        
        // Verify that only one user remains
        List<User> remainingUsers = userRepository.findAll();
        assertThat(remainingUsers).hasSize(1);
        assertThat(remainingUsers.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        Long userId = johnDoe.getId();
        
        userRepository.deleteById(userId);
        
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
        
        // Verify that only one user remains
        List<User> remainingUsers = userRepository.findAll();
        assertThat(remainingUsers).hasSize(1);
        assertThat(remainingUsers.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void deleteAll_ShouldRemoveAllUsers() {
        userRepository.deleteAll();
        
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
        
        long count = userRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    @Test
    void findById_WithExistingId_ShouldReturnUser() {
        Optional<User> result = userRepository.findById(johnDoe.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John Doe");
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        Optional<User> result = userRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        boolean result = userRepository.existsById(johnDoe.getId());

        assertThat(result).isTrue();
    }

    @Test
    void existsById_WithNonExistingId_ShouldReturnFalse() {
        boolean result = userRepository.existsById(999L);

        assertThat(result).isFalse();
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        long count = userRepository.count();

        assertThat(count).isEqualTo(2);
    }
} 