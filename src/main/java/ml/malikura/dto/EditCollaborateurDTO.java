package ml.malikura.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditCollaborateurDTO {
    @NotBlank(message = "le nom de l'employé ne doit pas NULL ou VIDE")
    private String name;

    @NotBlank(message = "le prenom de l'employé ne doit pas NULL ou VIDE")
    private String firstname;

    @NotBlank(message = "le champ email ne doit pas NULL ou VIDE")
    @Email(message = "Veuillez entrer un email valide")
    private String email;

    @NotBlank(message = "le champ spécialité ne doit pas NULL ou VIDE")
    private String jobTitle;

    private String address;

    private String telephone;

    @NotBlank(message = "le champ role est obligatoire")
    private String role;

    @NotBlank(message = "le champ etat compte est obligatoire")
    private String etatCompte;
}
