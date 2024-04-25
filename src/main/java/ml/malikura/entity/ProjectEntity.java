package ml.malikura.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ml.malikura.util.ProjectStatut;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "PROJECTS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String specificNote;

    @Enumerated(EnumType.STRING)
    private ProjectStatut statut;

    @OneToMany
    @JoinColumn(name = "project_id")
    private List<EmployeEntity> members;

    @OneToMany(mappedBy = "project")
    private List<TaskEntity> tasks;
}
