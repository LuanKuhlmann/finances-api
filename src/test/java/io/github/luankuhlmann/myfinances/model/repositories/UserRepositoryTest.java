package io.github.luankuhlmann.myfinances.model.repositories;

import io.github.luankuhlmann.myfinances.model.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //Do not allow that spring reconfigure application-test.properties
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

    public static User createUser() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@mail.com");
        user.setPassword("12345");

        return user;
    }

    @Test
    public void shouldVerifyTheExistenceOfAEmail() {
        User user = createUser();

        entityManager.persist(user);

        userRepository.save(user);

        boolean result = userRepository.existsByEmail("user@mail.com");

        Assertions.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenUserNotRegisteredWithEmail() {
        boolean result = userRepository.existsByEmail("user@mail.com");

        Assertions.assertFalse(result);
    }

    @Test
    public void shouldSaveAUserOnDatabase() {
        User user = createUser();

        User savedUser =  userRepository.save(user);

        Assertions.assertNotNull(savedUser);
    }

    @Test
    public void shouldFindUserByEmail() {
        User user = createUser();
        entityManager.persist(user);

        Optional<User> result = userRepository.findByEmail("user@mail.com");

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void shouldReturnNullWhenFindUserByEmailDoesNotFoundAUser() {
        User user = createUser();
        entityManager.persist(user);

        Optional<User> result = userRepository.findByEmail("user2@mail.com");

        Assertions.assertTrue(result.isEmpty());
    }

}