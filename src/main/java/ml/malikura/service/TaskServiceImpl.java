package ml.malikura.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.EditTaskDTO;
import ml.malikura.dto.EvaluateTaskDTO;
import ml.malikura.dto.NewCommentDTO;
import ml.malikura.dto.NewTaskDTO;
import ml.malikura.entity.CommentEntity;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import ml.malikura.exception.ProjectServiceBusinessException;
import ml.malikura.repository.CommentRepository;
import ml.malikura.repository.TaskRepository;
import ml.malikura.util.ResolutionEnum;
import ml.malikura.util.TaskState;
import ml.malikura.util.ValueMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;
    private ProjectService projectService;
    private EmployeService employeService;
    private CommentRepository commentRepository;

    @Override
    public TaskEntity saveNewTask(NewTaskDTO newTaskDTO) {
        TaskEntity savedTask = null;
        TaskEntity newTask = null;
        try {
            log.info("TaskServiceImpl:saveNewTask execution start.");
            // Check if the given task name exists
            Optional<TaskEntity> findOne = taskRepository.findByTitle(newTaskDTO.getTitle());
            if (findOne.isPresent()) {
                throw new ProjectServiceBusinessException("Une tâche avec le nom \"" + findOne.get().getTitle() + "\" existe déjà");
            }
            // Find the root project, author and the executor of the task
            ProjectEntity findProject = projectService.getProject(newTaskDTO.getProjectId()).get();
            EmployeEntity taskExecutor = null;
            EmployeEntity authorTask = null;
            if (newTaskDTO.getResponsableId() != null) {
                taskExecutor = employeService.loadEmployeByEmail(newTaskDTO.getResponsableId());
                newTaskDTO.setAffectationDate(LocalDateTime.now());
            }
            authorTask = employeService.loadEmployeByEmail(newTaskDTO.getAuthorId());

            newTask = ValueMapper.convertToEntity(newTaskDTO);

            //Set the root project and the executor
            newTask.setProject(findProject);
            newTask.setResponsable(taskExecutor);
            newTask.setAuthor(authorTask);

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
        EmployeEntity taskExecutor = null;
        try {
            log.info("TaskServiceImpl:updateTask execution started.");
            TaskEntity taskToUpdate = taskRepository.findById(taskId).orElse(null);
            ProjectEntity associatedProject = projectService.getProject(editTaskDTO.getProjectId()).get();
            if (taskToUpdate == null) {
                throw new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.");
            }
            TaskEntity taskNewValues = ValueMapper.convertToEntity(editTaskDTO);
            if (editTaskDTO.getResponsableId() != null) {
                taskExecutor = employeService.loadEmployeByEmail(editTaskDTO.getResponsableId());
                taskNewValues.setAffectationDate(LocalDateTime.now());
            }
            // set executor
            taskNewValues.setResponsable(taskExecutor);
            // set the associated project
            taskNewValues.setProject(associatedProject);
            // set author
            taskNewValues.setAuthor(taskToUpdate.getAuthor());
            // set creation date
            taskNewValues.setCreationDate(taskToUpdate.getCreationDate());

            taskRepository.save(taskNewValues);
        } catch (Exception ex) {
            log.error("Exception occurred while updating project to database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException("Exception occurred while updating project to database");
        }
        log.info("TaskServiceImpl:updateTask execution ended");
    }

    //Task submission logic
    @Override
    public void onTaskSubmit(Long taskId) {
        log.info("TaskServiceImpl:onTaskSubmit execution started.");
        try {
            TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);
            if (taskEntity == null) {
                throw new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.");
            }
            taskEntity.setState(TaskState.SOUMIS);
            taskRepository.save(taskEntity);
        } catch (Exception ex) {
            log.error("Exception occurred while submitting the task ");
            throw new ProjectServiceBusinessException("Exception occurred while submitting the task");
        }
        log.info("TaskServiceImpl:onTaskSubmit execution ended.");
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
    public Page<TaskEntity> getMyTasks(String email,int page, int size) {
        log.info("TaskServiceImpl:getMyTasks execution started.");
        Page<TaskEntity> myProjects = null;
        try {
            myProjects = taskRepository.findByExecutorEmail(email,PageRequest.of(page,size));
            if(myProjects.isEmpty())
            {
                myProjects = Page.empty();
            }
        }catch (Exception ex){
            log.error("Exception occurred while retrieving tasks from database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException("Exception occurred while fetch all projects from Database");
        }
        log.info("TaskServiceImpl:getMyTasks execution ended.");
        return myProjects;
    }

    // Task evaluation logic
    @Override
    public void evaluateTask(EvaluateTaskDTO evaluateTaskDTO, Long taskId) {
        log.info("TaskServiceImpl:evaluateTask execution started.");
        try {
            TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);
            if (taskEntity == null) {
                throw new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.");
            }
            taskEntity.setTaskEvaluation(evaluateTaskDTO.getTaskEvaluation());
            taskEntity.setMark(evaluateTaskDTO.getMark());
            taskRepository.save(taskEntity);
        } catch (Exception ex) {
            log.error("Exception occurred while evaluating the task ");
            throw new ProjectServiceBusinessException("Exception occurred while evaluating the task ");
        }
        log.info("TaskServiceImpl:evaluateTask execution ended.");
    }

    // Task solving logic
    @Override
    public void solveTask(Long taskId, String proposition) {
        log.info("TaskServiceImpl:solveTask execution started.");
        try {
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
        } catch (Exception ex) {
            log.error("Exception occurred while solving the task ");
            throw new ProjectServiceBusinessException("Exception occurred while solving the task ");
        }
        log.info("TaskServiceImpl:solveTask execution ended.");
    }

    @Override
    public void deleteTaskById(Long taskId) {
        log.info("TaskServiceImpl : deleteTaskById execution started.");
        try {
            taskRepository.findById(taskId).orElseThrow(
                    () -> new ProjectServiceBusinessException("Cette tâche est introuvable dans la base de données.")
            );
            taskRepository.deleteById(taskId);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting the task ");
            throw new ProjectServiceBusinessException("Exception occurred while deleting the task");
        }
        log.info("TaskServiceImpl : deleteTaskById execution ended.");
    }

    @Override
    public void saveComment(Long taskId,String authorComment, String content) {
        CommentEntity newComment = new CommentEntity();
        log.info("TaskServiceImpl : saveComment execution started.");
        try {
            EmployeEntity commentAuth = employeService.loadEmployeByEmail(authorComment);
            TaskEntity associatedTask = this.getTask(taskId);

            newComment.setPubDate(LocalDateTime.now());
            newComment.setContent(content);
            newComment.setAuthor(commentAuth);
            newComment.setTask(associatedTask);
            commentRepository.save(newComment);

        } catch (Exception ex) {
            log.error("Exception occurred while saving new comment.");
            throw new ProjectServiceBusinessException("Exception occurred while saving new comment.");
        }
        log.info("TaskServiceImpl : saveComment execution ended.");
    }

    @Override
    public boolean isExecutorOfTask(String email, Long taskId) {
        TaskEntity taskEntity = getTask(taskId);
        if (taskEntity.getResponsable() == null)
            return false;
        return Objects.equals(taskEntity.getResponsable().getEmail(), email);
    }

}
