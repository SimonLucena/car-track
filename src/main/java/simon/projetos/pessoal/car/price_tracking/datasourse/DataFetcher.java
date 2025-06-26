package simon.projetos.pessoal.car.price_tracking.datasourse;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface DataFetcher {
    JsonNode getMock() throws IOException;
}
