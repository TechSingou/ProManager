package ml.malikura.repository;

import ml.malikura.entity.CommentEntity;
import ml.malikura.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    List<CommentEntity> findByTaskOrderByPubDateDesc(TaskEntity task);
}
