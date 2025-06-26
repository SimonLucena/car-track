package simon.projetos.pessoal.car.price_tracking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import simon.projetos.pessoal.car.price_tracking.service.VeiculoService;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GeneralRestController {
    private final VeiculoService veiculoService;
    @GetMapping("/medias-preco-marcas")
    public Map<String, BigDecimal> mediasPreco() {
        return veiculoService.mediasTodasAsMarcas();
    }
}
