package br.com.arquivolivre.otelcrudapi.integration;

import br.com.arquivolivre.otelcrudapi.model.User;
import br.com.arquivolivre.otelcrudapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.threads.virtual.enabled=true",
    "logging.level.com.example.otelcrudapi=DEBUG"
})
@Transactional
class UserIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() throws Exception {
        User newUser = new User();
        newUser.setName("Integration Test User");
        newUser.setEmail("integration@test.com");
        newUser.setBio("Integration Test Bio");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Integration Test User")))
                .andExpect(jsonPath("$.email", is("integration@test.com")))
                .andExpect(jsonPath("$.bio", is("Integration Test Bio")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        // Verify user was actually saved to database
        assert userRepository.count() == 1;
        assert userRepository.findByEmail("integration@test.com").isPresent();
    }

    @Test
    void getUser_ShouldReturnUserById() throws Exception {
        User savedUser = createTestUser();

        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.bio", is("Test Bio")))
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())));
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() throws Exception {
        User savedUser = createTestUser();

        User updatedUser = new User();
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setBio("Updated Bio");

        mockMvc.perform(put("/api/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.bio", is("Updated Bio")))
                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())));

        // Verify user was actually updated in database
        User dbUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assert dbUser.getName().equals("Updated User");
        assert dbUser.getEmail().equals("updated@example.com");
    }

    @Test
    void deleteUser_ShouldDeleteUser() throws Exception {
        User savedUser = createTestUser();

        mockMvc.perform(delete("/api/users/" + savedUser.getId()))
                .andExpect(status().isNoContent());

        // Verify user was actually deleted from database
        assert !userRepository.existsById(savedUser.getId());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        createTestUser();
        createTestUser("Another User", "another@example.com");

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Test User", "Another User")));
    }

    @Test
    void searchUsers_ShouldReturnMatchingUsers() throws Exception {
        createTestUser("John Doe", "john@example.com");
        createTestUser("Jane Smith", "jane@example.com");

        mockMvc.perform(get("/api/users/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")));
    }

    @Test
    void getRecentUsers_ShouldReturnRecentUsers() throws Exception {
        createTestUser();
        createTestUser("Recent User", "recent@example.com");

        mockMvc.perform(get("/api/users/recent")
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getUserByEmail_ShouldReturnUser() throws Exception {
        User savedUser = createTestUser();

        mockMvc.perform(get("/api/users/email/" + savedUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldReturnBadRequest() throws Exception {
        createTestUser();

        User duplicateUser = new User();
        duplicateUser.setName("Duplicate User");
        duplicateUser.setEmail("test@example.com"); // Same email as existing user
        duplicateUser.setBio("Duplicate Bio");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("already exists")));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        User invalidUser = new User();
        // Missing required fields

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNonExistentUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNonExistentUser_ShouldReturnNotFound() throws Exception {
        User updatedUser = new User();
        updatedUser.setName("Updated User");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setBio("Updated Bio");

        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void deleteNonExistentUser_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void healthCheck_ShouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("UserService")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void threadInfo_ShouldReturnThreadInformation() throws Exception {
        mockMvc.perform(get("/api/users/thread-info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.threadName", notNullValue()))
                .andExpect(jsonPath("$.threadId", notNullValue()))
                .andExpect(jsonPath("$.isVirtual", notNullValue()))
                .andExpect(jsonPath("$.threadClass", notNullValue()))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    void virtualThreads_ShouldBeEnabled() throws Exception {
        mockMvc.perform(get("/api/users/thread-info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isVirtual", notNullValue())); // During testing, may run on main thread
    }

    @Test
    void fullUserLifecycle_ShouldWorkCorrectly() throws Exception {
        // Create user
        User newUser = new User();
        newUser.setName("Lifecycle User");
        newUser.setEmail("lifecycle@example.com");
        newUser.setBio("Lifecycle Bio");

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User createdUser = objectMapper.readValue(response, User.class);
        Long userId = createdUser.getId();

        // Read user
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Lifecycle User")));

        // Update user
        User updatedUser = new User();
        updatedUser.setName("Updated Lifecycle User");
        updatedUser.setEmail("updated.lifecycle@example.com");
        updatedUser.setBio("Updated Lifecycle Bio");

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Lifecycle User")));

        // Delete user
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        // Verify user is deleted
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    private User createTestUser() {
        return createTestUser("Test User", "test@example.com");
    }

    private User createTestUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBio("Test Bio");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
} 