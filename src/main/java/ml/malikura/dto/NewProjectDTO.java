package ml.malikura.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ml.malikura.dto.validation.DateRange;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DateRange(startDateField = "startDate",endDateField = "endDate",message = "Date début doit avant Date fin et les deux dates dans le future")
public class NewProjectDTO {

    @NotBlank(message = "Champ obligatoire*")
    private String title;

    @NotBlank(message = "Champ obligatoire*")
    @Size(min = 15, max = 150,message = "La description doit être entre 15 et 150 caractères")
    private String description;

    @NotNull(message = "Champ obligatoire*")
    @FutureOrPresent(message = "La date de début doit etre dans le présent ou dans le future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @NotNull(message = "Champ obligatoire*")
    @Future(message = "La date de fin doit etre dans le future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    private String specificNote;
}
