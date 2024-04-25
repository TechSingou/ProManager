package ml.malikura.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ml.malikura.dto.validation.ValueOfEnum;
import ml.malikura.util.ProjectStatut;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProjectDTO {

    @NotNull
    private Long id;

    @NotBlank(message = "Champ obligatoire*")
    private String title;

    @NotBlank(message = "Champ obligatoire*")
    @Size(min = 15, max = 150, message = "La description doit être entre 15 et 150 caractères")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @ValueOfEnum(enumClass = ProjectStatut.class)
    private String statut;

    private String specificNote;

}
