package ml.malikura.util;

import ml.malikura.dto.*;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.entity.RoleEmploye;
import ml.malikura.entity.TaskEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ValueMapper {

    public static ProjectEntity convertToEntity(NewProjectDTO projectDTO) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(null);
        projectEntity.setTitle(projectDTO.getTitle());
        projectEntity.setDescription(projectDTO.getDescription());
        projectEntity.setStartDate(projectDTO.getStartDate());
        projectEntity.setEndDate(projectDTO.getEndDate());
        projectEntity.setSpecificNote(projectDTO.getSpecificNote());
        projectEntity.setStatut(ProjectStatut.ATTENTE);
        projectEntity.setMembers(null);
        projectEntity.setTasks(null);
        return projectEntity;

    }

    public static EditProjectDTO convertToEditDTO(ProjectEntity projectEntity) {
        EditProjectDTO editProjectDTO = new EditProjectDTO();
        editProjectDTO.setId(projectEntity.getId());
        editProjectDTO.setTitle(projectEntity.getTitle());
        editProjectDTO.setDescription(projectEntity.getDescription());
        editProjectDTO.setStartDate(projectEntity.getStartDate());
        editProjectDTO.setEndDate(projectEntity.getEndDate());
        editProjectDTO.setStatut(String.valueOf(projectEntity.getStatut()));
        editProjectDTO.setSpecificNote(projectEntity.getSpecificNote());
        return editProjectDTO;

    }

    public static ProjectEntity convertToEntity(EditProjectDTO editProjectDTO) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(editProjectDTO.getId());
        projectEntity.setTitle(editProjectDTO.getTitle());
        projectEntity.setDescription(editProjectDTO.getDescription());
        projectEntity.setStartDate(editProjectDTO.getStartDate());
        projectEntity.setEndDate(editProjectDTO.getEndDate());
        projectEntity.setSpecificNote(editProjectDTO.getSpecificNote());
        projectEntity.setStatut(ProjectStatut.valueOf(editProjectDTO.getStatut()));
        // projectEntity.setMembers();
        // projectEntity.setTasks();
        return projectEntity;

    }

    public static TaskEntity convertToEntity(NewTaskDTO newTaskDTO) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(null);
        taskEntity.setTitle(newTaskDTO.getTitle());
        taskEntity.setDescription(newTaskDTO.getDescription());
        taskEntity.setPriority(TaskPriority.valueOf(newTaskDTO.getPriority()));
        taskEntity.setCriticality(TaskCriticality.valueOf(newTaskDTO.getCriticality()));
        taskEntity.setStatut(TaskStatut.ATTENTE);
        taskEntity.setState(TaskState.NON_SOUMIS);
        taskEntity.setCreationDate(LocalDateTime.now());
        taskEntity.setAffectationDate(newTaskDTO.getAffectationDate());
        taskEntity.setStartDate(newTaskDTO.getStartDate());
        taskEntity.setEndDate(newTaskDTO.getEndDate());
        taskEntity.setTaskEvaluation(0);
        taskEntity.setMark(null);
        taskEntity.setAuthor(null);
        taskEntity.setResponsable(null);
        taskEntity.setProject(null);
        return taskEntity;
    }

    public static EditTaskDTO convertToEditDTO(TaskEntity taskEntity) {
        EditTaskDTO editTaskDTO = new EditTaskDTO();
        editTaskDTO.setId(taskEntity.getId());
        editTaskDTO.setProjectId(taskEntity.getProject().getId());
        editTaskDTO.setTitle(taskEntity.getTitle());
        editTaskDTO.setDescription(taskEntity.getDescription());
        editTaskDTO.setStatut(String.valueOf(taskEntity.getStatut()));
        editTaskDTO.setPriority(String.valueOf(taskEntity.getPriority()));
        editTaskDTO.setCriticality(String.valueOf(taskEntity.getCriticality()));
        editTaskDTO.setState(String.valueOf(taskEntity.getState()));
        editTaskDTO.setStartDate(taskEntity.getStartDate());
        editTaskDTO.setEndDate(taskEntity.getEndDate());
        if (taskEntity.getResponsable() != null) {
            editTaskDTO.setResponsableId(taskEntity.getResponsable().getEmail());
        } else {
            editTaskDTO.setResponsableId(null);
        }

        return editTaskDTO;
    }

    public static TaskEntity convertToEntity(EditTaskDTO editTaskDTO) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(editTaskDTO.getId());
        taskEntity.setTitle(editTaskDTO.getTitle());
        taskEntity.setDescription(editTaskDTO.getDescription());
        taskEntity.setPriority(TaskPriority.valueOf(editTaskDTO.getPriority()));
        taskEntity.setCriticality(TaskCriticality.valueOf(editTaskDTO.getCriticality()));
        taskEntity.setStatut(TaskStatut.valueOf(editTaskDTO.getStatut()));
        taskEntity.setState(TaskState.valueOf(editTaskDTO.getState()));
        taskEntity.setStartDate(editTaskDTO.getStartDate());
        taskEntity.setEndDate(editTaskDTO.getEndDate());
        //taskEntity.setResponsable();
        return taskEntity;
    }

    public static EmployeEntity convertToEmploye(NewEmployeDTO newEmployeDTO) {
        EmployeEntity employeEntity = new EmployeEntity();
        employeEntity.setId(null);
        employeEntity.setName(newEmployeDTO.getName());
        employeEntity.setFirstname(newEmployeDTO.getFirstname());
        employeEntity.setEmail(newEmployeDTO.getEmail());
        employeEntity.setPassword(newEmployeDTO.getPassword());
        employeEntity.setRoles(null);
        employeEntity.setJobTitle(newEmployeDTO.getJobTitle());
        employeEntity.setAddress(newEmployeDTO.getAddress());
        employeEntity.setTelephone(newEmployeDTO.getTelephone());
        employeEntity.setAccountEnabled(false);
        employeEntity.setTasksToDo(null);
        employeEntity.setTasksCreated(null);
        return employeEntity;
    }

    public static EmployeEntity convertToEmploye(NewCollaborateurDTO newCollaborateurDTO) {
        EmployeEntity employeEntity = new EmployeEntity();
        employeEntity.setId(null);
        employeEntity.setName(newCollaborateurDTO.getName());
        employeEntity.setFirstname(newCollaborateurDTO.getFirstname());
        employeEntity.setEmail(newCollaborateurDTO.getEmail());
        employeEntity.setPassword(newCollaborateurDTO.getPassword());
        employeEntity.setJobTitle(newCollaborateurDTO.getJobTitle());
        employeEntity.setAddress(newCollaborateurDTO.getAddress());
        employeEntity.setTelephone(newCollaborateurDTO.getTelephone());
        employeEntity.setTasksToDo(null);
        employeEntity.setTasksCreated(null);
        String role = newCollaborateurDTO.getRole();
        List<RoleEmploye> employeRoles = new ArrayList<>();
        if (role.equals("ADMIN")) {
            employeRoles.add(new RoleEmploye("USER"));
            employeRoles.add(new RoleEmploye("MANAGER"));
            employeRoles.add(new RoleEmploye("ADMIN"));
            employeEntity.setRoles((employeRoles));
        } else if (role.equals("MANAGER")) {
            employeRoles.add(new RoleEmploye("USER"));
            employeRoles.add(new RoleEmploye("MANAGER"));
            employeEntity.setRoles((employeRoles));
        } else {
            employeRoles.add(new RoleEmploye("USER"));
            employeEntity.setRoles((employeRoles));
        }
        //employeEntity.setAccountEnabled(false);
        String etatCompte = newCollaborateurDTO.getEtatCompte();
        employeEntity.setAccountEnabled(etatCompte.equals("ACTIF"));
        return employeEntity;
    }

    public static EditCollaborateurDTO convertToCollaborateurEditDTO(EmployeEntity employe) {
        EditCollaborateurDTO editCollaborateurDTO = new EditCollaborateurDTO();
        editCollaborateurDTO.setFirstname(employe.getFirstname());
        editCollaborateurDTO.setName(employe.getName());
        editCollaborateurDTO.setEmail(employe.getEmail());
        editCollaborateurDTO.setJobTitle(employe.getJobTitle());
        editCollaborateurDTO.setTelephone(employe.getTelephone());
        editCollaborateurDTO.setAddress(employe.getAddress());
        if (employe.getRoles().stream().anyMatch(roleEmploye -> roleEmploye.getRole().equals("ADMIN"))) {
            editCollaborateurDTO.setRole("ADMIN");
        } else if (employe.getRoles().stream().anyMatch(roleEmploye -> roleEmploye.getRole().equals("MANAGER"))) {
            editCollaborateurDTO.setRole("MANAGER");
        } else {
            editCollaborateurDTO.setRole("USER");
        }

        if (employe.getAccountEnabled()) {
            editCollaborateurDTO.setEtatCompte("ACTIF");
        } else {
            editCollaborateurDTO.setEtatCompte("INACTIF");
        }
        return editCollaborateurDTO;
    }
}
