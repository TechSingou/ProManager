package ml.malikura.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.EditTaskDTO;
import ml.malikura.dto.EvaluateTaskDTO;
import ml.malikura.dto.NewTaskDTO;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import ml.malikura.exception.ProjectServiceBusinessException;
import ml.malikura.repository.TaskRepository;
import ml.malikura.util.ResolutionEnum;
import ml.malikura.util.TaskState;
import ml.malikura.util.ValueMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;
    ProjectService projectService;
    EmployeService employeService;

    @Override
    public TaskEntity saveNewTask(NewTaskDTO newTaskDTO) {
        TaskEntity savedTask = null;
        TaskEntity newTask = null;
        try {
            log.info("TaskServiceImpl:saveNewTask execution start.");
            // Check if the giving task name exists
            Optional<TaskEntity> findOne = taskRepository.findByTitle(newTaskDTO.getTitle());
            if (findOne.isPresent()) {
                throw new ProjectServiceBusinessException("Une tâche avec le nom \"" + findOne.get().getTitle() + "\" existe déjà");
            }
            // Find the root project and the executor of the task
            ProjectEntity findProject = projectService.getProject(newTaskDTO.getProjectId()).get();
            EmployeEntity responsable = null;
            if (newTaskDTO.getResponsableId() != null) {
                responsable = employeService.loadEmployeByEmail(newTaskDTO.getResponsableId());
                newTaskDTO.setAffectationDate(LocalDateTime.now());
            }
            newTask = ValueMapper.convertToEntity(newTaskDTO);

            //Set the root project and the executor
            newTask.setProject(findProject);
            newTask.setResponsable(responsable);

            savedTask = taskRepository.save(newTask);

        } catch (Exception exception) {
            log.info("Exception occurred persisting New Task in the database, Exception message {} ", exception.getMessage());
            throw new ProjectServiceBusinessException(exception.getMessage());
        }
        log.info("TaskServiceImpl:saveNewTask execution ended. ");
        return savedTask;
    }

    @Override
    public TaskEntity getTask(Long taskId) {
        TaskEntity taskFound = null;
        try {
            log.info("TaskServiceImpl:getTask execution started.");
            taskFound = taskRepository.findById(taskId).orElseThrow(
                    () -> new ProjectServiceBusinessException("La tâche avec ID " + taskId + " est introuvable.")
            );
        } catch (Exception exception) {
            log.info("Exception occurred while fetching task in the database, Exception message {}", exception.getMessage());
            throw new ProjectServiceBusinessException(exception.getMessage());
        }
        log.info("TaskServiceImpl:getTask execution ended.");
        return taskFound;
    }

    @Override
    public Page<TaskEntity> getTasksByProject(Long projectId, int page, int size) {
        Page<TaskEntity> entityPage = null;
        try {
            log.info("TaskServiceImpl:getTaskByProject execution started.");
            ProjectEntity project = projectService.getProject(projectId).get();
            entityPage = taskRepository.findByProject(project, PageRequest.of(page, size));
            if (entityPage.isEmpty()) {
                entityPage = Page.empty();
            }
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving tasks from database , Exception message {}", exception.getMessage());
            throw new ProjectServiceBusinessException("Exception occurred while fetch all tasks from Database");
        }
        log.info("TaskServiceImpl:getTaskByProject execution ended.");
        return entityPage;
    }

    @Override
    public void updateTask(EditTaskDTO editTaskDTO, Long taskId) {
        TaskEntity updateResult = null;
        try {
            log.info("TaskServiceImpl:updateTask execution started.");
            TaskEntity taskToUpdate = taskRepository.findById(taskId).orElse(null);
            ProjectEntity associatedProject = projectService.getProject(editTaskDTO.getProjectId()).get();
            if (taskToUpdate == null) {
                throw new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.");
            }
            TaskEntity taskNewValues = ValueMapper.convertToEntity(editTaskDTO);
            taskNewValues.setProject(associatedProject);
            taskToUpdate = taskNewValues;
            // set executor
            updateResult = taskRepository.save(taskToUpdate);
        } catch (Exception ex) {
            log.error("Exception occurred while updating project to database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException(ex.getMessage());
        }
        log.info("TaskServiceImpl:updateTask execution ended");
    }

    @Override
    public void onTaskSubmit(Long taskId) {
        TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);
        if (taskEntity == null) {
            throw new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.");
        }
        taskEntity.setState(TaskState.SOUMIS);
        taskRepository.save(taskEntity);
    }

    @Override
    public ProjectEntity getAssociatedProject(Long projectId) {
        return projectService.getProject(projectId).get();
    }

    @Override
    public Page<TaskEntity> getAllByPage(String keyword, int page, int size) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword, PageRequest.of(page, size));
    }

    @Override
    public void evaluateTask(EvaluateTaskDTO evaluateTaskDTO, Long taskId) {
        TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);
        if (taskEntity == null) {
            throw new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.");
        }
        taskEntity.setTaskEvaluation(evaluateTaskDTO.getTaskEvaluation());
        taskEntity.setMark(evaluateTaskDTO.getMark());
        taskRepository.save(taskEntity);
    }

    @Override
    public void solveTask(Long taskId, String proposition) {
        TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);
        if (taskEntity == null) {
            throw new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.");
        }
        if (proposition.equals(String.valueOf(ResolutionEnum.CLOTURE))) {
            taskEntity.setState(TaskState.RESOLU);
        } else {
            taskEntity.setState(TaskState.NON_RESOLU);
        }
        taskRepository.save(taskEntity);
    }

    @Override
    public void deleteTaskById(Long taskId) {
        log.info("TaskServiceImpl : deleteTaskById execution started.");
        taskRepository.findById(taskId).orElseThrow(
                () -> new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.")
        );
        taskRepository.deleteById(taskId);
        log.info("TaskServiceImpl : deleteTaskById execution ended.");
    }
}
