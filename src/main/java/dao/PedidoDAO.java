package dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import model.AdicionalPedidoModel;
import model.PedidoModel;
import model.ProdutoPedidoModel;
import model.enums.StatusPedido;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class PedidoDAO {

    private final MongoCollection<Document> collection;

    public PedidoDAO(MongoDatabase database) {
        this.collection = database.getCollection("pedidos");
    }

    public void create(PedidoModel pedido) {
        Document doc = new Document()
                .append("_id", pedido.getId())
                .append("idComanda", pedido.getIdComanda())
                .append("dataPedido", pedido.getDataPedido())
                .append("status", pedido.getStatus().name())
                .append("produtos", toProdutoDocuments(pedido.getProdutos()));
        collection.insertOne(doc);
    }

    public PedidoModel read(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        return (doc != null) ? fromDocument(doc) : null;
    }

    public void update(PedidoModel pedido) {
        Bson updates = combine(
                set("idComanda", pedido.getIdComanda()),
                set("dataPedido", pedido.getDataPedido()),
                set("status", pedido.getStatus().name()),
                set("produtos", toProdutoDocuments(pedido.getProdutos()))
        );
        collection.updateOne(eq("_id", pedido.getId()), updates);
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<PedidoModel> findAll() {
        List<PedidoModel> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    public List<PedidoModel> findAllByComanda(String idComanda) {
        List<PedidoModel> pedidos = new ArrayList<>();

        Bson filtro = Filters.eq("idComanda", idComanda);
        FindIterable<Document> docs = collection.find(filtro);

        for (Document doc : docs) {
            pedidos.add(fromDocument(doc));
        }

        return pedidos;
    }

    private PedidoModel fromDocument(Document doc) {
        PedidoModel pedido = new PedidoModel();
        pedido.setId(doc.getString("_id"));
        pedido.setIdComanda(doc.getString("idComanda"));
        pedido.setDataPedido(doc.getDate("dataPedido"));

        String statusStr = doc.getString("status");
        try {
            pedido.setStatus(StatusPedido.valueOf(statusStr));
        } catch (IllegalArgumentException | NullPointerException e) {
            pedido.setStatus(StatusPedido.PENDENTE);
        }

        List<Document> produtosDoc = doc.getList("produtos", Document.class);
        List<ProdutoPedidoModel> produtos = new ArrayList<>();
        if (produtosDoc != null) {
            for (Document prodDoc : produtosDoc) {
                ProdutoPedidoModel produto = new ProdutoPedidoModel();
                produto.setIdProduto(prodDoc.getString("idProduto"));

                List<AdicionalPedidoModel> adicionais = new ArrayList<>();
                List<Document> adicionaisDoc = prodDoc.getList("adicionais", Document.class);
                if (adicionaisDoc != null) {
                    for (Document adDoc : adicionaisDoc) {
                        AdicionalPedidoModel adicional = new AdicionalPedidoModel();
                        adicional.setIdAdicional(adDoc.getString("idAdicional"));

                        Object qtdRaw = adDoc.get("quantidade");
                        int quantidade;
                        if (qtdRaw instanceof Integer) {
                            quantidade = (Integer) qtdRaw;
                        } else if (qtdRaw instanceof Double) {
                            quantidade = ((Double) qtdRaw).intValue();
                        } else {
                            quantidade = 1;
                        }
                        adicional.setQuantidade(quantidade);

                        adicionais.add(adicional);
                    }
                }
                produto.setAdicionais(adicionais);
                produtos.add(produto);
            }
        }
        pedido.setProdutos(produtos);
        return pedido;
    }

    private List<Document> toProdutoDocuments(List<ProdutoPedidoModel> produtos) {
        List<Document> docs = new ArrayList<>();
        if (produtos != null) {
            for (ProdutoPedidoModel p : produtos) {
                List<Document> adicionaisDocs = new ArrayList<>();
                if (p.getAdicionais() != null) {
                    for (AdicionalPedidoModel a : p.getAdicionais()) {
                        adicionaisDocs.add(new Document("idAdicional", a.getIdAdicional())
                                .append("quantidade", a.getQuantidade()));
                    }
                }
                docs.add(new Document("idProduto", p.getIdProduto())
                        .append("adicionais", adicionaisDocs));
            }
        }
        return docs;
    }
}
