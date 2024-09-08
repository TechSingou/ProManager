package ml.malikura.service;

import jakarta.validation.Valid;
import ml.malikura.dto.EditCollaborateurDTO;
import ml.malikura.dto.NewCollaborateurDTO;
import ml.malikura.dto.NewEmployeDTO;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.RoleEmploye;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeService {

    EmployeEntity addNewEmploye(NewEmployeDTO newEmployeDTO);

    RoleEmploye addNewRole(String role);

    void addRoleToEmploye(String email, String role);

    void removeRoleFromUser(String email, String role);

    EmployeEntity loadEmployeByEmail(String email);

    void enabledAccount(String email, List<String> roles);

    void disabledAccount(String email);

    Page<EmployeEntity> getEmployeesList(String keyword, int page, int size);

    List<EmployeEntity> getAll();

    EmployeEntity addCollaborateurByAdmin(@Valid NewCollaborateurDTO newCollaborateurDTO);

    void editCollaborateurByAdmin(EditCollaborateurDTO editCollaborateurDTO);

    void deleteByIdByAdmin(Long employeId);
}
