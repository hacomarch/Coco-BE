package coco.ide.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    // 아래 코드는 개발용
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .addHeaderWriter(new StaticHeadersWriter("X-Frame-Options", "DENY")));

        return http.build();
    }


//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests((authz) -> authz
//                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()  // 개발중) 정적 리소스에 대한 접근 허용
//                        .requestMatchers("/api/docs/**","/api/members/login", "/api/members/register").permitAll()    // 개발중) API 문서 경로 접근 허용
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(withDefaults())
//                .csrf(AbstractHttpConfigurer::disable)         // 개발중) csrf 보호 비활성화
//                .headers(headers -> headers
//                        .addHeaderWriter(new StaticHeadersWriter("X-Frame-Options", "DENY"))) // 개발중) frame 옵션 비활성화
//                .formLogin((formLogin) -> formLogin
//                        .loginPage("/api/members/login")
//                        .defaultSuccessUrl("/", true)
//                        .failureForwardUrl("/api/members/login")
//                        .permitAll()
//                )
//                .logout((logout) -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/members/logout"))
//                        .logoutSuccessUrl("/api/members/login")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true)
//                        .permitAll()
//                );
//
//
//        return http.build();
//    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails admin = User.withUsername("coco")
//                .password(passwordEncoder().encode("coco"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user);
//    }

}
