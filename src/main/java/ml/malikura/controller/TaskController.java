package ml.malikura.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.EditTaskDTO;
import ml.malikura.dto.EvaluateTaskDTO;
import ml.malikura.dto.NewTaskDTO;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import ml.malikura.service.TaskService;
import ml.malikura.util.ValueMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/newTaskFrom")
    @PreAuthorize("hasRole('MANAGER')")
    public String getNewTaskForm(Model model, @RequestParam(value = "projectId") Long projectId, @RequestParam(value = "projectName") String projectName) {
        log.info("TaskController :: getNewTaskForm execution started.");

        model.addAttribute("projectId", projectId);
        model.addAttribute("projectName", projectName);
        model.addAttribute("newTaskDTO", new NewTaskDTO());

        log.info("TaskController :: getNewTaskForm execution ended.");
        return "taskTemplates/newTaskForm";
    }

    @GetMapping("/editTaskFrom")
    @PreAuthorize("hasRole('MANAGER')")
    public String getEditTaskForm(Model model, @RequestParam(value = "taskId") Long taskId) {
        log.info("TaskController :: getEditTaskForm execution started.");

        TaskEntity taskEntity = this.taskService.getTask(taskId);
        EditTaskDTO editTaskDTO = ValueMapper.convertToEditDTO(taskEntity);
        model.addAttribute("editTaskDTO", editTaskDTO);
        model.addAttribute("projectId", taskEntity.getProject().getId());
        model.addAttribute("projectName", taskEntity.getProject().getTitle());

        log.info("TaskController :: getEditTaskForm execution ended.");
        return "taskTemplates/editTaskForm";
    }

    @PostMapping("/enregistrerNouvelleTache")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveNewTask(Model model, @Valid NewTaskDTO newTaskDTO, BindingResult bindingResult) {

        log.info("TaskController::saveNewTask execution started.");

        if (bindingResult.hasErrors()) {
            ProjectEntity associatedProject = this.taskService.getAssociatedProject(newTaskDTO.getProjectId());
            model.addAttribute("projectId", associatedProject.getId());
            model.addAttribute("projectName", associatedProject.getTitle());
            return "taskTemplates/newTaskForm";
        }
        TaskEntity savedTask = this.taskService.saveNewTask(newTaskDTO);

        log.info("TaskController::saveNewTask execution ended.");
        return "redirect:/viewTask?taskId=" + savedTask.getId();
    }

    @GetMapping("/viewTask")
    @PreAuthorize("hasRole('USER')")
    public String viewTask(Model model, @RequestParam(value = "taskId") Long taskId) {
        log.info("TaskController::viewTask execution started.");

        TaskEntity taskRetrieved = this.taskService.getTask(taskId);
        model.addAttribute("task", taskRetrieved);
        model.addAttribute("projectName", taskRetrieved.getProject().getTitle());
        model.addAttribute("projectId", taskRetrieved.getProject().getId());
        model.addAttribute("submitAllowed", Objects.equals(taskRetrieved.getState().toString(), "NON_SOUMIS") || Objects.equals(taskRetrieved.getState().toString(), "NON_RESOLU"));
        // model.addAttribute("responsable", taskRetrieved.getResponsable().getName())
        model.addAttribute("evaluateTaskDTO", new EvaluateTaskDTO());
        log.info("TaskController::viewTask execution ended.");
        return "taskTemplates/viewTask";
    }

    @PostMapping("/updateTask")
    @PreAuthorize("hasRole('MANAGER')")
    public String updateTask(Model model, @Valid EditTaskDTO editTaskDTO, BindingResult bindingResult) {
        log.info("TaskController :: updateTask execution started.");

        if (bindingResult.hasErrors()) {
            //System.out.println(bindingResult.getAllErrors());
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
                           @RequestParam(value = "size", defaultValue = "3") int size) {
        log.info("ProjectController::TaskList execution started");

        Page<TaskEntity> taskListPage = this.taskService.getAllByPage(keyword, page, size);
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
}
