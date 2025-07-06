package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.AdicionalModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class AdicionalDAO {

    private final MongoCollection<Document> collection;

    public AdicionalDAO(MongoDatabase database) {
        this.collection = database.getCollection("adicionais");
    }

    public void create(AdicionalModel adicional) {
        Document doc = new Document()
                .append("_id", adicional.getId())
                .append("nome", adicional.getNome())
                .append("preco", adicional.getPreco().doubleValue());
        collection.insertOne(doc);
    }

    public AdicionalModel read(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        return (doc != null) ? fromDocument(doc) : null;
    }

    public void update(AdicionalModel adicional) {
        Bson updates = combine(
                set("nome", adicional.getNome()),
                set("preco", adicional.getPreco().doubleValue())
        );
        collection.updateOne(eq("_id", adicional.getId()), updates);
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<AdicionalModel> findAll() {
        List<AdicionalModel> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    private AdicionalModel fromDocument(Document doc) {
        AdicionalModel adicional = new AdicionalModel();
        adicional.setId(doc.getString("_id"));
        adicional.setNome(doc.getString("nome"));

        Object raw = doc.get("preco");
        BigDecimal preco;

        if (raw instanceof BigDecimal) {
            preco = (BigDecimal) raw;
        } else if (raw instanceof Double) {
            preco = BigDecimal.valueOf((Double) raw);
        } else if (raw instanceof Integer) {
            preco = BigDecimal.valueOf((Integer) raw);
        } else {
            preco = BigDecimal.ZERO;
        }

        adicional.setPreco(preco);
        return adicional;
    }
}
