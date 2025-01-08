package umc.TripPiece.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import umc.TripPiece.converter.OctetStreamReadMsgConverter;
import umc.TripPiece.validation.interceptor.TokenValidationInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final OctetStreamReadMsgConverter octetStreamReadMsgConverter;
    private final TokenValidationInterceptor tokenValidationInterceptor;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(octetStreamReadMsgConverter);
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(tokenValidationInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/swagger-ui/**",
//                        "/api-docs/**",
//                        "/user/signup",
//                        "/user/login"
//                );

    }
