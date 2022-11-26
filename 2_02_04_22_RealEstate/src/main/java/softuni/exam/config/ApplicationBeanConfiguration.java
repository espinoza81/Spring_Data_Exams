package softuni.exam.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class ApplicationBeanConfiguration {

    @Bean
    public Gson createGson(){
//        JsonDeserializer<LocalTime> toLocalTime =
//                (json, t, c) -> LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME);
//
//        JsonSerializer<String> fromLocalTime =
//                (date, t, c) -> new JsonPrimitive(date);

        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
//                .registerTypeAdapter(LocalTime.class, toLocalTime)
//                .registerTypeAdapter(LocalTime.class, fromLocalTime)
                .create();
    }

    @Bean
    public ModelMapper createModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Validator createValidation() {
        return Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

}
