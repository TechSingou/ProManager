package ml.malikura.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "EMPLOYEES")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String firstname;
    @Column(unique = true)
    private String email;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<RoleEmploye> roles;
    private String jobTitle;
    private String address;
    private String telephone;
    private Boolean accountEnabled;

    @OneToMany(mappedBy = "responsable")
    private List<TaskEntity> tasksToDo;

    @OneToMany(mappedBy = "author")
    private List<TaskEntity> tasksCreated;

}
