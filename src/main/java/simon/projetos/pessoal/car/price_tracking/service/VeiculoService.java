package simon.projetos.pessoal.car.price_tracking.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import simon.projetos.pessoal.car.price_tracking.datasourse.DataFetcher;
import simon.projetos.pessoal.car.price_tracking.entity.Marca;
import simon.projetos.pessoal.car.price_tracking.entity.Modelo;
import simon.projetos.pessoal.car.price_tracking.entity.Veiculo;
import simon.projetos.pessoal.car.price_tracking.repository.VeiculoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeiculoService {
    private final ModeloService modeloService;
    private final VeiculoRepository veiculoRepository;
    private final DataFetcher dataFetcher;
    private final MarcaService marcaService;

    @PostConstruct
    void init() throws IOException {
        JsonNode raiz = dataFetcher.getMock().path("veiculos");  // map<chave, objeto>

        raiz.fields().forEachRemaining((Map.Entry<String,JsonNode> e) -> {

            String chave         = e.getKey();     // ex.: "1001-2022-1"
            JsonNode vNode       = e.getValue();   // objeto do veículo
            String codigoModelo  = chave.split("-")[0];  // "1001"

            Modelo modelo = modeloService.getModeloByCodigo(codigoModelo);
            if (modelo == null) return;            // ignora se modelo não existe

            veiculoRepository.add(jsonToVeiculo(vNode, modelo));
        });
    }

    public List<Veiculo> getVeiculos(){
        return veiculoRepository.getVeiculos();
    }

    public List<Veiculo> getVeiculoByModeloCodigoid(String codigo){
        List<Veiculo> veiculosRetorno = new ArrayList<>();
        for (Veiculo veiculo : veiculoRepository.getVeiculos()) {
            if (veiculo.getModelo().getCodigo().equals(codigo)){
                veiculosRetorno.add(veiculo);
            }
        }
        return veiculosRetorno;
    }

    private Veiculo jsonToVeiculo(JsonNode v, Modelo m){
        String valorStr = v.get("valor").asText()
                .replaceAll("[R$\\s\\.]", "")
                .replace(',', '.');
        return new Veiculo(
                v.get("codigoFipe").asText(),
                m,
                v.get("anoModelo").asInt(),
                v.get("combustivel").asText(),
                v.get("siglaCombustivel").asText(),
                v.get("tipoVeiculo").asInt(),
                new BigDecimal(valorStr),
                v.get("mesReferencia").asText()
        );
    }

    public long countVeiculosByMarca(String m) {
        return veiculoRepository.getVeiculos().stream()
                .filter(v -> v.getModelo()
                        .getMarca()
                        .getCodigo()
                        .equals(m))
                .count();
    }

    public BigDecimal mediaPrecoPorMarca(String codigoMarca) {
        return veiculoRepository.getVeiculos().stream()
                .filter(v -> v.getModelo().getMarca().getCodigo().equals(codigoMarca))
                .map(Veiculo::getValor)                // BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(
                        countVeiculosByMarca(codigoMarca)), RoundingMode.HALF_UP);
    }

    public Map<String, BigDecimal> mediasTodasAsMarcas() {
        return marcaService.getMarcas().stream()
                .collect(Collectors.toMap(
                        Marca::getNome,
                        m -> mediaPrecoPorMarca(m.getCodigo()),
                        (a,b) -> a
                ));
    }
}
