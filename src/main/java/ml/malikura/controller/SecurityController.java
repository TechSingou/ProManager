package ml.malikura.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ml.malikura.dto.NewEmployeDTO;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.TaskEntity;
import ml.malikura.repository.EmployeRepository;
import ml.malikura.repository.ProjectRepository;
import ml.malikura.repository.TaskRepository;
import ml.malikura.service.EmployeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class SecurityController {
    ProjectRepository projectRepository;
    TaskRepository taskRepository;
    EmployeRepository employeRepository;
    private EmployeService employeService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "commons/loginPage";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        // Retrieve the authentication object from the security context
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        model.addAttribute("logoutSuccess", true);
        return "commons/loginPage";
    }

    @GetMapping("/formInscription")
    public String inscriptionForm(Model model) {
        model.addAttribute("newEmployeDTO", new NewEmployeDTO());
        return "commons/inscription";
    }

    @PostMapping("/inscriptionProcess")
    public String inscriptionProcess(Model model, @Valid NewEmployeDTO newEmployeDTO, BindingResult bindingResult) {
        // Check if password and passwordConfirmation match
        if (!newEmployeDTO.getPassword().equals(newEmployeDTO.getPasswordConfirmation())) {
            bindingResult.rejectValue("passwordConfirmation", "error.password.notMatch", "Les mots de passe ne correspondent pas.");
        }
        if (bindingResult.hasErrors())
            return "commons/inscription";
        EmployeEntity employeEntity = this.employeService.addNewEmploye(newEmployeDTO);
        return "redirect:/inscriptionConfirmation?emplId=" + employeEntity.getEmail();
    }

    @GetMapping("/inscriptionConfirmation")
    public String inscriptionConfirmation(@RequestParam("emplId") String employeId) {
        EmployeEntity newEmployee = employeService.loadEmployeByEmail(employeId);
        return "commons/inscriptionConfirmationPage";
    }

    @GetMapping("/index")
    @PreAuthorize("hasRole('USER')")
    public String home(Model model) {
        List<ProjectEntity> allProjects = projectRepository.findAll();
        List<ProjectEntity> finishedProjects = allProjects.stream().filter(project -> project.getStatut().toString().equals("TERMINE")).toList();
        List<TaskEntity> allTasks = taskRepository.findAll();
        List<TaskEntity> finishedTasks = allTasks.stream().filter(task -> task.getStatut().toString().equals("TERMINE")).toList();
        List<EmployeEntity> allEmployees = employeRepository.findAll();
        List<EmployeEntity> inactivatedAccounts = allEmployees.stream().filter(employe -> employe.getAccountEnabled().equals(false)).toList();

        model.addAttribute("totalProjects", allProjects.size());
        model.addAttribute("totalFinishedProjects", finishedProjects.size());
        model.addAttribute("totalTasks", allTasks.size());
        model.addAttribute("totalFinishedTasks", finishedTasks.size());
        model.addAttribute("totalEmployees", allEmployees.size());
        model.addAttribute("inactivatedAccounts", inactivatedAccounts.size());
        return "commons/home";
    }

    public String notFoundPage() {

        return "0";
    }

    @GetMapping("/notAutorized")
    public String notAutorized() {
        return "commons/notAutorized";
    }

    @GetMapping("/disabled-account-error")
    public String accountNotActiveError() {
        return "accountNotActiveError";
    }
}
