package park_api.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import park_api.entity.User;
import park_api.service.UserService;
import park_api.web.dto.UserCreateDto;
import park_api.web.dto.UserPasswordDto;
import park_api.web.dto.UserResponseDto;
import park_api.web.dto.mapper.UserMapper;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto userCreateDto) {
        User userCreated = userService.create(UserMapper.toUser(userCreateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(userCreated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDto userPasswordDto) {
        userService.updatePassword(
                id, userPasswordDto.getCurrentPassword(),
                userPasswordDto.getNewPassword(),
                userPasswordDto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }
}
