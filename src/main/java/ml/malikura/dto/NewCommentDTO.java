package ml.malikura.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDTO {

    @NotBlank(message = "Ce champ est obligatoire")
    private String content;
    private String authorComment;
    private Long taskId;
}
