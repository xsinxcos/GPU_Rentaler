package com.gpu.rentaler.infra;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.gpu.rentaler.common.EventStore;
import com.gpu.rentaler.sys.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.time.format.DateTimeFormatter;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final SessionService sessionService;
    private final EventStore eventStore;

    public WebMvcConfiguration(SessionService sessionService, EventStore eventStore) {
        this.sessionService = sessionService;
        this.eventStore = eventStore;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(new AuthInterceptor(sessionService) {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                    // 放行 OPTIONS 请求，解决跨域预检问题
                    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                        return true;
                    }
                    return super.preHandle(request, response, handler);
                }
            })
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/storage/fetch/**",
                "/storage/download/**",
                "/login",
                "/registry",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/assets/**",
                "/favicon.ico",
                "/avatar.jpg",
                "/index.html",
                "/",
                "/test/**"
            );

        // 事件订阅拦截器
        registry.addInterceptor(new EventSubscribesInterceptor(eventStore, sessionService))
            .addPathPatterns("/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/Rentaler_System_UI/");
    }

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // 允许指定前端域名跨域
            .allowedOrigins("http://localhost:3000" ,"https://gpurent.netlify.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true) // 允许发送 Cookie
            .maxAge(3600);
    }

    /**
     * JSON 序列化/反序列化配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            // JPA Entity 序列化器
            builder.serializers(BaseEntitySerializer.instance);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 反序列化器
            builder.deserializers(new LocalDateDeserializer(dateFormatter));
            builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));

            // 序列化器
            builder.serializers(new LocalDateSerializer(dateFormatter));
            builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
        };
    }
}
