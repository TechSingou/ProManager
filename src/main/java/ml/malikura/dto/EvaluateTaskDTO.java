package ml.malikura.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluateTaskDTO {
    @NotNull(message = "Value must not be null")
    @Range(min = 0, max = 10, message = "La doit être entre 0 et 10")
    private double taskEvaluation;
    private String mark;
}
