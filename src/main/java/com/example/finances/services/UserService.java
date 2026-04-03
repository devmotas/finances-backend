package com.example.finances.services;

import com.example.finances.domains.user.User;
import com.example.finances.domains.user.UserCreateDTO;
import com.example.finances.domains.user.UserDTO;
import com.example.finances.exceptions.UserAlreadyExistsException;
import com.example.finances.exceptions.UserIdDoNotExistsException;
import com.example.finances.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDTO findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserIdDoNotExistsException("Usuário não encontrado"));

        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.findByEmail(userCreateDTO.email()).isPresent()) {
            throw new UserAlreadyExistsException("O e-mail " + userCreateDTO.email() + " já está em uso.");
        }

        User user = new User(
                userCreateDTO.name(),
                userCreateDTO.email()
        );

        User savedUser = userRepository.save(user);

        return findById(savedUser.getId());

//        TODO salvar senha em userCreateDTO.password();
    }
}