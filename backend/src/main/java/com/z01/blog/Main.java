package com.z01.blog;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.security.Keys;

@SpringBootApplication
@EnableJpaRepositories(considerNestedRepositories = true)
public class Main {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
		SpringApplication.run(Main.class, args);
	}

	@Configuration
	public static class JwtConfig {

		@Value("${jwt.secret}")
		private String secret;

		@Bean
		public SecretKey jwtKey() {
			return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		}
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**").allowedOrigins("http://localhost:4200").allowCredentials(true)
						.allowedMethods("GET", "POST", "DELETE");
			}
		};
	}
}
