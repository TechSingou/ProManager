package ml.malikura.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ml.malikura.dto.validation.ValueOfEnum;
import ml.malikura.util.TaskCriticality;
import ml.malikura.util.TaskPriority;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTaskDTO {

    @NotBlank(message = "Champ obligatoire*")
    private String title;

    private String description;

    @ValueOfEnum(enumClass = TaskPriority.class)
    private String priority;

    @ValueOfEnum(enumClass = TaskCriticality.class)
    private String criticality;

    @NotNull(message = "Champ obligatoire*")
    @FutureOrPresent(message = "La date de début doit être ce jour ou une date future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @NotNull(message = "Champ obligatoire*")
    @Future(message = "La date de fin doit être dans le future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime affectationDate;

    //private Long authorId;
    private String responsableId;
    private Long projectId;
}
