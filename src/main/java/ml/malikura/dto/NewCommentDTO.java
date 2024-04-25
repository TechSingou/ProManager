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

    @NotBlank(message = "le contenu ne doit pas NULL ou VIDE")
    private String content;
    private Long authorId;
    private Long taskId;
}
