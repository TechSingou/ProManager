package ml.malikura.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.AccountActivationDTO;
import ml.malikura.dto.EditCollaborateurDTO;
import ml.malikura.dto.NewCollaborateurDTO;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.service.EmployeService;
import ml.malikura.util.MyUtils;
import ml.malikura.util.ValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
public class EmployeController {

    private EmployeService employeService;

    @Autowired
    public EmployeController(EmployeService employeService) {
        this.employeService = employeService;
    }

    @GetMapping("/employes")
    @PreAuthorize("hasRole('MANAGER')")
    public String employeList(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size) {
        log.info("ProjectController::employeList execution started");

        Page<EmployeEntity> employeeListPage = this.employeService.getEmployeesList(keyword, page, size);
        model.addAttribute("employeeListPage", employeeListPage);
        model.addAttribute("currentKeyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("pages", new int[employeeListPage.getTotalPages()]);

        log.info("ProjectController::employeList execution ended.");

        return "employeTemplates/employeList";
    }

    @GetMapping("/newEmployeeForm")
    @PreAuthorize("hasRole('ADMIN')")
    public String getNewEmployeForm(Model model) {
        model.addAttribute("newCollaborateurDTO", new NewCollaborateurDTO());
        return "employeTemplates/newEmployeForm";
    }

    @PostMapping("/addNewEmployeeByAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public String addNewEmployeeByAdmin(Model model, @Valid NewCollaborateurDTO newCollaborateurDTO, BindingResult bindingResult) {
        // Check if password and passwordConfirmation match
        if (!newCollaborateurDTO.getPassword().equals(newCollaborateurDTO.getPasswordConfirmation())) {
            bindingResult.rejectValue("passwordConfirmation", "error.password.notMatch", "Les mots de passe ne correspondent pas.");
        }
        if (bindingResult.hasErrors()) {
            return "employeTemplates/newEmployeForm";
        }
        EmployeEntity savedEmployee = this.employeService.addCollaborateurByAdmin(newCollaborateurDTO);
        return "redirect:/viewEmployee?employeeId=" + savedEmployee.getEmail();
    }

    @GetMapping("/viewEmployee")
    @PreAuthorize("hasRole('MANAGER')")
    public String viewEmploye(@RequestParam(value = "employeeId") String email, Model model) {
        EmployeEntity employee = employeService.loadEmployeByEmail(email);
        String roleEmploye = null;
        if (employee.getRoles().stream().anyMatch(r -> r.getRole().equals("ADMIN"))) {
            roleEmploye = "ADMIN";
        } else if (employee.getRoles().stream().anyMatch(r -> r.getRole().equals("MANAGER"))) {
            roleEmploye = "MANAGER";
        } else {
            roleEmploye = "USER";
        }
        model.addAttribute("employee", employee);
        model.addAttribute("moyenne", MyUtils.getMoyenne(employee.getTasksToDo()));
        model.addAttribute("roleEmploye", roleEmploye);
        return "employeTemplates/viewEmploye";
    }

    @GetMapping("/getEditEmployeForm")
    @PreAuthorize("hasRole('ADMIN')")
    public String getEditEmployeForm(Model model, @RequestParam(value = "employeId") String employeEmail) {
        EmployeEntity employe = this.employeService.loadEmployeByEmail(employeEmail);
        EditCollaborateurDTO editCollaborateurDTO = ValueMapper.convertToCollaborateurEditDTO(employe);
        model.addAttribute("editCollaborateurDTO", editCollaborateurDTO);
        return "employeTemplates/editEmployeForm";
    }

    @PostMapping("/editEmployeeByAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public String editEmployeByAdmin(Model model, @Valid EditCollaborateurDTO editCollaborateurDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employeId", editCollaborateurDTO.getEmail());
            return "employeTemplates/editEmployeForm";
        }
        this.employeService.editCollaborateurByAdmin(editCollaborateurDTO);
        return "redirect:/viewEmployee?employeeId=" + editCollaborateurDTO.getEmail();
    }

    @GetMapping("/getActivateAccountForm")
    @PreAuthorize("hasRole('ADMIN')")
    public String accountActivationForm(Model model, @RequestParam(value = "email") String email,
                                        @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "size", defaultValue = "5") int size,
                                        @RequestParam(value = "name") String name, @RequestParam(value = "firstname") String firstname) {
        model.addAttribute("email", email);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("name", name);
        model.addAttribute("firstname", firstname);
        model.addAttribute("accountActivationDTO", new AccountActivationDTO());
        return "employeTemplates/activateAccount";
    }

    @PostMapping("/employeeAccountActivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String activateAccount(Model model, @RequestParam(value = "email") String email,
                                  @RequestParam(value = "name") String name,
                                  @RequestParam(value = "firstname") String firstname,
                                  @Valid AccountActivationDTO accountActivationDTO, BindingResult bindingResult,
                                  @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("email", email);
            model.addAttribute("page", page);
            model.addAttribute("keyword", keyword);
            model.addAttribute("name", name);
            model.addAttribute("firstname", firstname);
            return "employeTemplates/activateAccount";

        }
        List<String> myRoles = new ArrayList<>();
        if (accountActivationDTO.getRole().equals("ADMIN")) {
            myRoles = Arrays.asList("USER", "MANAGER", "ADMIN");
        } else if (accountActivationDTO.getRole().equals("MANAGER")) {
            myRoles = Arrays.asList("USER", "MANAGER");
        } else {
            myRoles = Arrays.asList("USER");
        }
        employeService.enabledAccount(email, myRoles);
        return "redirect:/employes?keyword=" + keyword + "&page=" + page;
    }

    @GetMapping("/employeeAccountDisactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String employeeAccountDisactivateForm(@RequestParam(value = "email") String email,
                                                 @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "5") int size) {
        employeService.disabledAccount(email);
        return "redirect:/employes?keyword=" + keyword + "&page=" + page;
    }

    @GetMapping("/deleteEmployee")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEmployeeByAdmin(@RequestParam(value = "employeeId") Long employeId){
        this.employeService.deleteByIdByAdmin(employeId);
        return "redirect:/employes";
    }
}
