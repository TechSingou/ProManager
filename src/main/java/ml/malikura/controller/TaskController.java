package ml.malikura.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.EditTaskDTO;
import ml.malikura.dto.EvaluateTaskDTO;
import ml.malikura.dto.NewCommentDTO;
import ml.malikura.dto.NewTaskDTO;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import ml.malikura.repository.CommentRepository;
import ml.malikura.service.ProjectService;
import ml.malikura.service.TaskService;
import ml.malikura.util.ValueMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@AllArgsConstructor
@Slf4j
public class TaskController {

    private TaskService taskService;
    private ProjectService projectService;
    private CommentRepository commentRepository;

    @GetMapping("/newTaskFrom")
    @PreAuthorize("hasRole('MANAGER')")
    public String getNewTaskForm(Model model, @RequestParam(value = "projectId") Long projectId, @RequestParam(value = "projectName") String projectName) {
        log.info("TaskController :: getNewTaskForm execution started.");
        var project = projectService.getProject(projectId);

        model.addAttribute("projectId", projectId);
        model.addAttribute("projectName", projectName);
        model.addAttribute("newTaskDTO", new NewTaskDTO());
        project.ifPresent(projectEntity -> model.addAttribute("membersOfProject", projectEntity.getMembers()));

        log.info("TaskController :: getNewTaskForm execution ended.");
        return "taskTemplates/newTaskForm";
    }

    @GetMapping("/editTaskFrom")
    @PreAuthorize("hasRole('MANAGER')")
    public String getEditTaskForm(Model model, @RequestParam(value = "taskId") Long taskId) {
        log.info("TaskController :: getEditTaskForm execution started.");

        var taskEntity = this.taskService.getTask(taskId);
        EditTaskDTO editTaskDTO = ValueMapper.convertToEditDTO(taskEntity);
        model.addAttribute("editTaskDTO", editTaskDTO);
        model.addAttribute("projectId", taskEntity.getProject().getId());
        model.addAttribute("projectName", taskEntity.getProject().getTitle());
        model.addAttribute("membersOfProject", taskEntity.getProject().getMembers());

        log.info("TaskController :: getEditTaskForm execution ended.");
        return "taskTemplates/editTaskForm";
    }

    @PostMapping("/enregistrerNouvelleTache")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveNewTask(Model model, @Valid NewTaskDTO newTaskDTO, BindingResult bindingResult) {
        log.info("TaskController::saveNewTask execution started.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var authorEmail = authentication.getName();

        if (bindingResult.hasErrors()) {
            ProjectEntity associatedProject = this.taskService.getAssociatedProject(newTaskDTO.getProjectId());
            model.addAttribute("projectId", associatedProject.getId());
            model.addAttribute("projectName", associatedProject.getTitle());
            return "taskTemplates/newTaskForm";
        }
        newTaskDTO.setAuthorId(authorEmail);
        TaskEntity savedTask = this.taskService.saveNewTask(newTaskDTO);

        log.info("TaskController::saveNewTask execution ended.");
        return "redirect:/viewTask?taskId=" + savedTask.getId();
    }

    @GetMapping("/viewTask")
    @PreAuthorize("hasRole('USER')")
    public String viewTask(Model model, @RequestParam(value = "taskId") Long taskId) {
        log.info("TaskController::viewTask execution started.");

        TaskEntity taskRetrieved = this.taskService.getTask(taskId);
        var allComments = commentRepository.findByTaskOrderByPubDateDesc(taskRetrieved);

        model.addAttribute("task", taskRetrieved);
        model.addAttribute("projectName", taskRetrieved.getProject().getTitle());
        model.addAttribute("projectId", taskRetrieved.getProject().getId());
        model.addAttribute("submitAllowed", Objects.equals(taskRetrieved.getState().toString(),
                "NON_SOUMIS") || Objects.equals(taskRetrieved.getState().toString(), "NON_RESOLU"));
        model.addAttribute("resolveAllowed", Objects.equals(taskRetrieved.getState().toString(), "SOUMIS"));
        model.addAttribute("evaluateTaskDTO", new EvaluateTaskDTO());
        model.addAttribute("newCommentDTO", new NewCommentDTO());
        model.addAttribute("comments", allComments);
        log.info("TaskController::viewTask execution ended.");
        return "taskTemplates/viewTask";
    }

    // Not currently working as expected after adding the executor update feature(to solve)
    @PostMapping("/updateTask")
    @PreAuthorize("hasRole('MANAGER')")
    public String updateTask(Model model, @Valid EditTaskDTO editTaskDTO, BindingResult bindingResult) {
        log.info("TaskController :: updateTask execution started.");

        if (bindingResult.hasErrors()) {
            ProjectEntity associatedProject = this.taskService.getAssociatedProject(editTaskDTO.getProjectId());
            model.addAttribute("projectId", associatedProject.getId());
            model.addAttribute("projectName", associatedProject.getTitle());
            return "taskTemplates/editTaskForm";
        }
        taskService.updateTask(editTaskDTO, editTaskDTO.getId());

        log.info("TaskController :: updateTask execution ended.");
        return "redirect:/viewTask?taskId=" + editTaskDTO.getId();
    }

    @GetMapping("/getProjectTasks")
    @PreAuthorize("hasRole('MANAGER')")
    public String viewProjectTaskList(Model model, @RequestParam(value = "projectId") Long projectId,
                                      @RequestParam(value = "projectName") String projectName,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "4") int size) {
        log.info("ProjectController::viewProjectTaskList execution started.");
        Page<TaskEntity> taskEntityPage = this.taskService.getTasksByProject(projectId, page, size);
        model.addAttribute("taskListPage", taskEntityPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pages", new int[taskEntityPage.getTotalPages()]);
        model.addAttribute("projectName", projectName);
        model.addAttribute("projectId", projectId);
        log.info("ProjectController::viewProjectTaskList execution ended.");
        return "projectTemplates/viewProjectListTasks";
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('USER')")
    public String TaskList(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "3") int size,
                           Authentication authentication
                           ) {
        log.info("ProjectController::TaskList execution started");
        Page<TaskEntity> taskListPage = null;
        boolean anyMatch = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"));
        if(anyMatch){
            taskListPage = this.taskService.getAllByPage(keyword, page, size);
        }else {
            taskListPage = this.taskService.getMyTasks(authentication.getName(),page,size);
        }
        model.addAttribute("taskListPage", taskListPage);
        model.addAttribute("currentKeyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("pages", new int[taskListPage.getTotalPages()]);

        log.info("ProjectController::TaskList execution ended.");
        return "taskTemplates/taskList";
    }

    @GetMapping("/submitTask")
    @PreAuthorize("hasRole('USER')")
    public String submitTask(@RequestParam(value = "taskId") Long taskId) {
        log.info("ProjectController :: submitTask execution started");
        // Car for service
        this.taskService.onTaskSubmit(taskId);

        log.info("ProjectController :: submitTask execution ended");
        return "redirect:/viewTask?taskId=" + taskId;
    }

    @PostMapping("/evaluateTask")
    @PreAuthorize("hasRole('MANAGER')")
    public String onEvaluateTask(Model model, @Valid EvaluateTaskDTO evaluateTaskDTO,
                                 BindingResult bindingResult, @RequestParam(value = "taskId") Long taskId) {
        log.info("ProjectController :: onEvaluateTask execution started");
        if (bindingResult.hasErrors()) {
            TaskEntity taskRetrieved = this.taskService.getTask(taskId);
            model.addAttribute("task", taskRetrieved);
            model.addAttribute("projectName", taskRetrieved.getProject().getTitle());
            model.addAttribute("projectId", taskRetrieved.getProject().getId());
            model.addAttribute("submitAllowed", Objects.equals(taskRetrieved.getState().toString(), "NON_SOUMIS") || Objects.equals(taskRetrieved.getState().toString(), "NON_RESOLU"));
            return "taskTemplates/viewTask";
        }
        this.taskService.evaluateTask(evaluateTaskDTO, taskId);
        log.info("ProjectController :: onEvaluateTask execution ended.");
        return "redirect:/viewTask?taskId=" + taskId;
    }

    @PostMapping("/solveTask")
    @PreAuthorize("hasRole('MANAGER')")
    public String onSolveTask(Model model, @RequestParam(value = "taskId") Long taskId, String proposition) {
        this.taskService.solveTask(taskId, proposition);
        return "redirect:/viewTask?taskId=" + taskId;
    }

    @GetMapping("/deleteTask")
    @PreAuthorize("hasRole('MANAGER')")
    public String deleteTask(@RequestParam(value = "taskId") Long taskId, @RequestParam(value = "projectId") Long projectId,
                             @RequestParam(value = "projectName") String projectName) {
        this.taskService.deleteTaskById(taskId);
        return "redirect:/getProjectTasks?projectId=" + projectId + "&projectName=" + projectName;
    }


    // To start with, the post method is not working with argument

    @PostMapping("/addNewComment")
    @PreAuthorize("hasRole('MANAGER')")
    public String addNewComment( @Valid NewCommentDTO newCommentDTO, Model model, BindingResult bindingResult) {
        log.info("ProjectController :: addNewComment execution started");

        TaskEntity taskRetrieved = this.taskService.getTask(newCommentDTO.getTaskId());
        model.addAttribute("task", taskRetrieved);
        model.addAttribute("projectName", taskRetrieved.getProject().getTitle());
        model.addAttribute("projectId", taskRetrieved.getProject().getId());
        model.addAttribute("evaluateTaskDTO", new EvaluateTaskDTO());
        model.addAttribute("submitAllowed", Objects.equals(taskRetrieved.getState().toString(),
                "NON_SOUMIS") || Objects.equals(taskRetrieved.getState().toString(), "NON_RESOLU"));
        if(bindingResult.hasErrors()){
            var allComments = commentRepository.findByTaskOrderByPubDateDesc(taskRetrieved);
            model.addAttribute("comments", allComments);
            return "taskTemplates/viewTask";
        }

        this.taskService.saveComment(newCommentDTO.getTaskId(),newCommentDTO.getAuthorComment(),newCommentDTO.getContent());
        var allComments = commentRepository.findByTaskOrderByPubDateDesc(taskRetrieved);
        model.addAttribute("comments", allComments);
        log.info("ProjectController :: addNewComment execution ended");
        return "taskTemplates/viewTask";
    }
}
