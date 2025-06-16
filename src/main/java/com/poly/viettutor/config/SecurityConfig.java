package com.poly.viettutor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // mã hóa mật khẩu Bcrypt
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()); // Tắt CSRF (Cross-Site Request Forgery)
        http.authorizeHttpRequests(auth -> auth // Cấu hình phân quyền cho các request
                // .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN mới được truy cập
                .requestMatchers("/user/**").authenticated() // yêu cầu đăng nhập
                .requestMatchers("/admin/**").permitAll() // được phép truy cập không cần đăng nhập
                .anyRequest().permitAll()); // Tất cả các request khác đều được phép truy cập
        http.formLogin(form -> form.loginPage("/login").permitAll());
        http.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
