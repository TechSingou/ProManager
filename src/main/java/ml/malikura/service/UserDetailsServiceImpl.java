package ml.malikura.service;


import lombok.AllArgsConstructor;
import ml.malikura.entity.EmployeEntity;
import ml.malikura.entity.RoleEmploye;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private EmployeService employeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EmployeEntity employeEntity = employeService.loadEmployeByEmail(username);
        if (employeEntity == null)
            throw new UsernameNotFoundException(String.format("L'utilisateur %s non trouvé", username));
        if (!employeEntity.getAccountEnabled())
            throw new DisabledException("Ce compte n'est pas activé, merci de contacter l'admin");

        String[] roles = employeEntity.getRoles().stream().map(RoleEmploye::getRole).toArray(String[]::new);

        UserDetails userDetails = User.withUsername(employeEntity.getEmail())
                .password(employeEntity.getPassword())
                .roles(roles)
                .build();
        return userDetails;
    }
}
