package ml.malikura.service;

import ml.malikura.dto.EditProjectDTO;
import ml.malikura.dto.NewProjectDTO;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectService {

    public Page<ProjectEntity> getProjectList(String keyword, int page, int size);

    public Optional<ProjectEntity> getProject(Long projectId);

    public ProjectEntity saveNewProject(NewProjectDTO projectDTO);

    public ProjectEntity updateProduct(EditProjectDTO editProjectDTO, Long projectId);

    public void deleteProjectById(Long projectId);

    Set<EmployeEntity> getMembersList(Long projectId);

    Set<EmployeEntity> getNotMembersList(Long projectId);

    void addNewEmployeToProject(String email, Long projectId);

    boolean isMemberOfProject(String email, Long projectId);
}
