package park_api.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import park_api.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
}
