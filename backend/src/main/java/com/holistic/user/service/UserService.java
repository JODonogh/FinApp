package com.holistic.user.service;

import com.holistic.user.dto.UserDto;
import com.holistic.user.exception.UserNotFoundException;
import com.holistic.user.model.User;
import com.holistic.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user){
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDto.name());
            user.setEmail(userDto.email());

            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id " + id);
        }
        userRepository.deleteById(id);
    }
}