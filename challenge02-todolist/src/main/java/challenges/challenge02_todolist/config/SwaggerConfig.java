package challenges.challenge02_todolist.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSchemas("Page", new Schema<>().type("object")))
                .info( new Info()
                        .title("API de Tarefas - Todolist")
                        .version("1.0")
                        .description("Documentaçao da API para gerencia tarefas"));
    }



}
