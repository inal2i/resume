package dev.inal.resume.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.media-path}")
    private String mediaPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:" + mediaPath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "https://inal.tuaev.ru")
                .allowedMethods("POST", "GET", "OPTIONS")
                .allowedHeaders("*");
    }

    // SPA fallback: все маршруты без точки (не статика) отдают index.html
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/index.html");
    }
}
