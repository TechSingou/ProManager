package ml.malikura.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.EditProjectDTO;
import ml.malikura.dto.NewProjectDTO;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import ml.malikura.exception.ProjectNotFoundException;
import ml.malikura.exception.ProjectServiceBusinessException;
import ml.malikura.repository.ProjectRepository;
import ml.malikura.util.ValueMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    ProjectRepository projectRepository;

    @Override
    public Page<ProjectEntity> getProjectList(String keyword, int page, int size) {
        Page<ProjectEntity> projectList = null;
        try {
            log.info("ProjectServiceImpl:getProjectList execution started.");
            projectList = projectRepository.findByTitleContainingIgnoreCase(keyword, PageRequest.of(page, size));
            if (projectList.isEmpty()) {
                projectList = Page.empty();
            }
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving projects from database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException("Exception occurred while fetch all projects from Database");
        }
        log.info("ProjectServiceImpl:getProducts execution ended.");
        return projectList;
    }

    @Override
    public Optional<ProjectEntity> getProject(Long projectId) {
        ProjectEntity project = null;
        try {
            log.info("ProjectServiceImpl:getProject execution started.");
            project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException("Projet introuvable !"));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving project {} from database , Exception message {}", projectId, ex.getMessage());
            throw new ProjectServiceBusinessException(ex.getMessage());
        }
        log.info("ProductService:getProductById execution ended.");
        return Optional.of(project);
    }

    @Override
    public ProjectEntity saveNewProject(NewProjectDTO projectDTO) {
        ProjectEntity project = null;
        try {
            log.info("ProjectServiceImpl: saveNewProject execution started");
            Optional<ProjectEntity> projectExists = projectRepository.findByTitle(projectDTO.getTitle());
            if (projectExists.isPresent()) {
                throw new ProjectServiceBusinessException("Un projet avec le nom \"" + projectExists.get().getTitle() + "\" existe déjà");
            }
            project = projectRepository.save(ValueMapper.convertToEntity(projectDTO));
        } catch (Exception ex) {
            log.error("Exception occurred while persisting project to database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException(ex.getMessage());
        }
        return project;
    }

    @Override
    public ProjectEntity updateProduct(EditProjectDTO editProjectDTO, Long projectId) {
        ProjectEntity updateResult = null;
        try {
            log.info("ProjectServiceImpl:updateProduct execution started.");
            Optional<ProjectEntity> projectToUpdate = projectRepository.findById(projectId);
            if (projectToUpdate.isEmpty()) {
                throw new ProjectServiceBusinessException("Ce projet est introuvable dans la base de données.");
            }
            updateResult = projectRepository.save(ValueMapper.convertToEntity(editProjectDTO));
        } catch (Exception ex) {
            log.error("Exception occurred while updating project to database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException(ex.getMessage());
        }
        log.info("ProjectServiceImpl:updateProduct execution ended");
        return updateResult;
    }

    @Override
    public void deleteProjectById(Long projectId) {
        try {
            log.info("ProjectServiceImpl:deleteProjectById execution started.");
            projectRepository.deleteById(projectId);
            log.info("ProjectServiceImpl:deleteProjectById execution ended.");
        } catch (Exception ex) {
            log.error("Exception occurred while deleting project to database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException("Exception occurred while delete existing project");
        }
    }

}
