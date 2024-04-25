package ml.malikura.config;

import ml.malikura.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private UserDetailsServiceImpl userDetailsServiceImpl;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin().loginPage("/login")
                .defaultSuccessUrl("/index", true).permitAll();
        //httpSecurity.authorizeHttpRequests().requestMatchers("/formInscription", "/inscriptionProcess", "/inscriptionConfirmation").permitAll();
        httpSecurity.authorizeHttpRequests().anyRequest().authenticated();
        httpSecurity.exceptionHandling().accessDeniedPage("/notAutorized");
        httpSecurity.logout().logoutUrl("/logout");
        httpSecurity.userDetailsService(userDetailsServiceImpl);
        return httpSecurity.build();
    }

    @Bean
    WebSecurityCustomizer configureWebSecurity() {
        return (web) -> web.ignoring().requestMatchers("/img/**", "/webjars/**", "/h2-console/**", "/formInscription", "/inscriptionProcess", "/inscriptionConfirmation");
    }

}
