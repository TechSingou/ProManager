package ml.malikura.repository;

import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeRepository extends JpaRepository<EmployeEntity, Long> {
    EmployeEntity findByEmail(String email);
    Page<EmployeEntity> findByFirstnameContainingIgnoreCase(String firstname, Pageable pageable);
}
