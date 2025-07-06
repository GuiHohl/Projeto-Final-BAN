package dto;

import java.math.BigDecimal;

public class RelatorioProdutosMaisVendidosDTO {
    private String nomeProduto;
    private int quantidadeVendida;
    private BigDecimal faturamento;

    public RelatorioProdutosMaisVendidosDTO(String nomeProduto, int quantidadeVendida, BigDecimal faturamento) {
        this.nomeProduto = nomeProduto;
        this.quantidadeVendida = quantidadeVendida;
        this.faturamento = faturamento;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(int quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }

    public BigDecimal getFaturamento() {
        return faturamento;
    }

    public void setFaturamento(BigDecimal faturamento) {
        this.faturamento = faturamento;
    }

    @Override
    public String toString() {
        return String.format("Produto: %s | Vendas: %d | Faturamento: R$ %.2f", nomeProduto, quantidadeVendida, faturamento);
    }
}

