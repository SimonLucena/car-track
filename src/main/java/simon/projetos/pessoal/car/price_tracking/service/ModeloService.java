package simon.projetos.pessoal.car.price_tracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simon.projetos.pessoal.car.price_tracking.datasourse.DataFetcher;
import simon.projetos.pessoal.car.price_tracking.datasourse.JsonDataFetcher;
import simon.projetos.pessoal.car.price_tracking.entity.Marca;
import simon.projetos.pessoal.car.price_tracking.entity.Modelo;
import simon.projetos.pessoal.car.price_tracking.repository.ModeloRepository;
import simon.projetos.pessoal.car.price_tracking.util.MockLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeloService {
    private final MarcaService marcaService;
    private final DataFetcher dataFetcher;
    private final ModeloRepository modeloRepository;
    private final JsonNode mock = MockLoader.loadMock();

    @PostConstruct
    void init() throws IOException {
        JsonNode modelo = dataFetcher.getMock().get("modelos");

        modelo.fieldNames().forEachRemaining(codMarca -> {
            Marca marca = marcaService.getMarcaByCodigo(codMarca);
            if (marca == null) return;

            JsonNode lista = modelo.get(codMarca);
            if (lista == null || !lista.isArray()) return;

            lista.forEach(n -> modeloRepository.addModelo(
                    new Modelo(marca,
                            n.get("codigo").asText(),
                            n.get("nome").asText())));
        });
    }

    public List<Modelo> getModelos() {
        List<Modelo> modelos = new ArrayList<>();
        for (Marca marca : marcaService.getMarcas()) {
            modelos.addAll(getModelosByMarcaCodigo(marca.getCodigo()));
        }
        return modelos;
    }
    
    public List<Modelo> getModelosByMarcaCodigo(String codigoMarca) {
        List<Modelo> modelos = new ArrayList<>();
        JsonNode modelosJson = mock.get("modelos").get(codigoMarca);
        if (modelosJson != null) {
            Marca marca = marcaService.getMarcas().stream()
                    .filter(m -> m.getCodigo().equals(codigoMarca))
                    .findFirst()
                    .orElse(null);

            for (JsonNode node : modelosJson) {
                modelos.add(new Modelo(marca, node.get("codigo").asText(), node.get("nome").asText()));
            }
        }
        return modelos;
    }

    public Modelo getModeloByCodigo(String codigo) {
        for (Modelo modelo : modeloRepository.getModelos()){
            if (modelo.getCodigo().equals(codigo)){
                return modelo;
            }
        }
        return null;
    }

    public Modelo getModeloByNome(String nome) {
        for (Modelo modelo : modeloRepository.getModelos()){
            if (modelo.getCodigo().equals(nome)){
                return modelo;
            }
        }
        return null;
    }
}
