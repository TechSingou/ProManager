package ml.malikura.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.malikura.dto.EditCollaborateurDTO;
import ml.malikura.dto.NewCollaborateurDTO;
import ml.malikura.dto.NewEmployeDTO;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.RoleEmploye;
import ml.malikura.exception.ProjectServiceBusinessException;
import ml.malikura.repository.EmployeRepository;
import ml.malikura.repository.RoleEmployeRepository;
import ml.malikura.util.ValueMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class EmployeServiceImpl implements EmployeService {
    private EmployeRepository employeRepository;
    private RoleEmployeRepository roleEmployeRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public EmployeEntity addNewEmploye(NewEmployeDTO newEmployeDTO) {
        EmployeEntity employeEntity = employeRepository.findByEmail(newEmployeDTO.getEmail());
        if (employeEntity != null)
            throw new ProjectServiceBusinessException("Cet utilisateur existe déjà");
        //Encode password
        newEmployeDTO.setPassword(passwordEncoder.encode(newEmployeDTO.getPassword()));
        employeEntity = ValueMapper.convertToEmploye(newEmployeDTO);
        return employeRepository.save(employeEntity);
    }

    @Override
    public RoleEmploye addNewRole(String role) {
        RoleEmploye roleEmploye = roleEmployeRepository.findById(role).orElse(null);
        if (roleEmploye != null)
            throw new ProjectServiceBusinessException("Ce role existe déjà");
        roleEmploye = RoleEmploye.builder().role(role).build();
        return roleEmployeRepository.save(roleEmploye);
    }

    @Override
    public void addRoleToEmploye(String email, String role) {
        EmployeEntity employeEntity = employeRepository.findByEmail(email);
        RoleEmploye roleEmploye = roleEmployeRepository.findById(role).orElse(null);
        if (employeEntity == null)
            throw new ProjectServiceBusinessException("Cet utilisateur n'existe pas");
        if (roleEmploye == null)
            throw new ProjectServiceBusinessException("Ce role n'existe pas");
        employeEntity.getRoles().add(roleEmploye);
        employeRepository.save(employeEntity); //implicitly done by @Transactional
    }

    @Override
    public void removeRoleFromUser(String email, String role) {
        EmployeEntity employeEntity = employeRepository.findByEmail(email);
        RoleEmploye roleEmploye = roleEmployeRepository.findById(role).orElse(null);
        if (employeEntity == null)
            throw new ProjectServiceBusinessException("Cet utilisateur n'existe pas");
        if (roleEmploye == null)
            throw new ProjectServiceBusinessException("Ce role n'existe pas");
        employeEntity.getRoles().remove(roleEmploye);
        employeRepository.save(employeEntity); //implicitly done by @Transactional
    }

    @Override
    public EmployeEntity loadEmployeByEmail(String email) {
        return employeRepository.findByEmail(email);
    }

    @Override
    public void enabledAccount(String email, List<String> roles) {
        log.info("EmployeServiceImpl:enabledAccount execution started.");
        EmployeEntity employeEntity = employeRepository.findByEmail(email);
        if (employeEntity == null)
            throw new ProjectServiceBusinessException("Cet utilisateur n'existe pas.");

        List<RoleEmploye> roleEmployes = new ArrayList<>();
        RoleEmploye roleEmploye = null;
        for (String role : roles) {
            roleEmploye = roleEmployeRepository.findById(role).orElseThrow(
                    () -> new ProjectServiceBusinessException("Le role " + role + " n'existe pas.")
            );
            roleEmployes.add(roleEmploye);
        }
        employeEntity.setAccountEnabled(true);
        employeEntity.getRoles().addAll(roleEmployes);
        employeRepository.save(employeEntity);
        log.info("EmployeServiceImpl:enabledAccount execution ended.");
    }

    @Override
    public void disabledAccount(String email) {
        EmployeEntity employeEntity = employeRepository.findByEmail(email);
        if (employeEntity == null)
            throw new ProjectServiceBusinessException("Cet utilisateur n'existe pas");
        employeEntity.setAccountEnabled(false);
        employeEntity.getRoles().clear();
    }

    @Override
    public Page<EmployeEntity> getEmployeesList(String keyword, int page, int size) {
        Page<EmployeEntity> employeEntities = null;
        try {
            log.info("EmployeServiceImpl:getEmployeesList execution started.");
            employeEntities = employeRepository.findByFirstnameContainingIgnoreCase(keyword, PageRequest.of(page, size));
            if (employeEntities.isEmpty()) {
                employeEntities = Page.empty();
            }
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving employees from database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException("Exception occurred while fetch all projects from Database");
        }
        log.info("EmployeServiceImpl:getEmployeesList execution ended.");
        return employeEntities;
    }

    @Override
    public List<EmployeEntity> getAll() {
        return employeRepository.findAll();
    }

    @Override
    public EmployeEntity addCollaborateurByAdmin(NewCollaborateurDTO newCollaborateurDTO) {
        EmployeEntity employeEntity = null;
        try {
            log.info("EmployeServiceImpl:addCollaborateurByAdmin execution started.");
            employeEntity = employeRepository.findByEmail(newCollaborateurDTO.getEmail());
            if (employeEntity != null)
                throw new ProjectServiceBusinessException("Ce collaborateur existe déjà");
            //Encode password
            newCollaborateurDTO.setPassword(passwordEncoder.encode(newCollaborateurDTO.getPassword()));
            employeEntity = ValueMapper.convertToEmploye(newCollaborateurDTO);
        } catch (Exception ex) {
            log.error("Exception occurred during the operation");
            throw new ProjectServiceBusinessException("Exception occurred during the operation");
        }
        log.info("EmployeServiceImpl:addCollaborateurByAdmin execution ended.");
        return employeRepository.save(employeEntity);
    }

    @Override
    public void editCollaborateurByAdmin(EditCollaborateurDTO editCollaborateurDTO) {
        EmployeEntity employeToUpdate = null;
        try {
            log.info("EmployeServiceImpl:editCollaborateurByAdmin execution started.");
            employeToUpdate = employeRepository.findByEmail(editCollaborateurDTO.getEmail());
            if (employeToUpdate == null)
                throw new ProjectServiceBusinessException("Ce collaborateur n'existe pas!");
            employeToUpdate.setName(editCollaborateurDTO.getName());
            employeToUpdate.setFirstname(editCollaborateurDTO.getFirstname());
            employeToUpdate.setJobTitle(editCollaborateurDTO.getJobTitle());
            employeToUpdate.setTelephone(editCollaborateurDTO.getTelephone());
            employeToUpdate.setAddress(editCollaborateurDTO.getAddress());
            if (editCollaborateurDTO.getRole().equals("ADMIN")) {
                employeToUpdate.getRoles().add(new RoleEmploye("USER"));
                employeToUpdate.getRoles().add(new RoleEmploye("MANAGER"));
                employeToUpdate.getRoles().add(new RoleEmploye("ADMIN"));
            } else if (editCollaborateurDTO.getRole().equals("MANAGER")) {
                employeToUpdate.getRoles().add(new RoleEmploye("USER"));
                employeToUpdate.getRoles().add(new RoleEmploye("MANAGER"));
            } else {
                employeToUpdate.getRoles().add(new RoleEmploye("USER"));
            }

            String etatCompte = editCollaborateurDTO.getEtatCompte();
            employeToUpdate.setAccountEnabled(etatCompte.equals("ACTIF"));
            this.employeRepository.save(employeToUpdate);
        } catch (Exception ex) {
            log.error("Exception occurred during the operation");
            throw new ProjectServiceBusinessException("Exception occurred during the operation");
        }
        log.info("EmployeServiceImpl:addCollaborateurByAdmin execution ended.");
    }

    @Override
    public void deleteByIdByAdmin(Long employeId) {
        try{
            log.info("EmployeServiceImpl:deleteByIdByAdmin execution started.");
            this.employeRepository.deleteById(employeId);
        }catch (Exception ex){
            log.error("Exception occurred during this operation.");
            throw new ProjectServiceBusinessException("Exception occurred during this operation.");
        }
        log.info("EmployeServiceImpl:deleteByIdByAdmin execution started.");
    }
}
