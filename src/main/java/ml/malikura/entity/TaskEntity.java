package ml.malikura.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ml.malikura.util.TaskCriticality;
import ml.malikura.util.TaskPriority;
import ml.malikura.util.TaskState;
import ml.malikura.util.TaskStatut;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "TASKS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskCriticality criticality;
    private TaskStatut statut;
    private TaskState state;
    private LocalDateTime creationDate;
    private LocalDateTime affectationDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double taskEvaluation;
    private String mark;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private EmployeEntity author;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private EmployeEntity responsable;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

//    @OneToMany(mappedBy = "task",fetch = FetchType.LAZY)
//    private List<CommentEntity> comments;

}
