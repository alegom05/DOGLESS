

package com.example.springdogless.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig {

    final DataSource dataSource;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        String sql1 = "SELECT email, pwd, borrado FROM usuarios WHERE email = ?";
        String sql2 = "SELECT u.email, r.nombre FROM usuarios u "
                + "INNER JOIN roles r ON (u.idroles = r.idroles) "
                + "WHERE u.email = ? and u.borrado = 1";

        users.setUsersByUsernameQuery(sql1);
        users.setAuthoritiesByUsernameQuery(sql2);
        return users;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin();

        http.authorizeHttpRequests()
                .requestMatchers("/admin", "/admin/**").authenticated()
                .requestMatchers("/zonal", "/zonal/**").authenticated()
                .anyRequest().permitAll();


    /*

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                    .loginPage("/loginForm")
                    .loginProcessingUrl("/processLogin")
                    //.usernameParameter("email") //Si no vas a usar password
                    //.passwordParameter("contrasenia") //Si no vas a usar username

                    .successHandler((request, response, authentication) -> {

                        DefaultSavedRequest defaultSavedRequest =
                                (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                        if (defaultSavedRequest != null) {
                            String targetURL = defaultSavedRequest.getRedirectUrl();
                            redirectStrategy.sendRedirect(request, response, targetURL);
                        } else {
                            String roles = "";
                            for (GrantedAuthority role : authentication.getAuthorities()) {
                                roles = role.getAuthority();
                                break;
                            }
                            if (roles.equals("Superadmin")) {
                                response.sendRedirect("/admin");
                            } else {
                                response.sendRedirect("/zonal");
                            }
                        }
                    });

        http.logout();

        //Dogless: usuario:agomez@gmail.com, Pass=1111 y los demás de manera análoga
        http.authorizeHttpRequests()
                .requestMatchers("/usuario", "/usuario/**").hasAnyAuthority("Superadmin", "Adminzonal","Agente","Usuario")
                .requestMatchers("/agente", "/agente/**").hasAnyAuthority("Superadmin", "Adminzonal","Agente")
                .requestMatchers("/zonal", "/zonal/**").hasAnyAuthority("Superadmin", "Adminzonal")
                .requestMatchers("/admin", "/admin/**").hasAuthority("Superadmin")
                //Ejemplos
                //.requestMatchers("/zonal", "/zonal/**").hasAuthority("admin")//El admin
                //.requestMatchers("/product", "/product/**").hasAuthority("hr")//El hr

                .anyRequest().permitAll();

        return http.build();

    }

     */
        return http.build();

    }
}
