package simon.projetos.pessoal.car.price_tracking.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import simon.projetos.pessoal.car.price_tracking.entity.Veiculo;
import simon.projetos.pessoal.car.price_tracking.service.MarcaService;
import simon.projetos.pessoal.car.price_tracking.service.ModeloService;
import simon.projetos.pessoal.car.price_tracking.service.VeiculoService;
import java.util.List;
import java.util.Objects;

@Controller
public class ComparadorPageController {
    private final MarcaService marcaService;
    private final ModeloService modeloService;
    private final VeiculoService veiculoService;

    public ComparadorPageController(MarcaService marcaService, ModeloService modeloService, VeiculoService veiculoService) {
        this.marcaService = marcaService;
        this.modeloService = modeloService;
        this.veiculoService = veiculoService;
    }

    @GetMapping("/comparador")
    public ModelAndView comparador(@RequestParam(required = false) Integer card,
                                   @RequestParam(required = false) String codigoModelo,
                                   @RequestParam(required = false) String codigoAno,
                                   HttpSession session) {

        Veiculo v1 = (Veiculo) session.getAttribute("veiculo1");
        Veiculo v2 = (Veiculo) session.getAttribute("veiculo2");

        if (card != null && codigoModelo != null && codigoAno != null) {
            Veiculo v = veiculoService.getVeiculoByModeloAndAno(codigoModelo, codigoAno);
            if (card == 1) { v1 = v; session.setAttribute("veiculo1", v1); }
            if (card == 2) { v2 = v; session.setAttribute("veiculo2", v2); }
        }

        ModelAndView mv = new ModelAndView("comparador/comparador");

        mv.addObject("marcas", marcaService.getMarcas());
        mv.addObject("modelos", modeloService.getModelos());
        mv.addObject("veiculo1", v1);
        mv.addObject("veiculo2", v2);
        mv.addObject("condiction1", v1 != null);
        mv.addObject("condiction2", v2 != null);
        return mv;
    }

    @GetMapping("/comparador/limpar/{veiculo}")
    public String limpar(@PathVariable String veiculo,
                         HttpSession session) {
        if ("veiculo1".equals(veiculo) || "veiculo2".equals(veiculo)) {
            session.removeAttribute(veiculo);
        }
        return "redirect:/comparador";
    }

    @GetMapping("/anosModelo/{codigoModelo}")
    public ResponseEntity<List<String>> getAnosByModelo(@PathVariable String codigoModelo){
        List<String> anos = veiculoService.getAnosByModeloCodigoid(codigoModelo);
        return ResponseEntity.ok(anos);
    }
}
