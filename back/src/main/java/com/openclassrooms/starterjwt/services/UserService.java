package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.exception.UnauthorizedException;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.starterjwt.payload.request.SignupRequest;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User findByIdOrThrow(Long id) {
        User user = this.findById(id);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }

    public User findByEmailOrThrow(String email) {
        User user = this.findByEmail(email);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    public Boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User register(SignupRequest signUpRequest) {
        if (this.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException();
        }

        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getLastName(),
                signUpRequest.getFirstName(),
                this.passwordEncoder.encode(signUpRequest.getPassword()),
                false);

        return this.userRepository.save(user);
    }

    public void delete(Long id, String email) {
        User user = this.findByIdOrThrow(id);
        if (!user.getEmail().equals(email)) {
            throw new UnauthorizedException();
        }

        this.userRepository.deleteById(id);
    }
}
