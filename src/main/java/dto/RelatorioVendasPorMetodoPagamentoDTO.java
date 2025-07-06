package dto;

import java.math.BigDecimal;

public class RelatorioVendasPorMetodoPagamentoDTO {
    private String metodoPagamento;
    private int totalPagamentos;
    private BigDecimal totalVendas;

    public RelatorioVendasPorMetodoPagamentoDTO(String metodoPagamento, int totalPagamentos, BigDecimal totalVendas) {
        this.metodoPagamento = metodoPagamento;
        this.totalPagamentos = totalPagamentos;
        this.totalVendas = totalVendas;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public int getTotalPagamentos() {
        return totalPagamentos;
    }

    public void setTotalPagamentos(int totalPagamentos) {
        this.totalPagamentos = totalPagamentos;
    }

    public BigDecimal getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(BigDecimal totalVendas) {
        this.totalVendas = totalVendas;
    }

    @Override
    public String toString() {
        return String.format("Método: %s | Total Pagamentos: %d | Total Vendas: R$ %.2f", metodoPagamento, totalPagamentos, totalVendas);
    }
}
