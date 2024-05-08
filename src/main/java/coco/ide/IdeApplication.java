package coco.ide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class IdeApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdeApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				//Todo: cors설정 시 프론트 포트로 allowedOrigins 바꿔주면 됨
				registry.addMapping("/**").allowedOrigins("5176");
			}
		};
	}
}
