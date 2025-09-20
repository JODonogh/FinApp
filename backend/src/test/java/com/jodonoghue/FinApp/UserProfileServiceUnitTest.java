package com.jodonoghue.FinApp;

import com.holistic.user.model.User;
import com.holistic.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import com.holistic.user.service.UserService;
import com.holistic.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void testFindUserById_WhenUserExists() {
        // Given a mock user and that the repository will find it
        User mockUser = new User("John Doe", "john.doe@example.com", "password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // When the service method is called
        Optional<User> result = userProfileService.findUserById(1L);

        // Then verify the result is correct and the repository was called
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John Doe");
        verify(userRepository).findById(1L);
    }
    
    @Test
    void testFindUserById_WhenUserDoesNotExist() {
        // Given that the repository will not find any user
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When the service method is called
        Optional<User> result = userProfileService.findUserById(99L);

        // Then verify the result is empty and the repository was called
        assertThat(result).isEmpty();
        verify(userRepository).findById(99L);
    }

    @Test
    void testCreateUser() {
        // Given a new user
        User newUser = new User("Jane Doe", "jane.doe@example.com", "password");
        // And we mock the save() method to return the saved user
        when(userRepository.save(newUser)).thenReturn(newUser);

        // When the service method is called
        User result = userProfileService.createUser(newUser);

        // Then we verify the user was saved and the result is correct
        assertThat(result.getName()).isEqualTo("Jane Doe");
        verify(userRepository).save(newUser);
    }
}