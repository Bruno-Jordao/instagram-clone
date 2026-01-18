package br.edu.ifpb.instagram.controller.impl;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import br.edu.ifpb.instagram.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    // Teste Bruno
    @Test
    void testGetUsers_ReturnsListOfUsers() throws Exception {

        UserDto user1 = new UserDto(
                1L, "User One", "user1", "user1@email.com", null, null);
        UserDto user2 = new UserDto(
                2L, "User Two", "user2", "user2@email.com", null, null);

        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName").value("User One"))
                .andExpect(jsonPath("$[1].fullName").value("User Two"));

        verify(userService, times(1)).findAll();
    }

    // Teste Bruno
    @Test
    void testGetUserById_ReturnsUser() throws Exception {

        Long userId = 1L;

        UserDto userDto = new UserDto(
                userId, "Paulo Pereira", "paulo", "paulo@email.com", null, null);

        when(userService.findById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.fullName").value("Paulo Pereira"))
                .andExpect(jsonPath("$.email").value("paulo@email.com"));

        verify(userService, times(1)).findById(userId);
    }
}
