package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    @Autowired
    private DatabaseUserDetailsService databaseUserDetailsService;

    @Bean
    public SecurityFilterChain normalSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/manage/manage", "/manage/manage#", "/serie/**", "/country/**",
                                "/maingroup/**", "/songSubgroup/**", "/subgroup/**", "/gamedb/**")
                        .hasAuthority("ADMIN"))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/**", "/content/**", "/css/**", "/js/**", "/images/**",
                                "/fragments/**", "/game/**", "/author/**", "/genre/**", "/search/**",
                                "/custom/playlist", "/song/**", "/songinfo/**",
                                "/favicon.ico", "/nfssoundtrack.ico",
                                "favicon.ico", "nfssoundtrack.ico").permitAll()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .failureUrl("/content/donate")
                        .defaultSuccessUrl("/content/home", true)
                )
                .logout((logout) -> logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()).userDetailsService(databaseUserDetailsService);
        return http.build();
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @Cacheable("discoGSMap")
    public Map<Author, DiscoGSObj> discoGSObjMap() {
        Map<Author, DiscoGSObj> cachedMap = new HashMap<>();
        return cachedMap;
    }

}
