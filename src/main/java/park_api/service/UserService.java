package park_api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import park_api.entity.User;
import park_api.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional()
    public User updatePassword(Long id, String newPassword) {
        User user = getById(id);
        user.setPassword(newPassword);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
