package io.github.luankuhlmann.myfinances.service;

import io.github.luankuhlmann.myfinances.exception.AuthenticationError;
import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.User;
import io.github.luankuhlmann.myfinances.model.repositories.UserRepository;
import io.github.luankuhlmann.myfinances.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    public static final String EMAIL = "user@mail.com";
    public static final String PASSWORD = "12345";

//    @Autowired
//    UserServiceImpl userService;

    @SpyBean
    UserServiceImpl userService;

    @MockBean
    UserRepository userRepository;

//    @BeforeEach //Original way for creating a Mockito spy
//    public void setUp() {
//        userService = Mockito.spy(UserServiceImpl.class);
//
//        //userService = new UserServiceImpl(userRepository);
//    }

    public static User createUser() {
        User user = new User();
        user.setName("user");
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        return user;
    }

    @Test
    public void shouldSaveAUser() {
        Mockito.doNothing().when(userService).validateEmail(Mockito.anyString());
        User user = createUser();

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User registeredUser = userService.saveUser(new User());

        Assertions.assertNotNull(registeredUser);
        Assertions.assertEquals(registeredUser.getId(), user.getId());
        Assertions.assertEquals(registeredUser.getName(), user.getName());
        Assertions.assertEquals(registeredUser.getEmail(), user.getEmail());
        Assertions.assertEquals(registeredUser.getPassword(), user.getPassword());
    }

    @Test
    public void shouldNotSaveAUserIfEmailAlreadyRegistered() {
        User user = createUser();
        Mockito.doThrow(BusinessRuleException.class).when(userService).validateEmail(EMAIL);

        Assertions.assertThrows(BusinessRuleException.class, () -> userService.saveUser(user));

        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    public void shouldValidateEmail() {
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

        userService.validateEmail(EMAIL);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToValidateAAlreadyRegisteredEmail() {
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(BusinessRuleException.class, () -> {
            userService.validateEmail(EMAIL);
        });
    }

    @Test
    public void shouldSuccessfullyAuthenticateAUser() {
        User user = createUser();

        Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        User result = userService.authentication(EMAIL, PASSWORD);

        Assertions.assertNotNull(result);
    }

    @Test
    public void shouldThrowExceptionWhenErrorTryingToFindUserEmailToAuthenticate() {
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Throwable exception = Assertions.assertThrows(AuthenticationError.class, () -> {
            userService.authentication(EMAIL, PASSWORD);
        });

        Assertions.assertTrue(exception.getMessage().contains("User email not found"));
    }

    @Test
    public void shouldThrowExceptionWhenErrorTryingToAuthenticateUserPassword() {
        User user = createUser();

        Mockito.when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        Throwable exception = Assertions.assertThrows(AuthenticationError.class, () -> {
            userService.authentication(EMAIL, "54321");
        });

        Assertions.assertTrue(exception.getMessage().contains("Invalid Password"));
    }
}