package com.example.gym_management.service;

import com.example.gym_management.dto.UserRequest;
import com.example.gym_management.dto.UserResponse;
import com.example.gym_management.entity.Gym;
import com.example.gym_management.entity.User;
import com.example.gym_management.entity.UserRole;
import com.example.gym_management.mapper.UserMapper;
import com.example.gym_management.repository.GymRepository;
import com.example.gym_management.repository.UserRepository;
import com.example.gym_management.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithGym(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }

    @Transactional
    public UserResponse createUser(@Valid UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email '" + request.getEmail() + "' is already registered");
        }

        validateGymAssignment(request.getRole(), request.getGymId());

        Gym gym = null;
        if (request.getGymId() != null) {
            gym = gymRepository.findById(request.getGymId())
                    .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + request.getGymId()));
        }

        User user = userMapper.toEntity(request, gym);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        return userMapper.toResponseList(userRepository.findByRole(role));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByGym(Long gymId) {
        gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + gymId));
        return userMapper.toResponseList(userRepository.findByGymId(gymId));
    }

    @Transactional
    public UserResponse updateUser(Long id, @Valid UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username '" + request.getUsername() + "' is already taken");
        }
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email '" + request.getEmail() + "' is already registered");
        }

        validateGymAssignment(request.getRole(), request.getGymId());

        Gym gym = null;
        if (request.getGymId() != null) {
            gym = gymRepository.findById(request.getGymId())
                    .orElseThrow(() -> new IllegalArgumentException("Gym not found with id: " + request.getGymId()));
        }

        userMapper.updateEntity(request, user, gym);
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        user.setEnabled(true);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        user.setEnabled(false);
        return userMapper.toResponse(userRepository.save(user));
    }

    private void validateGymAssignment(UserRole role, Long gymId) {
        if (role == UserRole.ADMIN && gymId != null) {
            throw new IllegalArgumentException("Admin users cannot be assigned to a specific gym");
        }
        if ((role == UserRole.MANAGER || role == UserRole.EMPLOYEE) && gymId == null) {
            throw new IllegalArgumentException(role.name() + " users must be assigned to a gym");
        }
    }
}
