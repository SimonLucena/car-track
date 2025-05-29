package simon.projetos.pessoal.car.price_tracking.service;

import org.springframework.stereotype.Service;

import simon.projetos.pessoal.car.price_tracking.entity.Marca;
import simon.projetos.pessoal.car.price_tracking.entity.Modelo;
import simon.projetos.pessoal.car.price_tracking.repository.ModeloRepository;
import simon.projetos.pessoal.car.price_tracking.service.coleta.DataFetcher;
import simon.projetos.pessoal.car.price_tracking.service.coleta.JsonDataFetcher;
import simon.projetos.pessoal.car.price_tracking.util.MockLoader;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModeloService {
    private final MarcaService marcaService;
    private final DataFetcher dataFetcher;
    private final ModeloRepository modeloRepository;
    private final JsonNode mock = MockLoader.loadMock();

    public ModeloService(MarcaService marcaService) {
        this.modeloRepository = new ModeloRepository();
        this.dataFetcher = new JsonDataFetcher();
        this.marcaService = marcaService;
        gerarModelos(marcaService);
    }

    private void gerarModelos(MarcaService marcaService) {
//        return dataFetcher.getModelos(marcas);
        Modelo modelo1 = new Modelo(marcaService.getMarcaByCodigo("001"), "1001", "Uno");
        Modelo modelo2 = new Modelo(marcaService.getMarcaByCodigo("001"), "1002", "Palio");
        Modelo modelo3 = new Modelo(marcaService.getMarcaByCodigo("002"), "2001", "Gol");
        Modelo modelo4 = new Modelo(marcaService.getMarcaByCodigo("002"), "2002", "Polo");
        Modelo modelo5 = new Modelo(marcaService.getMarcaByCodigo("003"), "3001", "Onix");
        Modelo modelo6 = new Modelo(marcaService.getMarcaByCodigo("003"), "3002", "Celta");
        modeloRepository.addModelo(modelo1);
        modeloRepository.addModelo(modelo2);
        modeloRepository.addModelo(modelo3);
        modeloRepository.addModelo(modelo4);
        modeloRepository.addModelo(modelo5);
        modeloRepository.addModelo(modelo6);
    }

    // public List<Modelo> getModelos() {
    //     return modeloRepository.getModelos();
    // }

    // public List<Modelo> getModelosByMarcaCodigo(String codigoMarca) {
    //     List<Modelo> modelosRetorno = new ArrayList<>();
    //     for (Modelo modelo : modeloRepository.getModelos()){
    //         if(modelo.getMarca().getCodigo().equals(codigoMarca)){
    //             modelosRetorno.add(modelo);
    //         }
    //     }
    //     return modelosRetorno;
    // }

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
}
