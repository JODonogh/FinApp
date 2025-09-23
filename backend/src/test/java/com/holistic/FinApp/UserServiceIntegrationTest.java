package com.holistic.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.holistic.user.controller.UserController;
import com.holistic.user.dto.UserDto;
import com.holistic.user.model.User;
import com.holistic.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void testCreateUserEndpoint() throws Exception {
        UserDto newUserDto = new UserDto(null, "John Doe", "john.doe@example.com", "securepassword123");
        String jsonRequest = objectMapper.writeValueAsString(newUserDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com"));

        assertThat(userRepository.findByEmail("john.doe@example.com")).isPresent();
    }
    
    // Test to ensure validation works by sending an invalid request
@Test
void testCreateUserValidationFails() throws Exception {
    UserDto invalidUserDto = new UserDto(null, "", "invalid-email", "pass");
    String jsonRequest = objectMapper.writeValueAsString(invalidUserDto);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
        .andExpect(status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Name is mandatory"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("Email should be valid"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("Password must be at least 8 characters long"));
}

 @Test
        void testUpdateUserEndpoint() throws Exception{
        //Given a user exists
        User existingUser = new User(12122L, "John Doe", "john.doe@example.com", "password");
        existingUser = userRepository.save(existingUser);

        // When: A PUT request is made to update the user
        UserDto updateUserDto = new UserDto(existingUser.getId(),  "Updated Name", "updated@example.com", "newpassword");
        String jsonRequest = objectMapper.writeValueAsString(updateUserDto);

        mockMvc.perform(put("/api/users/{id}", existingUser.getId())
               .contentType(MediaType.APPLICATION_JSON)
               .content(jsonRequest)) 
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Updated Name"))
               .andExpect(jsonPath("$.email").value("updated@example.com"));
         
        // Then: The user is updated in the database
        User updatedUser = userRepository.findById(existingUser.getId()).get();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");       
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");       
    }

        @Test
        void testDeleteUser(){
            // Given: A user exists
            User userToDelete = new User(null, "Delete Me", "delete.me@example.com", "password");
            userToDelete = userRepository.save(userToDelete);

            mockMvc.perform(delete("/api/users/{id}", userToDelete.getId()))
                    .andExpect(status().isNoContent());

            assertThat(userRepository.findById(userToDelete.getId())).isNotPresent();
        }
    
    @Test
    void testGetUserByIdEndpoint() throws Exception {
        User user = new User(null, "Read Me", "read.me@example.com", "password");
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Read Me"));
    }

    // Test for duplicate name
    @Test
    void testCreateUserWithDuplicateNameFails() throws Exception {
        // First, create a user successfully
        User existingUser = new User(null, "Unique Name", "unique@example.com", "password123");
        userRepository.save(existingUser);

        // Now, try to create another user with the same name
        UserDto duplicateNameUserDto = new UserDto(null, "Unique Name", "another@example.com", "securepassword");
        String jsonRequest = objectMapper.writeValueAsString(duplicateNameUserDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(content().string("A resource with the given unique identifier already exists."));
        }        
        
       
       @Test
        void testCreateUserWithDuplicateEmailFails() throws Exception {
            // Given: A user exists with a specific email
            User existingUser = new User(null, "Existing User", "test@example.com", "password123");
            userRepository.save(existingUser);

            // When: We try to create a new user with the same email
            UserDto duplicateEmailUserDto = new UserDto(null, "Another User", "test@example.com", "securepassword");
            String jsonRequest = objectMapper.writeValueAsString(duplicateEmailUserDto);

            // Then: The API should return a 409 Conflict
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                    .andExpect(status().isConflict())
                    .andExpect(content().string("A resource with the given unique identifier already exists."));
        }

        @Test
        void testGetUserNotFound() throws Exception {
            // Given: A user ID that does not exist in the database
            Long nonExistentId = 9999L;

            // When: We try to get the non-existent user
            mockMvc.perform(get("/api/users/{id}", nonExistentId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("User not found with ID: " + nonExistentId));
        }   
}