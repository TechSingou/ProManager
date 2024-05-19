package ml.malikura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ml.malikura.dto.validation.ValueOfEnum;
import ml.malikura.util.TaskCriticality;
import ml.malikura.util.TaskPriority;
import ml.malikura.util.TaskState;
import ml.malikura.util.TaskStatut;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditTaskDTO {
    @NotNull
    private Long id;

    @NotBlank(message = "Champ obligatoire*")
    private String title;

    private String description;

    @ValueOfEnum(enumClass = TaskStatut.class)
    private String statut;

    @ValueOfEnum(enumClass = TaskPriority.class)
    private String priority;

    @ValueOfEnum(enumClass = TaskCriticality.class)
    private String criticality;

    @ValueOfEnum(enumClass = TaskState.class)
    private String state;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private String authorId;
    private String responsableId;

    private Long projectId;

}
