package dao;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dto.RelatorioProdutosMaisVendidosDTO;
import model.ProdutoModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class ProdutoDAO {

    private final MongoCollection<Document> collection;
    private final MongoDatabase database;

    public ProdutoDAO(MongoDatabase database) {
        this.database = database;
        this.collection = database.getCollection("produtos");
    }

    public void create(ProdutoModel produto) {
        Document doc = new Document()
                .append("_id", produto.getId())
                .append("idCategoria", produto.getIdCategoria())
                .append("nome", produto.getNome())
                .append("descricao", produto.getDescricao())
                .append("preco", produto.getPreco().doubleValue());
        collection.insertOne(doc);
    }

    public ProdutoModel read(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        return (doc != null) ? fromDocument(doc) : null;
    }

    public void update(ProdutoModel produto) {
        Bson updates = combine(
                set("idCategoria", produto.getIdCategoria()),
                set("nome", produto.getNome()),
                set("descricao", produto.getDescricao()),
                set("preco", produto.getPreco().doubleValue())
        );
        collection.updateOne(eq("_id", produto.getId()), updates);
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<ProdutoModel> findAll() {
        List<ProdutoModel> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    public List<RelatorioProdutosMaisVendidosDTO> findProdutosMaisVendidos() {
        MongoCollection<Document> pedidos = database.getCollection("pedidos");
        List<RelatorioProdutosMaisVendidosDTO> lista = new ArrayList<>();

        List<Bson> pipeline = Arrays.asList(
                new Document("$unwind", "$produtos"),
                new Document("$group", new Document("_id", "$produtos.idProduto")
                        .append("vezes_vendido", new Document("$sum", 1))),
                new Document("$lookup", new Document("from", "produtos")
                        .append("localField", "_id")
                        .append("foreignField", "_id")
                        .append("as", "produto")),
                new Document("$unwind", "$produto"),
                new Document("$project", new Document("nome", "$produto.nome")
                        .append("vezes_vendido", 1)
                        .append("faturamento", new Document("$multiply", Arrays.asList("$vezes_vendido", "$produto.preco")))),
                new Document("$sort", new Document("vezes_vendido", -1).append("faturamento", -1))
        );

        AggregateIterable<Document> result = pedidos.aggregate(pipeline);

        for (Document doc : result) {
            Object rawFaturamento = doc.get("faturamento");
            BigDecimal faturamento;

            if (rawFaturamento instanceof BigDecimal) {
                faturamento = (BigDecimal) rawFaturamento;
            } else if (rawFaturamento instanceof Double) {
                faturamento = BigDecimal.valueOf((Double) rawFaturamento);
            } else if (rawFaturamento instanceof Integer) {
                faturamento = BigDecimal.valueOf((Integer) rawFaturamento);
            } else {
                faturamento = BigDecimal.ZERO;
            }

            lista.add(new RelatorioProdutosMaisVendidosDTO(
                    doc.getString("nome"),
                    doc.getInteger("vezes_vendido"),
                    faturamento
            ));
        }

        return lista;
    }

    private ProdutoModel fromDocument(Document doc) {
        ProdutoModel produto = new ProdutoModel();
        produto.setId(doc.getString("_id"));
        produto.setIdCategoria(doc.getString("idCategoria"));
        produto.setNome(doc.getString("nome"));
        produto.setDescricao(doc.getString("descricao"));

        Object rawPreco = doc.get("preco");
        BigDecimal preco;

        if (rawPreco instanceof BigDecimal) {
            preco = (BigDecimal) rawPreco;
        } else if (rawPreco instanceof Double) {
            preco = BigDecimal.valueOf((Double) rawPreco);
        } else if (rawPreco instanceof Integer) {
            preco = BigDecimal.valueOf((Integer) rawPreco);
        } else {
            preco = BigDecimal.ZERO;
        }

        produto.setPreco(preco);
        return produto;
    }
}
