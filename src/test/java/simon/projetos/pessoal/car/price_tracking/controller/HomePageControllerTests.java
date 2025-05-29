package simon.projetos.pessoal.car.price_tracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import simon.projetos.pessoal.car.price_tracking.controller.HomePageController;
import simon.projetos.pessoal.car.price_tracking.entity.Marca;
import simon.projetos.pessoal.car.price_tracking.entity.Modelo;
import simon.projetos.pessoal.car.price_tracking.entity.Veiculo;
import simon.projetos.pessoal.car.price_tracking.service.MarcaService;
import simon.projetos.pessoal.car.price_tracking.service.ModeloService;
import simon.projetos.pessoal.car.price_tracking.service.VeiculoService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HomePageControllerTest {

    @Mock
    private MarcaService marcaService;

    @Mock
    private ModeloService modeloService;

    @Mock
    private VeiculoService veiculoService;

    @InjectMocks
    private HomePageController homePageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(homePageController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testIndexEndpoint() {
        // Given
        List<Marca> marcasMock = Arrays.asList(
            new Marca("001", "Fiat"),
            new Marca("002", "Volkswagen"),
            new Marca("003", "Chevrolet")
        );

        List<Modelo> modelosMock = Arrays.asList(
            new Modelo(new Marca("001", "Fiat"), "1001", "Uno"),
            new Modelo(new Marca("001", "Fiat"), "1002", "Palio"),
            new Modelo(new Marca("002", "Volkswagen"), "2001", "Gol")
        );

        when(marcaService.getMarcas()).thenReturn(marcasMock);
        when(modeloService.getModelos()).thenReturn(modelosMock);

        // When
        ModelAndView result = homePageController.index();

        // Then
        assertNotNull(result);
        assertEquals("index", result.getViewName());
        assertEquals(marcasMock, result.getModel().get("marcas"));
        assertEquals(modelosMock, result.getModel().get("modelos"));

        verify(marcaService, times(1)).getMarcas();
        verify(modeloService, times(1)).getModelos();
    }

    @Test
    void testIndexEndpoint_WithMockMvc() throws Exception {
        // Given
        List<Marca> marcasMock = Arrays.asList(
            new Marca("001", "Fiat"),
            new Marca("002", "Volkswagen")
        );

        List<Modelo> modelosMock = Arrays.asList(
            new Modelo(new Marca("001", "Fiat"), "1001", "Uno")
        );

        when(marcaService.getMarcas()).thenReturn(marcasMock);
        when(modeloService.getModelos()).thenReturn(modelosMock);

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("marcas"))
                .andExpect(model().attributeExists("modelos"))
                .andExpect(model().attribute("marcas", marcasMock))
                .andExpect(model().attribute("modelos", modelosMock));

        verify(marcaService, times(1)).getMarcas();
        verify(modeloService, times(1)).getModelos();
    }

    @Test
    void testGetModelosByMarca_Success() throws Exception {
        // Given
        String codigoMarca = "001";
        Marca fiat = new Marca("001", "Fiat");
        List<Modelo> modelosFiat = Arrays.asList(
            new Modelo(fiat, "1001", "Uno"),
            new Modelo(fiat, "1002", "Palio")
        );

        when(modeloService.getModelosByMarcaCodigo(codigoMarca)).thenReturn(modelosFiat);

        // When & Then
        mockMvc.perform(get("/modelos/{codigoMarca}", codigoMarca))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo").value("1001"))
                .andExpect(jsonPath("$[0].nome").value("Uno"))
                .andExpect(jsonPath("$[0].marca.codigo").value("001"))
                .andExpect(jsonPath("$[0].marca.nome").value("Fiat"))
                .andExpect(jsonPath("$[1].codigo").value("1002"))
                .andExpect(jsonPath("$[1].nome").value("Palio"));

        verify(modeloService, times(1)).getModelosByMarcaCodigo(codigoMarca);
    }

    @Test
    void testGetModelosByMarca_EmptyResult() throws Exception {
        // Given
        String codigoMarca = "999";
        when(modeloService.getModelosByMarcaCodigo(codigoMarca)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/modelos/{codigoMarca}", codigoMarca))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(modeloService, times(1)).getModelosByMarcaCodigo(codigoMarca);
    }

    @Test
    void testGetModelosByMarca_DirectCall() {
        // Given
        String codigoMarca = "002";
        Marca volkswagen = new Marca("002", "Volkswagen");
        List<Modelo> modelosVolkswagen = Arrays.asList(
            new Modelo(volkswagen, "2001", "Gol"),
            new Modelo(volkswagen, "2002", "Polo")
        );

        when(modeloService.getModelosByMarcaCodigo(codigoMarca)).thenReturn(modelosVolkswagen);

        // When
        var response = homePageController.getModelosByMarca(codigoMarca);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(modelosVolkswagen, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(modeloService, times(1)).getModelosByMarcaCodigo(codigoMarca);
    }

    @Test
    void testGetVeiculosByModelo_Success() throws Exception {
        // Given
        String codigoModelo = "1001";
        Marca fiat = new Marca("001", "Fiat");
        Modelo uno = new Modelo(fiat, "1001", "Uno");
        
        List<Veiculo> veiculosUno = Arrays.asList(
            createVeiculo(new BigDecimal("42500.00"), fiat, uno, 2022, "Gasolina", "0010010"),
            createVeiculo(new BigDecimal("41000.00"), fiat, uno, 2021, "Gasolina", "0010011"),
            createVeiculo(new BigDecimal("39700.00"), fiat, uno, 2020, "Gasolina", "0010012")
        );

        when(veiculoService.getVeiculoByModeloCodigoid(codigoModelo)).thenReturn(veiculosUno);

        // When & Then
        mockMvc.perform(get("/veiculosModelo/{codigoModelo}", codigoModelo))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].valor").value(42500.00))
                .andExpect(jsonPath("$[0].ano").value(2022))
                .andExpect(jsonPath("$[0].combustivel").value("Gasolina"))
                .andExpect(jsonPath("$[0].modelo.nome").value("Uno"))
                .andExpect(jsonPath("$[1].ano").value(2021))
                .andExpect(jsonPath("$[2].ano").value(2020));

        verify(veiculoService, times(1)).getVeiculoByModeloCodigoid(codigoModelo);
    }

    @Test
    void testGetVeiculosByModelo_EmptyResult() throws Exception {
        // Given
        String codigoModelo = "9999";
        when(veiculoService.getVeiculoByModeloCodigoid(codigoModelo)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/veiculosModelo/{codigoModelo}", codigoModelo))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(veiculoService, times(1)).getVeiculoByModeloCodigoid(codigoModelo);
    }

    @Test
    void testGetVeiculosByModelo_DirectCall() {
        // Given
        String codigoModelo = "2001";
        Marca volkswagen = new Marca("002", "Volkswagen");
        Modelo gol = new Modelo(volkswagen, "2001", "Gol");
        
        List<Veiculo> veiculosGol = Arrays.asList(
            createVeiculo(new BigDecimal("48000.00"), volkswagen, gol, 2022, "Gasolina", "0020010"),
            createVeiculo(new BigDecimal("46500.00"), volkswagen, gol, 2021, "Gasolina", "0020011")
        );

        when(veiculoService.getVeiculoByModeloCodigoid(codigoModelo)).thenReturn(veiculosGol);

        // When
        var response = homePageController.getVeiculosByModelo(codigoModelo);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(veiculosGol, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(veiculoService, times(1)).getVeiculoByModeloCodigoid(codigoModelo);
    }

    @Test
    void testAllEndpoints_VerifyServiceInteractions() {
        // Given
        when(marcaService.getMarcas()).thenReturn(Collections.emptyList());
        when(modeloService.getModelos()).thenReturn(Collections.emptyList());
        when(modeloService.getModelosByMarcaCodigo(anyString())).thenReturn(Collections.emptyList());
        when(veiculoService.getVeiculoByModeloCodigoid(anyString())).thenReturn(Collections.emptyList());

        // When
        homePageController.index();
        homePageController.getModelosByMarca("001");
        homePageController.getVeiculosByModelo("1001");

        // Then
        verify(marcaService, times(1)).getMarcas();
        verify(modeloService, times(1)).getModelos();
        verify(modeloService, times(1)).getModelosByMarcaCodigo("001");
        verify(veiculoService, times(1)).getVeiculoByModeloCodigoid("1001");
        
        verifyNoMoreInteractions(marcaService, modeloService, veiculoService);
    }

    @Test
    void testConstructor_ValidateInjection() {
        // Given
        MarcaService mockMarcaService = mock(MarcaService.class);
        ModeloService mockModeloService = mock(ModeloService.class);
        VeiculoService mockVeiculoService = mock(VeiculoService.class);

        // When
        HomePageController controller = new HomePageController(
            mockMarcaService, 
            mockModeloService, 
            mockVeiculoService
        );

        // Then
        assertNotNull(controller);
        // Verifica se os serviços foram injetados corretamente através de reflexão
        // ou testando comportamento
    }

    // Método auxiliar para criar objetos Veiculo para os testes
    private Veiculo createVeiculo(BigDecimal valor, Marca marca, Modelo modelo, 
                                 int ano, String combustivel, String codigoFipe) {
        Veiculo veiculo = new Veiculo();
        veiculo.setValor(valor);
        veiculo.setModelo(modelo);
        veiculo.setAno(ano);
        veiculo.setCombustivel(combustivel);
        veiculo.setCodigoFipe(codigoFipe);
        veiculo.setMesReferencia("maio de 2024");
        veiculo.setTipoVeiculo(1);
        veiculo.setSiglaCombustivel(combustivel.equals("Gasolina") ? "G" : "F");
        return veiculo;
    }
}