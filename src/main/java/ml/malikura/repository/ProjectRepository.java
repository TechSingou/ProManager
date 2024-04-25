package ml.malikura.repository;

import ml.malikura.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Optional<ProjectEntity> findByTitle(String title);
    // Find PROJECTS by keyword using @Query annotation
    Page<ProjectEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
