package io.roemers.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@SpringBootApplication(proxyBeanMethods = false)
public class ReceiverApplication {

    private final ObjectMapper mapper;

	public static void main(String[] args) {
		SpringApplication.run(ReceiverApplication.class, args);
	}

	ReceiverApplication(ObjectMapper mapper) {
	    this.mapper = mapper;
    }

	@GetMapping("/")
	public ObjectNode test() {
        ObjectNode res = mapper.createObjectNode();
        res.put("message", "Receiver is running!");
        return res;
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
      }
    };
  }

}
