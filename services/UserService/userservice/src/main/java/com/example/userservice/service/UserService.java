package com.example.userservice.service;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;



@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO findById(int id) {
        User foundedUser = userRepository.findById(id) .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not founded"));
        return UserMapper.INSTANCE.mapUserToUserDTO(foundedUser);
    }

    @Transactional
    public UserDTO createUser(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is required");
        }
        if (!StringUtils.hasText(user.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (!StringUtils.hasText(user.getSurname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Surname is required");
        }
        if (!StringUtils.hasText(user.getEmailAddress())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }


        userRepository.findByEmailAddress(user.getEmailAddress().trim())
                .ifPresent(u -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used"); });


        user.setName(user.getName().trim());
        user.setSurname(user.getSurname().trim());
        user.setEmailAddress(user.getEmailAddress().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword().trim()));

        User saved = userRepository.save(user);
        return UserMapper.INSTANCE.mapUserToUserDTO(saved);
    }

    public UserDTO login(User user) {
        if (user == null || !StringUtils.hasText(user.getEmailAddress()) || !StringUtils.hasText(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
        }

        String email = user.getEmailAddress().trim();
        String rawPassword = user.getPassword();

        User u = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String storedHash = u.getPassword();
        if (!StringUtils.hasText(storedHash) || !passwordEncoder.matches(rawPassword, storedHash)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return UserMapper.INSTANCE.mapUserToUserDTO(u);
    }

    @Transactional
    public UserDTO updateUser(int id, User user) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found"));

        if (user == null) return UserMapper.INSTANCE.mapUserToUserDTO(entity);

        if (StringUtils.hasText(user.getName())) {
            entity.setName(user.getName().trim());
        }
        if (StringUtils.hasText(user.getSurname())) {
            entity.setSurname(user.getSurname().trim());
        }
        if (StringUtils.hasText(user.getEmailAddress())) {
            String newEmail = user.getEmailAddress().trim();
            userRepository.findByEmailAddress(newEmail)
                    .filter(u -> u.getId() != entity.getId())
                    .ifPresent(u -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used"); });
            entity.setEmailAddress(newEmail);
        }
        if (StringUtils.hasText(user.getPassword())) {
            entity.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        }

        User saved = userRepository.save(entity);
        return UserMapper.INSTANCE.mapUserToUserDTO(saved);
    }

    @Transactional
    public void deleteUser(int id) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found"));
        userRepository.delete(entity);
    }
}
