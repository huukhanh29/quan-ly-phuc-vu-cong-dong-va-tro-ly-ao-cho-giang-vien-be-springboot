package com.quanly.hoatdongcongdong.sercurity;

import com.quanly.hoatdongcongdong.entity.TaiKhoan;
import com.quanly.hoatdongcongdong.sercurity.jwt.AuthEntryPointJwt;
import com.quanly.hoatdongcongdong.sercurity.jwt.AuthTokenFilter;
import com.quanly.hoatdongcongdong.sercurity.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity

public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/tai-khoan/**").permitAll()
                                .requestMatchers("/cau-hoi/**").hasAnyAuthority("QuanTriVien","SinhVien")
                                .requestMatchers("/lich-su/**").hasAnyAuthority("QuanTriVien","SinhVien")
                                .requestMatchers("/phan-hoi/**").hasAnyAuthority("QuanTriVien","SinhVien")
                                .requestMatchers("/thong-bao/**").hasAnyAuthority("QuanTriVien","SinhVien", "GiangVien")
                                .requestMatchers("/chuc-danh/**").hasAnyAuthority("QuanTriVien", "GiangVien")
                                .requestMatchers("/loai-hoat-dong/**").hasAnyAuthority("QuanTriVien", "GiangVien")
                                .requestMatchers("/tai-khoan/**").hasAnyAuthority("QuanTriVien", "GiangVien", "SinhVien")
                                .requestMatchers("/hoat-dong/**").hasAnyAuthority("QuanTriVien", "GiangVien")
                                .anyRequest().authenticated()
                )
                        .cors(Customizer.withDefaults());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
