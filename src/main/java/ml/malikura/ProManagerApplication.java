package ml.malikura;

import ml.malikura.dto.NewEmployeDTO;
import ml.malikura.entity.ProjectEntity;
import ml.malikura.repository.ProjectRepository;
import ml.malikura.service.EmployeService;
import ml.malikura.util.ProjectStatut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class ProManagerApplication implements CommandLineRunner {

    @Autowired
    private ProjectRepository projectRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProManagerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        projectRepository.save(new ProjectEntity(null, "E-sugu", "An example 2-level sidebar with collasible menu items. The menu functions like an accordion where only a single",
                LocalDateTime.now(), LocalDateTime.now(), "test note", ProjectStatut.ATTENTE,
                null, null));

        projectRepository.save(new ProjectEntity(null, "ProManager", "An example 2-level sidebar with collasible menu items. The menu functions like an accordion where only a single",
                LocalDateTime.now(), LocalDateTime.now(), "test note", ProjectStatut.EN_COURS,
                null, null));

        projectRepository.save(new ProjectEntity(null, "E-dôniya", "An example 2-level sidebar with collasible menu items. The menu functions like an accordion where only a single",
                LocalDateTime.now(), LocalDateTime.now(), "test note", ProjectStatut.ATTENTE,
                null, null));

        projectRepository.save(new ProjectEntity(null, "MalibaTrans", "An example 2-level sidebar with collasible menu items. The menu functions like an accordion where only a single",
                LocalDateTime.now(), LocalDateTime.now(), "test note", ProjectStatut.ATTENTE,
                null, null));

        projectRepository.save(new ProjectEntity(null, "E-sugu1", "An example 2-level sidebar with collasible menu items. The menu functions like an accordion where only a single",
                LocalDateTime.now(), LocalDateTime.now(), "test note", ProjectStatut.ATTENTE,
                null, null));

        projectRepository.save(new ProjectEntity(null, "ProManager1", "An example 2-level sidebar with collasible menu items. The menu functions like an accordion where only a single",
                LocalDateTime.now(), LocalDateTime.now(), "test note", ProjectStatut.EN_COURS,
                null, null));

        projectRepository.save(new ProjectEntity(null, "E-dôniya1", "An example 2-level sidebar with collasible menu items. The menu functions like an accordion where only a single",
                LocalDateTime.now(), LocalDateTime.now(), "test note", ProjectStatut.ATTENTE,
                null, null));

        projectRepository.save(new ProjectEntity(null, "MalibaTrans1", "An example 2-level sidebar with collasible menu items. The menu functions like an accordion where only a single",
                LocalDateTime.now(), LocalDateTime.now(), "test note", ProjectStatut.ATTENTE,
                null, null));
    }


    @Bean
    CommandLineRunner commandLineRunnerDefaultEmployees(EmployeService employeService) {
        return args -> {
            // Roles
            employeService.addNewRole("USER");
            employeService.addNewRole("MANAGER");
            employeService.addNewRole("ADMIN");

            // Default Users
            NewEmployeDTO employe1 = NewEmployeDTO.builder()
                    .name("user1")
                    .firstname("user1")
                    .email("user1@gmail.com")
                    .password("1234")
                    .jobTitle("Developer")
                    .address("Martil, Morocco")
                    .telephone("0705123356")
                    .build();
            NewEmployeDTO employe2 = NewEmployeDTO.builder()
                    .name("user2")
                    .firstname("user2")
                    .email("user2@gmail.com")
                    .password("1234")
                    .jobTitle("Manager")
                    .address("Martil, Morocco")
                    .telephone("0705124456")
                    .build();
            NewEmployeDTO employe3 = NewEmployeDTO.builder()
                    .name("user3")
                    .firstname("user3")
                    .email("user3@gmail.com")
                    .password("1234")
                    .jobTitle("Administrateur")
                    .address("Martil, Morocco")
                    .telephone("0705122299")
                    .build();
            NewEmployeDTO employe4 = NewEmployeDTO.builder()
                    .name("user4")
                    .firstname("user4")
                    .email("user4@gmail.com")
                    .password("1234")
                    .jobTitle("Developer")
                    .address("Martil, Morocco")
                    .telephone("0705122299")
                    .build();
            employeService.addNewEmploye(employe1);
            employeService.addNewEmploye(employe2);
            employeService.addNewEmploye(employe3);
            employeService.addNewEmploye(employe4);

            // Enable default users
            employeService.enabledAccount("user1@gmail.com", Arrays.asList("USER"));
            employeService.enabledAccount("user2@gmail.com", Arrays.asList("USER", "MANAGER"));
            employeService.enabledAccount("user3@gmail.com", Arrays.asList("USER", "MANAGER", "ADMIN"));

        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
