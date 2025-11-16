package park_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import park_api.entity.User;
import park_api.exception.EntityNotFoundException;
import park_api.exception.UsernameUniqueViolationException;
import park_api.exception.PasswordInvalidException;
import park_api.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User create(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UsernameUniqueViolationException(String.format("Username {%s} já cadastrado", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário id=%s não encontrado", id)));
    }

    @Transactional()
    public User updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("Nova senha não confere com a senha confirmada");
        }

        User user = getById(id);
        if (!user.getPassword().equals(currentPassword)) {
            throw new PasswordInvalidException("Sua senha não confere");
        }
        
        user.setPassword(newPassword);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
