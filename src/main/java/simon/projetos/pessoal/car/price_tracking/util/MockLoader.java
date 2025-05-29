package simon.projetos.pessoal.car.price_tracking.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public class MockLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode loadMock() {
        try (InputStream is = MockLoader.class.getClassLoader().getResourceAsStream("static/mock.json")) {
            return mapper.readTree(is);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar mock.json", e);
        }
    }
}
