package simon.projetos.pessoal.car.price_tracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;

public final class MockLoader {

    private static JsonNode cache;

    private MockLoader() { }

    public static JsonNode loadMock() {
        if (cache == null) {
            try (InputStream is =
                         new ClassPathResource("static/mock.json").getInputStream()) {
                cache = new ObjectMapper().readTree(is);
            } catch (IOException e) {
                throw new IllegalStateException("Não foi possível carregar mock.json", e);
            }
        }
        return cache;
    }
}