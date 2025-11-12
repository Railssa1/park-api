package park_api.web.dto.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordDto {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
