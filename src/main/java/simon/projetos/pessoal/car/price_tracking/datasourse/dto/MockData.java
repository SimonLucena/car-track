package simon.projetos.pessoal.car.price_tracking.datasourse.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class MockData {
    private List<MarcaDTO> marcas;
    private Map<String, List<ModeloDTO>> modelos;
    private Map<String, List<AnoDTO>> anos;
    private Map<String, VeiculoDTO> veiculos;

}

