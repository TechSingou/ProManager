package ml.malikura.service;

import ml.malikura.dto.EditProjectDTO;
import ml.malikura.dto.NewProjectDTO;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ProjectService {

    public Page<ProjectEntity> getProjectList(String keyword, int page,int size);

    public Optional<ProjectEntity> getProject(Long projectId);

    public  ProjectEntity saveNewProject(NewProjectDTO projectDTO);

    public ProjectEntity updateProduct(EditProjectDTO editProjectDTO, Long projectId);

    public void deleteProjectById(Long projectId);
}
