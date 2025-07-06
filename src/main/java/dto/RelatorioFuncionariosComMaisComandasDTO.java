package dto;

public class RelatorioFuncionariosComMaisComandasDTO {
    private String nomeFuncionario;
    private String cargoFuncionario;
    private int totalComandas;

    public RelatorioFuncionariosComMaisComandasDTO(String nomeFuncionario, String cargoFuncionario, int totalComandas) {
        this.nomeFuncionario = nomeFuncionario;
        this.cargoFuncionario = cargoFuncionario;
        this.totalComandas = totalComandas;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public String getCargoFuncionario() {
        return cargoFuncionario;
    }

    public void setCargoFuncionario(String cargoFuncionario) {
        this.cargoFuncionario = cargoFuncionario;
    }

    public int getTotalComandas() {
        return totalComandas;
    }

    public void setTotalComandas(int totalComandas) {
        this.totalComandas = totalComandas;
    }

    @Override
    public String toString() {
        return String.format("Funcionário: %s | Cargo: %s | Comandas Atendidas: %d", nomeFuncionario, cargoFuncionario, totalComandas);
    }
}

