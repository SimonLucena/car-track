package simon.projetos.pessoal.car.price_tracking.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import simon.projetos.pessoal.car.price_tracking.entity.Marca;
import simon.projetos.pessoal.car.price_tracking.repository.MarcaRepository;
import simon.projetos.pessoal.car.price_tracking.service.coleta.DataFetcher;
import simon.projetos.pessoal.car.price_tracking.service.coleta.JsonDataFetcher;
import simon.projetos.pessoal.car.price_tracking.util.MockLoader;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarcaService {
    private DataFetcher dataFetcher;
    private final MarcaRepository marcaRepository;
    private final JsonNode mock = MockLoader.loadMock();

    public MarcaService() {
        this.dataFetcher = new JsonDataFetcher();
        this.marcaRepository = new MarcaRepository();
        gerarMarcasDeCarros();
    }

    public List<Marca> getMarcas() {
        List<Marca> marcas = new ArrayList<>();
        for (JsonNode node : mock.get("marcas")) {
            marcas.add(new Marca(node.get("codigo").asText(), node.get("nome").asText()));
        }
        return marcas;
    }

    private void gerarMarcasDeCarros() {
//        return dataFetcher.getMarcasDeCarros();
        Marca marca1 = new Marca("001", "Fiat");
        Marca marca2 = new Marca("002", "Volkswagen");
        Marca marca3 = new Marca("003", "Chevrolet");
        marcaRepository.addMarca(marca1);
        marcaRepository.addMarca(marca2);
        marcaRepository.addMarca(marca3);
    }

    // public List<Marca> getMarcas(){
    //     return marcaRepository.getMarcas();
    // }

    public Marca getMarcaByCodigo(String codigo){
        for (Marca i : marcaRepository.getMarcas()) {
            if (i.getCodigo().equals(codigo)){
                return i;
            }
        }
        return null;
    }
}
