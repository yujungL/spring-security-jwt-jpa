package com.web.my.config;

import com.web.my.common.bean.Role;
import com.web.my.common.jwt.JwtAccessDeniedHandler;
import com.web.my.common.jwt.JwtAuthenticationEntryPoint;
import com.web.my.common.jwt.JwtAuthenticationFilter;
import com.web.my.common.jwt.JwtTokenProvider;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration      //configuration : 빈 관리
@EnableWebSecurity //시큐리티 활성화
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
                //securedEnabled = @Secured 어노테이션 사용가능 여부 prePostEnabled = @PreAuthorize 어노테이션 사용가능 여부.
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrfConfig)
                        -> csrfConfig.disable()
                )//csrf(Cross site Request forgery) 설정을 disable
                .cors((cors)
                        -> cors.configurationSource(corsConfigurationSource())
                )// cors 설정 추가
                .sessionManagement((sessionManage)
                    -> sessionManage.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )// 세션 안씀
                .headers((headerConfig) ->
                        headerConfig
                                .frameOptions(frameOptionsConfig -> frameOptionsConfig.disable())
                )
                .formLogin((formLogin)
                        -> formLogin.disable()
                )// form login 안씀
                .httpBasic((httpBasic)
                        -> httpBasic.disable()
                ).exceptionHandling((exceptionHandle) ->
                        exceptionHandle
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) //인증예외 처리
                                .accessDeniedHandler(new JwtAccessDeniedHandler())  //접근권한예외 처리
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                                .requestMatchers("/**").permitAll() //메인화면과 로그인 및 회원가입 화면은 권한에 상관없이 접근할 수 있어야 하기에 permitAll로 모든 접근을 허용
                                .requestMatchers("/api/member/**").hasAnyRole("USER", "ADMIN")//경로별 권한설정
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://172.30.40.39:8080");
        configuration.addAllowedOrigin("http://172.30.40.39:9792");
        configuration.addAllowedOrigin("http://localhost:9792");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
