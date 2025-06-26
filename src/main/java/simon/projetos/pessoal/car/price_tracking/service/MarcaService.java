package simon.projetos.pessoal.car.price_tracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simon.projetos.pessoal.car.price_tracking.datasourse.DataFetcher;
import simon.projetos.pessoal.car.price_tracking.entity.Marca;
import simon.projetos.pessoal.car.price_tracking.repository.MarcaRepository;
import simon.projetos.pessoal.car.price_tracking.util.MockLoader;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaService {
    private final DataFetcher dataFetcher;
    private final MarcaRepository marcaRepository;
    private final JsonNode mock = MockLoader.loadMock();

    @PostConstruct
    void init() throws IOException {
        JsonNode marcas = dataFetcher.getMock().get("marcas");
        if (marcas == null || !marcas.isArray()) {
            throw new IllegalStateException("'marcas' não encontrado em mock.json");
        }
        marcas.forEach(n -> marcaRepository.addMarca(
                new Marca(n.get("codigo").asText(),
                        n.get("nome").asText())));
    }

    public List<Marca> getMarcas() {
        return marcaRepository.getMarcas();
    }

    public Marca getMarcaByCodigo(String codigo){
        for (Marca i : marcaRepository.getMarcas()) {
            if (i.getCodigo().equals(codigo)){
                return i;
            }
        }
        return null;
    }
}
