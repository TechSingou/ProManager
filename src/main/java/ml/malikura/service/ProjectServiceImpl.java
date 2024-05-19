package ml.malikura.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.EditProjectDTO;
import ml.malikura.dto.NewProjectDTO;
import ml.malikura.entity.EmployeEntity;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    ProjectRepository projectRepository;
    private EmployeService employeService;


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


    @Override
    public Set<EmployeEntity> getMembersList(Long projectId) {
        Optional<ProjectEntity> project = getProject(projectId);
        return project.map(ProjectEntity::getMembers).orElse(null);
    }

    @Override
    public Set<EmployeEntity> getNotMembersList(Long projectId) {
        Set<EmployeEntity> employeesNotMembersList = null;
        try {
            log.info("ProjectServiceImpl:getNotMembersList execution started.");
            Set<EmployeEntity> employeeListWithRoleUser = (Set<EmployeEntity>) employeService.getAll().stream().filter(e -> e.getRoles().size() == 1).collect(Collectors.toSet());
            Set<EmployeEntity> membersOfProject = getMembersList(projectId);
            if (membersOfProject.isEmpty()) {
                employeesNotMembersList = (Set<EmployeEntity>) employeeListWithRoleUser;
            } else {
                for (EmployeEntity m : membersOfProject) {
                    employeesNotMembersList = employeeListWithRoleUser.stream().filter(epl -> !Objects.equals(epl.getEmail(), m.getEmail())).collect(Collectors.toSet());
                }
            }

        } catch (Exception exception) {
            log.error("Exception occurred while retrieving employees from database , Exception message {}", exception.getMessage());
            throw new ProjectServiceBusinessException("Exception occurred while fetch employees from Database");
        }
        log.info("ProjectServiceImpl:getNotMembersList execution ended.");
        return employeesNotMembersList;
    }

    @Override
    public void addNewEmployeToProject(String email, Long projectId) {
        log.info("ProjectServiceImpl:addNewEmployeToProject execution started.");
        EmployeEntity employeeToAdd = employeService.loadEmployeByEmail(email);
        ProjectEntity myProject = getProject(projectId).orElseThrow(null);
        if (employeeToAdd == null)
            throw new ProjectServiceBusinessException("Ce membre existe pas dans la base de données");
        if (myProject == null)
            throw new ProjectServiceBusinessException("Vous essayez d'ajouter un membre à ce projet qui n'existe pas.");
        myProject.getMembers().add(employeeToAdd);
        projectRepository.save(myProject);
        log.info("ProjectServiceImpl:addNewEmployeToProject execution ended.");
    }

    // Method used to manage member based access to a project
    @Override
    public boolean isMemberOfProject(String email, Long projectId) {
        ProjectEntity project = getProject(projectId).get();
        EmployeEntity employee = employeService.loadEmployeByEmail(email);
        return project.getMembers().contains(employee);
    }

}
