package ml.malikura.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            log.info("ProjectServiceImpl:getProjectList execution started.");
            employeEntities = employeRepository.findByFirstnameContainingIgnoreCase(keyword, PageRequest.of(page, size));
            if (employeEntities.isEmpty()) {
                employeEntities = Page.empty();
            }
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving projects from database , Exception message {}", ex.getMessage());
            throw new ProjectServiceBusinessException("Exception occurred while fetch all projects from Database");
        }
        log.info("ProjectServiceImpl:getProducts execution ended.");
        return employeEntities;
    }

    @Override
    public List<EmployeEntity> getAll() {
        return employeRepository.findAll();
    }
}
