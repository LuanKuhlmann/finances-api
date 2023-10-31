package io.github.luankuhlmann.myfinances.service.impl;

import io.github.luankuhlmann.myfinances.exception.AuthenticationError;
import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.User;
import io.github.luankuhlmann.myfinances.model.repositories.UserRepository;
import io.github.luankuhlmann.myfinances.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User authentication(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            throw new AuthenticationError("User email not found");
        }

        if(!user.get().getPassword().equals(password)) {
            throw new AuthenticationError("Invalid Password");
        }

        return user.get();
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        validateEmail(user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void validateEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if(exists) {
            throw new BusinessRuleException("Email already exists.");
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
