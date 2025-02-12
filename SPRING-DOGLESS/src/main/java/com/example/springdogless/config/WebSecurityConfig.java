

package com.example.springdogless.config;

import com.example.springdogless.Repository.UsuarioRepository;
import com.example.springdogless.entity.Usuario;
import jakarta.servlet.http.HttpSession;
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
import java.util.Optional;

@Configuration
public class WebSecurityConfig {

    final UsuarioRepository usuarioRepository;
    final DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource, UsuarioRepository usuarioRepository) {
        this.dataSource = dataSource;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {



        http.formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/processLogin")
                //.usernameParameter("correo")
                //.passwordParameter("contrasenia")
                .successHandler((request, response, authentication) -> {

                    DefaultSavedRequest defaultSavedRequest =
                            (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                    HttpSession session = request.getSession();

                    Usuario usuario = usuarioRepository.findByEmail(authentication.getName());
                    if (usuario == null) {
                         usuario = usuarioRepository.findByCodigoaduana(authentication.getName());
                    }

                    session.setAttribute("usuario", usuario);
                    // Guardar el idzona en la sesión también
                    session.setAttribute("idzona", usuario.getZona().getIdzonas());

                    //Para el parcial
                    //si vengo por url -> defaultSR existe
                    if (defaultSavedRequest != null) {
                        String targetURl = defaultSavedRequest.getRequestURL();
                        new DefaultRedirectStrategy().sendRedirect(request, response, targetURl);
                    } else { //estoy viniendo del botón de login
                        String rol = "";
                        for (GrantedAuthority role : authentication.getAuthorities()) {
                            rol = role.getAuthority();
                            break;
                        }

                        switch (rol) {
                            case "Superadmin":
                                response.sendRedirect("/admin");
                                break;
                            case "Adminzonal":
                                response.sendRedirect("/zonal");
                                break;
                            case "Agente":
                                response.sendRedirect("/agente");
                                break;
                            case "Usuario":
                                response.sendRedirect("/usuario");
                                break;
                            default:
                                response.sendRedirect("/default"); // Puedes cambiar esto según lo que necesites
                                break;
                        }
                    }
                });

        http.csrf(csrf -> csrf.disable());

        //Dogless: usuario:agomez@gmail.com, Pass=1111 y los demás de manera análoga
        http.authorizeHttpRequests()
                .requestMatchers("/usuario", "/usuario/**").hasAnyAuthority("Superadmin", "Usuario")
                .requestMatchers("/agente", "/agente/**").hasAnyAuthority("Superadmin", "Agente")
                .requestMatchers("/zonal", "/zonal/**").hasAnyAuthority("Superadmin", "Adminzonal")
                .requestMatchers("/admin", "/admin/**").hasAuthority("Superadmin")
                .requestMatchers("/api/chat/**").permitAll()
                //Ejemplos
                //.requestMatchers("/zonal", "/zonal/**").hasAuthority("admin")//El admin
                //.requestMatchers("/product", "/product/**").hasAuthority("hr")//El hr

                .anyRequest().permitAll();



        http.logout().logoutSuccessUrl("/");

        return http.build();

    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        //String sql1 = "SELECT email, pwd, borrado FROM usuarios WHERE email = ?";
        //String sql2 = "SELECT u.email, r.nombre FROM usuarios u "
                //+ "INNER JOIN roles r ON (u.idroles = r.idroles) "
                //+ "WHERE u.email = ? and u.borrado = 1";
        String sql1 = "SELECT COALESCE(codigoaduana, email) as email, pwd, borrado FROM usuarios WHERE ? IN (email, codigoaduana)";

        String sql2 = "SELECT COALESCE(u.codigoaduana, u.email) as identificador, r.nombre FROM usuarios u INNER JOIN roles r ON u.idroles = r.idroles WHERE ? IN (u.email, u.codigoaduana) AND u.borrado = 1";
        users.setUsersByUsernameQuery(sql1);
        users.setAuthoritiesByUsernameQuery(sql2);
        return users;
    }


}