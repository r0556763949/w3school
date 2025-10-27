package config;

import co.verisoft.fw.selenium.drivers.VerisoftDriver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
@Configuration
@PropertySources( @PropertySource({"root.config.properties"}))
@ComponentScan({"w3schools"})

public class EnvConfig {
    public EnvConfig() {
    }

    //for placeholders
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public VerisoftDriver driver() {
        ChromeOptions options = new ChromeOptions();
        return new VerisoftDriver(options);
    }

    //for read json
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
