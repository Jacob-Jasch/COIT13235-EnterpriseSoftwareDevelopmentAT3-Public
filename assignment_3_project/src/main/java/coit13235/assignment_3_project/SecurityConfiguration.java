package coit13235.assignment_3_project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the application.
 */
@Configuration
class SecurityConfiguration {

  /**
   * Provides a BCrypt password encoder bean.
   * @return the password encoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }



  /**
   * RestTemplate bean with custom message converter to handle iTunes API responses
   * @return configured RestTemplate
   */
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    
    // Add a custom message converter that can handle text/javascript content type
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Arrays.asList(
        MediaType.APPLICATION_JSON,
        MediaType.valueOf("text/javascript"),
        MediaType.valueOf("application/javascript")
    ));
    
    List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
    messageConverters.add(0, converter); // Add at the beginning to prioritize
    restTemplate.setMessageConverters(messageConverters);
    
    return restTemplate;
  }

  /**
   * Configures security for API endpoints.
   * @param http the HttpSecurity object
   * @return the security filter chain
   * @throws Exception if configuration fails
   */
  @Bean @Order(1)
  SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
    http
      .securityMatcher("/api/**")
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(a -> a.anyRequest().authenticated())
      .httpBasic(basic -> basic.realmName("Chat API"))
      .sessionManagement(session -> session
          .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED))
      .exceptionHandling(e -> e.authenticationEntryPoint((AuthenticationEntryPoint) new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
    return http.build();
  }

  /**
   * Configures security for web endpoints.
   * @param http the HttpSecurity object
   * @return the security filter chain
   * @throws Exception if configuration fails
   */
  @Bean @Order(2)
  SecurityFilterChain webChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(a -> a
          .requestMatchers("/css/**","/js/**","/images/**","/h2-console/**","/favicon.ico").permitAll()
          .requestMatchers("/register").permitAll()
          .anyRequest().authenticated())
      .headers(h -> h.frameOptions(f -> f.sameOrigin()))
    .formLogin(f -> f
      .loginPage("/login")
      .defaultSuccessUrl("/ui/chat", true)
      .permitAll())
      .logout(l -> l.logoutSuccessUrl("/login?logout").permitAll());
    return http.build();
  }
}