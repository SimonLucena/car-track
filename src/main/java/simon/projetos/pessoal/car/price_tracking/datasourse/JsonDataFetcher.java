package simon.projetos.pessoal.car.price_tracking.datasourse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class JsonDataFetcher implements DataFetcher {
    @Value("classpath:/static/mock.json")
    private Resource mockFile;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public JsonNode getMock() {
        try (InputStream is = mockFile.getInputStream()) {
            return mapper.readTree(is);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler mock.json", e);
        }
    }
}
