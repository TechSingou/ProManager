package ml.malikura.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.EditProjectDTO;
import ml.malikura.dto.NewProjectDTO;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import ml.malikura.service.ProjectService;
import ml.malikura.util.ValueMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class ProjectController {
    ProjectService projectService;

    @GetMapping("/projets")
    @PreAuthorize("hasRole('USER')")
    public String projectsList(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "3") int size) {
        log.info("ProjectController::projectsList execution started");

        Page<ProjectEntity> projectListPage = projectService.getProjectList(keyword, page, size);
        model.addAttribute("projectList", projectListPage);
        model.addAttribute("currentKeyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("pages", new int[projectListPage.getTotalPages()]);

        log.info("ProjectController::projectsList execution ended.");
        return "projectTemplates/projectList";
    }

    @GetMapping("/nouveauProjetForm")
    @PreAuthorize("hasRole('MANAGER')")
    public String getNewProjectForm(Model model) {
        NewProjectDTO projectDTO = new NewProjectDTO();
        model.addAttribute("newProjectDTO", projectDTO);
        return "projectTemplates/newProjectForm";
    }

    @PostMapping("/enregistrerNouveauProjet")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveNewProject(Model model, @Valid NewProjectDTO project, BindingResult bindingResult) {
        log.info("ProjectController::saveNewProject execution started");

        if (bindingResult.hasErrors()) {
            return "projectTemplates/newProjectForm";
        }
        ProjectEntity project1 = projectService.saveNewProject(project);
        model.addAttribute("project", project1);

        log.info("ProjectController::saveNewProject execution ended.");
        return "commons/successPage";
    }

    @GetMapping("/viewProject")
    @PreAuthorize("hasRole('USER')")
    public String viewProject(Model model, @RequestParam(value = "projectId") Long projectId) {
        log.info("ProjectController::viewProject execution started");

        ProjectEntity project = projectService.getProject(projectId).get();
        model.addAttribute("project", project);

        log.info("ProjectController::viewProject execution ended");
        return "projectTemplates/viewProject";
    }


    @GetMapping()
    public String viewProjectMembersList() {
        return "";
    }

    public String statProject() {

        return "0";
    }

    @GetMapping("/editerProjetForm")
    @PreAuthorize("hasRole('MANAGER')")
    public String getEditProjectForm(Model model, @RequestParam(value = "projectId") Long projectId) {
        log.info("ProjectController::getEditProjectForm execution started");

        ProjectEntity project = projectService.getProject(projectId).get();
        EditProjectDTO editProjectDTO = ValueMapper.convertToEditDTO(project);
        model.addAttribute("editProjectDTO", editProjectDTO);

        log.info("ProjectController::getEditProjectForm execution ended");
        return "projectTemplates/editProjectForm";
    }

    @PostMapping("/miseAJourProjet")
    @PreAuthorize("hasRole('MANAGER')")
    public String updateProject(Model model, @Valid EditProjectDTO project, BindingResult bindingResult) {
        log.info("ProjectController::updateProject execution started");

        if (bindingResult.hasErrors()) {
            return "projectTemplates/editProjectForm";
        }
        projectService.updateProduct(project, project.getId());
        log.info("ProjectController::updateProject execution ended.");

        return "redirect:/viewProject?projectId=" + project.getId();
    }

    @GetMapping("/deleteProject")
    @PreAuthorize("hasRole('MANAGER')")
    public String deleteProject(@RequestParam(value = "projectId") Long projectId, @RequestParam(value = "keyword", defaultValue = "") String keyword, @RequestParam(value = "page", defaultValue = "0") int page) {
        log.info("ProjectController::deleteProject execution started.");

        projectService.deleteProjectById(projectId);
        log.info("ProjectController::deleteProject execution ended.");

        return "redirect:/projets?keyword=" + keyword + "&page=" + page;
    }
}
