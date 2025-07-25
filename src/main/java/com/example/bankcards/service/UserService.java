package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserCreateDTO;
import com.example.bankcards.dto.user.UserDTO;
import com.example.bankcards.dto.user.UserRolesUpdateDTO;
import com.example.bankcards.dto.user.UserUpdateDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ErrorCode;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.UserOperationException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        return userMapper.map(user);
    }

    public List<UserDTO> getAll() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO create(UserCreateDTO userData) {
        User user = userMapper.map(userData);
        try {
            userRepository.save(user);
            return userMapper.map(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserOperationException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    public UserDTO update(Long userId, UserUpdateDTO userData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        userMapper.update(userData, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO updateUserRoles(Long userId, UserRolesUpdateDTO userData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateRoles(userData, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void deleteById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }
}
