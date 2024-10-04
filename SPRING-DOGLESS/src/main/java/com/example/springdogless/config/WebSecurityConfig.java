/*

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
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource){
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        String sql1 = "SELECT email, pwd, activo FROM usuario WHERE email = ?";
        String sql2 = "SELECT u.email, r.nombre FROM usuario u "
                + "INNER JOIN rol r ON (u.idrol = r.idrol) "
                + "WHERE u.email = ? and u.activo = 1";

        users.setUsersByUsernameQuery(sql1);
        users.setAuthoritiesByUsernameQuery(sql2);
        return users;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                    .loginPage("/loginForm")
                    .loginProcessingUrl("/processLogin")
                    .usernameParameter("email") //Si no vas a usar password
                    .passwordParameter("contrasenia") //Si no vas a usar username
                    .successHandler((request, response, authentication) -> {

                        DefaultSavedRequest defaultSavedRequest =
                                (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                        if (defaultSavedRequest != null) {
                            String targetURL = defaultSavedRequest.getRedirectUrl();
                            redirectStrategy.sendRedirect(request, response, targetURL);
                        } else {
                            String rol = "";
                            for (GrantedAuthority role : authentication.getAuthorities()) {
                                rol = role.getAuthority();
                                break;
                            }
                            if (rol.equals("admin")) {
                                response.sendRedirect("/shipper");
                            } else {
                                response.sendRedirect("/employee");
                            }
                        }
                    });

        http.logout();

        //Pass: 1234, Alejandro: 1111

        http.authorizeHttpRequests()
                .requestMatchers("/employee", "/employee/**").hasAnyAuthority("admin", "logistica")
                .requestMatchers("/shipper", "/shipper/**").hasAuthority("admin")
                .requestMatchers("/product", "/product/**").hasAuthority("hr")

                .anyRequest().permitAll();

        return http.build();
    }


}

*/
