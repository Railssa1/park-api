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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import park_api.entity.User;
import park_api.service.UserService;
import park_api.web.dto.UserCreateDto;
import park_api.web.dto.UserPasswordDto;
import park_api.web.dto.UserResponseDto;
import park_api.web.dto.mapper.UserMapper;
import park_api.web.exception.ErrorMessage;

@Tag(name = "users", description = "Contém todas as operações relativas aos recursos para cadastro, edição e leitura de um usuário")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Criar um novo usuário",
        description = "Recurso para criar um novo usuário no sistema",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Recurso criado com sucesso",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Email já cadastrado no sistema",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)
                )
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Recurso não processado por dados de entrada inválidos",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)
                )
            )
        }
    )
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto userCreateDto) {
        User userCreated = userService.create(UserMapper.toUser(userCreateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(userCreated));
    }

    @Operation(
        summary = "Recuperar um usuário pelo ID",
        description = "Recurso para recuperar um usuário específico pelo seu identificador",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Recurso recuperado com sucesso",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Recurso não encontrado",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)
                )
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @Operation(
        summary = "Atualizar a senha de um usuário",
        description = "Recurso para atualizar a senha de um usuário existente",
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Senha atualizada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Recurso não encontrado",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Senha não confere",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class)
                )
            )
        }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id,
            @Valid @RequestBody UserPasswordDto userPasswordDto) {
        userService.updatePassword(
                id, userPasswordDto.getCurrentPassword(),
                userPasswordDto.getNewPassword(),
                userPasswordDto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Listar todos os usuários",
        description = "Recurso para listar todos os usuários cadastrados no sistema",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuários listados com sucesso",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class))
                )
            )
        }
    )
    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(UserMapper.toListDto(users));
    }
}
