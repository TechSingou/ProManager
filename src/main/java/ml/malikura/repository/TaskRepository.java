package ml.malikura.repository;

import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    Optional<TaskEntity> findByTitle(String title);

    Page<TaskEntity> findByProject(ProjectEntity project, Pageable pageable);

    Page<TaskEntity> findByTitleContainingIgnoreCase(String title,Pageable pageable);
}
