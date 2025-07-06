package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.ComandaModel;
import model.enums.StatusComanda;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class ComandaDAO {

    private final MongoCollection<Document> collection;

    public ComandaDAO(MongoDatabase database) {
        this.collection = database.getCollection("comandas");
    }

    public void create(ComandaModel comanda) {
        Document doc = new Document()
                .append("_id", comanda.getId())
                .append("idFuncionario", comanda.getIdFuncionario())
                .append("dataAbertura", comanda.getDataAbertura())
                .append("status", comanda.getStatus().name())
                .append("numMesa", comanda.getNumMesa());
        collection.insertOne(doc);
    }

    public ComandaModel read(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    public void update(ComandaModel comanda) {
        Bson updates = combine(
                set("idFuncionario", comanda.getIdFuncionario()),
                set("dataAbertura", comanda.getDataAbertura()),
                set("status", comanda.getStatus().name()),
                set("numMesa", comanda.getNumMesa())
        );
        collection.updateOne(eq("_id", comanda.getId()), updates);
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<ComandaModel> findAll() {
        List<ComandaModel> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    private ComandaModel fromDocument(Document doc) {
        ComandaModel comanda = new ComandaModel();
        comanda.setId(doc.getString("_id"));
        comanda.setIdFuncionario(doc.getString("idFuncionario"));
        comanda.setDataAbertura(doc.getDate("dataAbertura"));

        String statusStr = doc.getString("status");
        try {
            comanda.setStatus(StatusComanda.valueOf(statusStr));
        } catch (IllegalArgumentException | NullPointerException e) {
            comanda.setStatus(StatusComanda.ABERTA);
        }

        comanda.setNumMesa(doc.getInteger("numMesa"));
        return comanda;
    }
}
