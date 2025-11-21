package park_api;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.reactive.server.WebTestClient;

import park_api.web.dto.UserCreateDto;
import park_api.web.dto.UserPasswordDto;
import park_api.web.dto.UserResponseDto;
import park_api.web.exception.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/users-insert.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/users-delete.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUser_WithUsernameAndPasswordValid_ReturnUserCreatedWithStatus201() {
        UserResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("raissa@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("raissa@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CUSTOMER");
    }

    @Test
    public void createUser_WithUsernameInvalid_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("raissa", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUser_WithPasswordInvalid_ReturnErrorMessageWithStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("raissa@gmail.com", "1234567"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUser_WithDuplicateUsername_ReturnErrorMessageWithStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("admin@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void getUser_WithIdValid_ReturnUserWithStatus200() {
        UserResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/users/100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("admin@email.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");
    }

    @Test
    public void getUser_WithIdInvalid_ReturnErrorMessageWithStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/users/1000")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void updatePassword_WithPasswordValid_ReturnStatus204() {
        testClient
                .patch()
                .uri("/api/v1/users/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123456", "101010", "101010"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void updatePassword_WithIdInvalid_ReturnStatus404() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/0")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123456", "101010", "101010"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void updatePassword_WithPasswordInvalid_ReturnStatus422() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updatePassword_WithPasswordInvalid_ReturnStatus400() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/users/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserPasswordDto("123458", "098712", "098712"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void getAll_ReturnAllUsersWithStatus200() {
        List<UserResponseDto> responseBody = testClient
            .get()
            .uri("/api/v1/users")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserResponseDto.class).hasSize(2)
                .returnResult().getResponseBody();
            
        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.size()).isEqualTo(2);
    }
}