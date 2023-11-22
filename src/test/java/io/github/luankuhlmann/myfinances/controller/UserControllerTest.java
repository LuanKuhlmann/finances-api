package io.github.luankuhlmann.myfinances.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.luankuhlmann.myfinances.dto.UserDTO;
import io.github.luankuhlmann.myfinances.exception.AuthenticationError;
import io.github.luankuhlmann.myfinances.exception.BusinessRuleException;
import io.github.luankuhlmann.myfinances.model.entities.User;
import io.github.luankuhlmann.myfinances.service.EntriesService;
import io.github.luankuhlmann.myfinances.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    static final String API = "/api/users";
    static final MediaType JSON = MediaType.APPLICATION_JSON;
    public static final String EMAIL = "user@mail.com";
    public static final String PASSWORD = "123";

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    EntriesService entriesService;

    @Test
    public void shallAnthenticateAUser() throws Exception {
        UserDTO userDTO = UserDTO.builder().email(EMAIL).password(PASSWORD).build();
        User user = User.builder().id(1l).email(EMAIL).password(PASSWORD).build();

        when(userService.authentication(EMAIL, PASSWORD)).thenReturn(user);

        String json = new ObjectMapper().writeValueAsString(userDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/authenticate"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()));
    }

    @Test
    public void shallReturnABadRequestWhenGettingAAuthenticationError() throws Exception {
        UserDTO userDTO = UserDTO.builder().email(EMAIL).password(PASSWORD).build();

        when(userService.authentication(EMAIL, PASSWORD)).thenThrow(AuthenticationError.class);

        String json = new ObjectMapper().writeValueAsString(userDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/authenticate"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void shallCreateANewUser() throws Exception {
        UserDTO userDTO = UserDTO.builder().email(EMAIL).password(PASSWORD).build();
        User user = User.builder().id(1l).email(EMAIL).password(PASSWORD).build();

        when(userService.saveUser(any(User.class))).thenReturn(user);

        String json = new ObjectMapper().writeValueAsString(userDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()));
    }

    @Test
    public void shallReturnBadRequestWhenTryingToCreateAInvalidUser() throws Exception {
        UserDTO userDTO = UserDTO.builder().email(EMAIL).password(PASSWORD).build();

        when(userService.saveUser(any(User.class))).thenThrow(BusinessRuleException.class);

        String json = new ObjectMapper().writeValueAsString(userDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}