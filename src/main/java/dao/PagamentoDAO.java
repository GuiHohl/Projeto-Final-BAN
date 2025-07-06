package dao;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dto.RelatorioFaturamentoDiarioDTO;
import dto.RelatorioVendasPorMetodoPagamentoDTO;
import model.PagamentoModel;
import model.enums.MetodoPagamento;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigDecimal;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class PagamentoDAO {

    private final MongoCollection<Document> collection;

    public PagamentoDAO(MongoDatabase database) {
        this.collection = database.getCollection("pagamentos");
    }

    public void create(PagamentoModel pagamento) {
        Document doc = new Document()
                .append("_id", pagamento.getId())
                .append("idComanda", pagamento.getIdComanda())
                .append("valor", pagamento.getValor().doubleValue())
                .append("metodoPagamento", pagamento.getMetodoPagamento().name())
                .append("dataPagamento", pagamento.getDataPagamento());
        collection.insertOne(doc);
    }

    public PagamentoModel read(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        return (doc != null) ? fromDocument(doc) : null;
    }

    public void update(PagamentoModel pagamento) {
        Bson updates = combine(
                set("idComanda", pagamento.getIdComanda()),
                set("valor", pagamento.getValor().doubleValue()),
                set("metodoPagamento", pagamento.getMetodoPagamento().name()),
                set("dataPagamento", pagamento.getDataPagamento())
        );
        collection.updateOne(eq("_id", pagamento.getId()), updates);
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<PagamentoModel> findAll() {
        List<PagamentoModel> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    public List<RelatorioFaturamentoDiarioDTO> findFaturamentoDiario() {
        List<RelatorioFaturamentoDiarioDTO> lista = new ArrayList<>();

        List<Bson> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", new Document("$dateToString", new Document("format", "%Y-%m-%d")
                        .append("date", "$dataPagamento")))
                        .append("total_comandas", new Document("$sum", 1))
                        .append("faturamento_total", new Document("$sum", "$valor"))),
                new Document("$sort", new Document("_id", -1))
        );

        AggregateIterable<Document> result = collection.aggregate(pipeline);

        for (Document doc : result) {
            Double totalDouble = doc.getDouble("faturamento_total");
            BigDecimal faturamento = BigDecimal.valueOf(totalDouble);

            lista.add(new RelatorioFaturamentoDiarioDTO(
                    doc.getString("_id"),
                    doc.getInteger("total_comandas"),
                    faturamento
            ));
        }

        return lista;
    }

    public List<RelatorioVendasPorMetodoPagamentoDTO> findVendasPorMetodo() {
        List<RelatorioVendasPorMetodoPagamentoDTO> lista = new ArrayList<>();

        List<Bson> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$metodoPagamento")
                        .append("total_pagamentos", new Document("$sum", 1))
                        .append("total_vendas", new Document("$sum", "$valor"))),
                new Document("$sort", new Document("total_vendas", -1))
        );

        AggregateIterable<Document> result = collection.aggregate(pipeline);

        for (Document doc : result) {
            Double totalDouble = doc.getDouble("total_vendas");
            BigDecimal totalVendas = BigDecimal.valueOf(totalDouble);

            lista.add(new RelatorioVendasPorMetodoPagamentoDTO(
                    doc.getString("_id"),
                    doc.getInteger("total_pagamentos"),
                    totalVendas
            ));
        }

        return lista;
    }

    private PagamentoModel fromDocument(Document doc) {
        PagamentoModel p = new PagamentoModel();
        p.setId(doc.getString("_id"));
        p.setIdComanda(doc.getString("idComanda"));

        Object valorRaw = doc.get("valor");
        BigDecimal valor;
        if (valorRaw instanceof BigDecimal) {
            valor = (BigDecimal) valorRaw;
        } else if (valorRaw instanceof Double) {
            valor = BigDecimal.valueOf((Double) valorRaw);
        } else if (valorRaw instanceof Integer) {
            valor = BigDecimal.valueOf((Integer) valorRaw);
        } else {
            valor = BigDecimal.ZERO;
        }
        p.setValor(valor);

        String metodoStr = doc.getString("metodoPagamento");
        try {
            p.setMetodoPagamento(MetodoPagamento.valueOf(metodoStr));
        } catch (IllegalArgumentException | NullPointerException e) {
            p.setMetodoPagamento(MetodoPagamento.DINHEIRO);
        }

        p.setDataPagamento(doc.getDate("dataPagamento"));
        return p;
    }
}
