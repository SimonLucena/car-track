package simon.projetos.pessoal.car.price_tracking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import simon.projetos.pessoal.car.price_tracking.entity.Marca;
import simon.projetos.pessoal.car.price_tracking.entity.Modelo;
import simon.projetos.pessoal.car.price_tracking.entity.Veiculo;
import simon.projetos.pessoal.car.price_tracking.service.MarcaService;
import simon.projetos.pessoal.car.price_tracking.service.ModeloService;
import simon.projetos.pessoal.car.price_tracking.service.VeiculoService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomePageController {
    private final MarcaService marcaService;
    private final ModeloService modeloService;
    private final VeiculoService veiculoService;

    public HomePageController(MarcaService marcaService, ModeloService modeloService, VeiculoService veiculoService) {
        this.marcaService = marcaService;
        this.modeloService = modeloService;
        this.veiculoService = veiculoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("marcas",  marcaService.getMarcas());
        model.addAttribute("modelos", modeloService.getModelos());
        return "index";
    }

    @GetMapping("/marcas-lista")
    public String marcas(Model model) {
        List<Marca> marcas = marcaService.getMarcas();

        Map<String, Long> qtdVeiculos = marcas.stream()
                .collect(Collectors.toMap(
                        Marca::getCodigo,
                        m -> veiculoService.countVeiculosByMarca(m.getCodigo())
                ));

        model.addAttribute("marcas", marcas);
        model.addAttribute("qtdVeiculos", qtdVeiculos);
        return "marcas";
    }

    @GetMapping("/modelos-lista")
    public String modelos(Model model) {
        model.addAttribute("modelos", modeloService.getModelos());
        return "comparador";
    }

    @GetMapping("/modelos/{codigoMarca}")
    public ResponseEntity<List<Modelo>> getModelosByMarca(@PathVariable String codigoMarca) {
        List<Modelo> modelos = modeloService.getModelosByMarcaCodigo(codigoMarca);
        return ResponseEntity.ok(modelos);
    }

    @GetMapping("/veiculosModelo/{codigoModelo}")
    public ResponseEntity<List<Veiculo>> getVeiculosByModelo(@PathVariable String codigoModelo) {
        List<Veiculo> veiculos = veiculoService.getVeiculoByModeloCodigoid(codigoModelo);
        return ResponseEntity.ok(veiculos);
    }
}
