package ml.malikura.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "COMMENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime pubDate;
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private EmployeEntity author;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;
}
