package com.nfssoundtrack.racingsoundtracks.others;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.racingsoundtracks.deserializers.AuthorToDiscoGSDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    private final DatabaseUserDetailsService databaseUserDetailsService;
    private final Environment environment;

    public WebSecurityConfig(DatabaseUserDetailsService databaseUserDetailsService, Environment environment) {
        this.databaseUserDetailsService = databaseUserDetailsService;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain normalSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                //disabling cors/csrf to avoid stupid browser blocking it all
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        //these endpoints we don't want to expose to non-admin user
                        //or so called not-authenticated user
                        .requestMatchers("/manage/manage", "/manage/manage#", "/serie/**", "/country/**",
                                "/maingroup/**", "/songSubgroup/**", "/subgroup/**", "/gamedb/**")
                        .hasAuthority("ADMIN"))
                .authorizeHttpRequests(requests -> requests
                        //those can be seen by everones
                        .requestMatchers("/**", "/content/**", "/css/**", "/js/**", "/images/**",
                                "/fragments/**", "/game/**", "/author/**", "/genre/**", "/search/**",
                                "/custom/playlist", "/song/**", "/songinfo/**", "/countryInfo/**",
                                "/favicon.ico", "/nfssoundtrack.ico",
                                "favicon.ico", "nfssoundtrack.ico").permitAll()
                )
                //setting up login form
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/content/donate")
                        .defaultSuccessUrl("/content/home", true)
                )
                //setting up logout form
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .userDetailsService(databaseUserDetailsService);
        return http.build();
    }

    /**
     * probably some dynaminc linkage to locale, no idea how this works
     *
     * @return
     */
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
    public Map<Long, DiscoGSObj> discoGSObjMap() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(AuthorToDiscoGSObj.class, new AuthorToDiscoGSDeserializer());
        mapper.registerModule(simpleModule);
        Map<Long, DiscoGSObj> discogsMap = new HashMap<>();
        File currentDir = new File("discogs");
        if (!currentDir.exists()) {
            currentDir.mkdir();
        }
        File[] allDirs = currentDir.listFiles();
        if (allDirs != null) {
            for (File artistDir : allDirs) {
                if (artistDir.isDirectory()) {
                    try {
                        File discoGsFile = new File(artistDir.getPath() + File.separator + "discogs.json");
                        AuthorToDiscoGSObj obj = mapper.readValue(discoGsFile, AuthorToDiscoGSObj.class);
                        String profile = obj.getDiscoGSObj().getProfile();
                        if (profile != null && !profile.isEmpty()) {
                            obj.getDiscoGSObj().setProfile(profile
                                    .replace("[b]", "<b>").replace("[/b]", "</b>")
                                    .replace("[u]", "<u>").replace("[/u]", "</u>")
                                    .replace("[i]", "<i>").replace("[/i]", "</i>"));
                        }
                        discogsMap.put(obj.getArtistId(), obj.getDiscoGSObj());
                    } catch (NumberFormatException e1) {
                        logger.debug("that's fine");
                    } catch (IOException e) {
                        logger.error("unexpected error {}", e.getMessage());
                    }
                }
            }
        }
        return discogsMap;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        if (environment.getActiveProfiles().length > 0 && !"docker".contains(environment.getActiveProfiles()[0])) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/")
                    .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS));
        }

    }

}
