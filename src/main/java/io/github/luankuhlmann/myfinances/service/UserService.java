package io.github.luankuhlmann.myfinances.service;

import io.github.luankuhlmann.myfinances.model.entities.User;

public interface UserService {

    User authentication(String email, String password);

    User saveUser(User user);

    void validateEmail(String email);
}
