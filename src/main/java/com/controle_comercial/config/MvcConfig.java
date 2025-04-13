package com.controle_comercial.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração MVC para a resolução de recursos estáticos.
 * Aqui, garantimos que os arquivos dos webjars, CSS, JS, etc., sejam servidos corretamente.
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia requisições para "/webjars/**" para os recursos internos dos webjars.
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // Mapeia arquivos CSS que ficam em /css/ dentro de src/main/resources/static/css.
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // Mapeia arquivos JavaScript que ficam em /js/ dentro de src/main/resources/static/js.
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}
