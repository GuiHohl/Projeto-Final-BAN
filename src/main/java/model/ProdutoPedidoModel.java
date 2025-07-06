package model;

import java.util.List;

public class ProdutoPedidoModel {
    private String idProduto;
    private List<AdicionalPedidoModel> adicionais;

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public List<AdicionalPedidoModel> getAdicionais() {
        return adicionais;
    }

    public void setAdicionais(List<AdicionalPedidoModel> adicionais) {
        this.adicionais = adicionais;
    }
}
