package model;

import model.enums.StatusPedido;

import java.util.Date;
import java.util.List;

public class PedidoModel {
    private String _id;
    private String idComanda;
    private Date dataPedido;
    private StatusPedido status;
    private List<ProdutoPedidoModel> produtos;

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getIdComanda() {
        return idComanda;
    }

    public void setIdComanda(String idComanda) {
        this.idComanda = idComanda;
    }

    public Date getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(Date dataPedido) {
        this.dataPedido = dataPedido;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public List<ProdutoPedidoModel> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<ProdutoPedidoModel> produtos) {
        this.produtos = produtos;
    }
}
