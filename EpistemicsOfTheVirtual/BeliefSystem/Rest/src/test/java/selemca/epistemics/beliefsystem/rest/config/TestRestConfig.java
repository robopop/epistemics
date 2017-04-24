package selemca.epistemics.beliefsystem.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@Configuration
public class TestRestConfig extends RestConfig {
    private static final String SELEMCA_HOME_PATH = "selemca_home";

    @Bean
    @Override
    public File selemcaHome() {
        Path resourceDirectory = Paths.get("BeliefSystem/Rest/src/test/resources", SELEMCA_HOME_PATH);
        return resourceDirectory.toFile();
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter result = new RequestMappingHandlerAdapter();
        result.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        return result;
    }

    @Bean
    public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter() {
        return new SimpleControllerHandlerAdapter();
    }

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }
}