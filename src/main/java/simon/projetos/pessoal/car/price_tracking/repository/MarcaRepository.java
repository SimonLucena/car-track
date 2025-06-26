package simon.projetos.pessoal.car.price_tracking.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import simon.projetos.pessoal.car.price_tracking.entity.Marca;
import simon.projetos.pessoal.car.price_tracking.service.ModeloService;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Repository
public class MarcaRepository {
    private List<Marca> marcas = new ArrayList<>();

    public void addMarca(Marca marca){
        marcas.add(marca);
    }
}
