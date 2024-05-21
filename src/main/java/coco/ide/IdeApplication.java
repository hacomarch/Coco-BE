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
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:3000", "http://ec2-3-25-61-114.ap-southeast-2.compute.amazonaws.com", "http://ec2-13-125-221-158.ap-northeast-2.compute.amazonaws.com/", "http://ec2-3-27-138-7.ap-southeast-2.compute.amazonaws.com", "http://ec2-54-79-146-246.ap-southeast-2.compute.amazonaws.com")
						.allowedMethods("*")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
}