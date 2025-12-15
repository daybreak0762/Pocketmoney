package school.pocketmoney;

// CORS 설정을 추가
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로에 적용
                // 📌 Origin을 명확히 정의합니다. (localhost와 127.0.0.1 모두 허용하는 것이 안전)
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
                // 📌 필요한 모든 메서드를 허용합니다. GET 요청이므로 GET은 필수.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // 📌 세션 쿠키 전송을 허용합니다. (401 오류 방지를 위해 필수)
                .allowCredentials(true);
    }
}