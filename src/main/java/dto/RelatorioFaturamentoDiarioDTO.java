package dto;

import java.math.BigDecimal;

public class RelatorioFaturamentoDiarioDTO {
    private String dataPagamento;
    private int totalComandas;
    private BigDecimal faturamentoTotal;

    public RelatorioFaturamentoDiarioDTO(String dataPagamento, int totalComandas, BigDecimal faturamentoTotal) {
        this.dataPagamento = dataPagamento;
        this.totalComandas = totalComandas;
        this.faturamentoTotal = faturamentoTotal;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public int getTotalComandas() {
        return totalComandas;
    }

    public void setTotalComandas(int totalComandas) {
        this.totalComandas = totalComandas;
    }

    public BigDecimal getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public void setFaturamentoTotal(BigDecimal faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }

    @Override
    public String toString() {
        return String.format("Data: %s | Comandas: %d | Faturamento: R$ %.2f", dataPagamento, totalComandas, faturamentoTotal);
    }
}

