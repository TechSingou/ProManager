package ml.malikura.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCollaborateurDTO {
    @NotBlank(message = "le nom de l'employé ne doit pas NULL ou VIDE")
    private String name;

    @NotBlank(message = "le prenom de l'employé ne doit pas NULL ou VIDE")
    private String firstname;

    @NotBlank(message = "le champ email ne doit pas NULL ou VIDE")
    @Email(message = "Veuillez entrer un email valide")
    private String email;

    @NotBlank(message = "le champ password ne doit pas NULL ou VIDE")
    //Regex
    //@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial")
    private String password;

    @NotBlank(message = "le champ confirmation ne doit pas NULL ou VIDE")
    //Regex
    private String passwordConfirmation;

    @NotBlank(message = "le champ spécialité ne doit pas NULL ou VIDE")
    private String jobTitle;

    private String address;
    private String telephone;
    @NotBlank(message = "le champ role est obligatoire")
    private String role;
    @NotBlank(message = "le champ etat compte est obligatoire")
    private String etatCompte;
}
