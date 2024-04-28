package ml.malikura.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.AccountActivationDTO;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.TaskEntity;
import ml.malikura.service.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String getNewEmployeForm() {

        return "newEmployeForm";
    }

    @GetMapping("/viewEmployee")
    @PreAuthorize("hasRole('MANAGER')")
    public String viewEmploye(@RequestParam(value = "employeeId") String email) {

        return "employeTemplates/viewEmploye";
    }

    public String getEditEmployeForm() {

        return "0";
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
        for (String role : accountActivationDTO.getRoles()) {
            if (role.equals("ADMIN")) {
                myRoles = Arrays.asList("USER", "MANAGER", "ADMIN");
            } else if (role.equals("MANAGER")) {
                myRoles = Arrays.asList("USER", "MANAGER");
            } else {
                myRoles = Arrays.asList("USER");
            }
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
}
