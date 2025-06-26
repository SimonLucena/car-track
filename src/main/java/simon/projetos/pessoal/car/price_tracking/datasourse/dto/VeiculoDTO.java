package simon.projetos.pessoal.car.price_tracking.datasourse.dto;

public record VeiculoDTO(
        String valor,
        String marca,
        String modelo,
        Integer anoModelo,
        String combustivel,
        String codigoFipe,
        String mesReferencia,
        Integer tipoVeiculo,
        String siglaCombustivel
) {
}
