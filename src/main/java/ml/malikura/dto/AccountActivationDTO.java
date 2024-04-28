package ml.malikura.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountActivationDTO {
    @NotEmpty(message = "Au moins un rôle doit être choisit") String[] roles;
}
