package ml.malikura.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InscriptionRequestDTO {

    @NotBlank(message = "le nom de l'employé ne doit pas NULL ou VIDE")
    private String name;

    @NotBlank(message = "le prenom de l'employé ne doit pas NULL ou VIDE")
    private String firstname;

    @NotBlank(message = "le champ email ne doit pas NULL ou VIDE")
    @Email(message = "Veuillez entrer un email valide")
    private String email;

    @NotBlank(message = "le champ password ne doit pas NULL ou VIDE")
    //Regex
    private String password;

    @NotBlank(message = "le champ confirmation ne doit pas NULL ou VIDE")
    //Regex
    private String passwordConfirmation;

    @NotBlank(message = "le champ spécialité ne doit pas NULL ou VIDE")
    private String jobTitle;

    private String address;
    private String telephone;

}
