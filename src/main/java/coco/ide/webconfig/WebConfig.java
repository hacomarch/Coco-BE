package coco.ide.webconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

<<<<<<< HEAD
@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
=======

//@Configuration
//public class WebConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
>>>>>>> 54cc4164fa3f80ad1f5f42bbc77de6f4f31c7319
//                registry.addMapping("/**")
////                        .allowedOrigins("http://localhost:8080",
////                                "http://localhost:3000",
////                                "https://k40d5114c4212a.user-app.krampoline.com",
////                                "https://k100f7af4f18ea.user-app.krampoline.com")
////                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
////                        .allowedHeaders("*")
//                        .allowedOrigins()
<<<<<<< HEAD
                registry.addMapping("/**")
                        .allowedOriginPatterns("*") // 안에 해당 주소를 넣어도 됨
                        .allowedHeaders("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS" , "PATCH")
                        .exposedHeaders("Authorization", "RefreshToken");
                //.allowCredentials(true);
            }
        };
    }
}
=======
//            }
//        };
//    }
//}

>>>>>>> 54cc4164fa3f80ad1f5f42bbc77de6f4f31c7319
