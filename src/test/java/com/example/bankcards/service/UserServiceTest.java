package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserCreateDTO;
import com.example.bankcards.dto.user.UserDTO;
import com.example.bankcards.dto.user.UserRolesUpdateDTO;
import com.example.bankcards.dto.user.UserUpdateDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.UserOperationException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnsUserDto() {
        Long id = 1L;
        User user = new User();
        UserDTO dto = new UserDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.map(user)).thenReturn(dto);

        UserDTO result = userService.findById(id);

        assertEquals(dto, result);
    }

    @Test
    void findShouldThrowsResourceNotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    void shouldReturnsMappedList() {
        List<User> users = List.of(new User(), new User());
        List<UserDTO> dtos = List.of(new UserDTO(), new UserDTO());

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.map(any(User.class)))
                .thenReturn(dtos.get(0), dtos.get(1));
        List<UserDTO> result = userService.getAll();

        assertEquals(dtos, result);
    }

    @Test
    void shouldCreateAndReturnsUserDto() {
        UserCreateDTO input = new UserCreateDTO();
        User user = new User();
        UserDTO dto = new UserDTO();

        when(userMapper.map(input)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.map(user)).thenReturn(dto);
        UserDTO result = userService.create(input);

        assertEquals(dto, result);
    }

    @Test
    void createShouldThrowsOperationEx() {
        UserCreateDTO input = new UserCreateDTO();
        User user = new User();

        when(userMapper.map(input)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserOperationException.class, () -> userService.create(input));
    }

    @Test
    void updateShouldAppliesChanges() {
        Long id = 1L;
        UserUpdateDTO update = new UserUpdateDTO();
        User user = new User();
        UserDTO dto = new UserDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.map(user)).thenReturn(dto);

        UserDTO result = userService.update(id, update);

        verify(userMapper).update(update, user);
        assertEquals(dto, result);
    }

    @Test
    void updateShouldThrowsNotFound() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(id, new UserUpdateDTO()));
    }

    @Test
    void updateRolesShouldAppliesChanges() {
        Long id = 1L;
        UserRolesUpdateDTO update = new UserRolesUpdateDTO();
        User user = new User();
        UserDTO dto = new UserDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.map(user)).thenReturn(dto);

        UserDTO result = userService.updateUserRoles(id, update);

        verify(userMapper).updateRoles(update, user);
        assertEquals(dto, result);
    }

    @Test
    void updateRolesShouldThrowsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserRoles(1L, new UserRolesUpdateDTO()));
    }

    @Test
    void deleteShouldDeletes() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);
        userService.deleteById(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteShouldThrowsNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(1L));
    }
}

