package br.edu.ifpb.instagram.service.impl;

import br.edu.ifpb.instagram.model.dto.UserDto;
import br.edu.ifpb.instagram.model.entity.UserEntity;
import br.edu.ifpb.instagram.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testFindById_ReturnsUserDto() {
        // Configurar o comportamento do mock
        Long userId = 1L;

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(userId);
        mockUserEntity.setFullName("Paulo Pereira");
        mockUserEntity.setEmail("paulo@ppereira.dev");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUserEntity));

        // Executar o método a ser testado
        UserDto userDto = userService.findById(userId);

        // Verificar o resultado
        assertNotNull(userDto);
        assertEquals(mockUserEntity.getId(), userDto.id());
        assertEquals(mockUserEntity.getFullName(), userDto.fullName());
        assertEquals(mockUserEntity.getEmail(), userDto.email());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_ThrowsExceptionWhenUserNotFound() {
        // Configurar o comportamento do mock
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Executar e verificar a exceção
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });

        assertEquals("User not found", exception.getMessage());

        // Verificar a interação com o mock
        verify(userRepository, times(1)).findById(userId);
    }

    // Teste Bruno
    @Test
    void testFindAll_ReturnsListOfUsers() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setFullName("User One");
        user1.setEmail("user1@email.com");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setFullName("User Two");
        user2.setEmail("user2@email.com");

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserDto> users = userService.findAll();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("User One", users.get(0).fullName());
        assertEquals("User Two", users.get(1).fullName());

        verify(userRepository, times(1)).findAll();
    }

    // Teste Bruno
    @Test
    void testDeleteUser_CallsRepositoryDeleteById() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    // ------------------------------Levi------------------------------
    @Test
    void findById_WithNoExistingUserEntity_ThrowsRuntimeException() {
        var id = 1L;
        var sut = catchThrowable(() -> userService.findById(id));
        assertThat(sut)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found with id: 1");

    }

    // ------------------------------Levi------------------------------
    @Test
    void findById_WithExistingUser_ReturnsUserEntity() {
        var id = 1L;
        var userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setEmail("userEntity@mail.com");
        userEntity.setFullName("The user entity name");
        userEntity.setEncryptedPassword("userPass");
        ArgumentCaptor<Long> userId = ArgumentCaptor.forClass(Long.class);
        when(userRepository.findById(userId.capture())).thenReturn(Optional.of(userEntity));

        var sut = userService.findById(id);

        assertNotNull(sut);
        assertInstanceOf(UserDto.class, sut,"Must be instance of UserDto");
        assertEquals(userEntity.getEmail(), sut.email());
        assertEquals(id, userId.getValue());
    }

    // ------------------------------Levi------------------------------
    @Test
    void shouldCreateUserSuccessfully() {
        var id = 1L;

        var userDto = new UserDto(id,
                "The user entity name",
                "User Entity",
                "userEntity@mail.com",
                "userPass",
                passwordEncoder.encode("userPass"));

        UserEntity mockedUserEntity = new UserEntity();
        mockedUserEntity.setId(id);
        mockedUserEntity.setUsername("User Entity");
        mockedUserEntity.setFullName("The user entity name");
        mockedUserEntity.setEmail("userEntity@mail.com");
        mockedUserEntity.setEncryptedPassword(passwordEncoder.encode("userPass"));

        when(userRepository.save(any(UserEntity.class))).thenReturn(mockedUserEntity);

        UserDto createdUserDto = userService.createUser(userDto);

        assertNotNull(createdUserDto);
        assertEquals(userDto.id(), createdUserDto.id());
        assertEquals(userDto.fullName(), createdUserDto.fullName());
        assertEquals(userDto.email(), createdUserDto.email());
        assertNull(createdUserDto.password());
        assertNull(createdUserDto.encryptedPassword());
    }

    @Test //gilberto
    void shouldUpdateUserSuccessfully() {
        var id = 1L;

        var originalUserDto = new UserDto(id,
                "The user entity name",
                "User Entity",
                "userEntity@mail.com",
                "userPass",
                passwordEncoder.encode("userPass"));

        var mockedUserEntity = new UserEntity();
        mockedUserEntity.setId(id);
        mockedUserEntity.setUsername("User Entity");
        mockedUserEntity.setFullName("The user entity name");
        mockedUserEntity.setEmail("userEntity@mail.com");
        mockedUserEntity.setEncryptedPassword(passwordEncoder.encode("userPass"));

        var updatedUserDto = new UserDto(id,
                "Updated user entity name",
                "User Entity",
                "userEntity@mail.com",
                "userPass",
                passwordEncoder.encode("userPass"));

        when(userRepository.findById(id)).thenReturn(Optional.of(mockedUserEntity));

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto returnedUserDto = userService.updateUser(updatedUserDto);

        assertNotNull(returnedUserDto);
        assertEquals(updatedUserDto.id(), returnedUserDto.id());
        assertEquals(updatedUserDto.fullName(), returnedUserDto.fullName());
        assertEquals(updatedUserDto.email(), returnedUserDto.email());
        assertEquals(updatedUserDto.username(), returnedUserDto.username());
        assertNull(returnedUserDto.password());
        assertNull(returnedUserDto.encryptedPassword());
    }

    @Test //gilberto
    void shouldThrowExceptionWhenUserNotFoundDuringUpdate() {
        var id = 1L;

        var updatedUserDto = new UserDto(id,
                "Updated user entity name",
                "User Entity",
                "userEntity@mail.com",
                "userPass",
                passwordEncoder.encode("userPass"));

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(updatedUserDto));

        assertEquals("User not found with id: 1", exception.getMessage());
    }
}