package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.CategoriaModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class CategoriaDAO {

    private final MongoCollection<Document> collection;

    public CategoriaDAO(MongoDatabase database) {
        this.collection = database.getCollection("categorias");
    }

    public void create(CategoriaModel categoria) {
        Document doc = new Document()
                .append("_id", categoria.getId())
                .append("nome", categoria.getNome());
        collection.insertOne(doc);
    }

    public CategoriaModel read(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    public void update(CategoriaModel categoria) {
        Bson update = set("nome", categoria.getNome());
        collection.updateOne(eq("_id", categoria.getId()), update);
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<CategoriaModel> findAll() {
        List<CategoriaModel> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    private CategoriaModel fromDocument(Document doc) {
        CategoriaModel categoria = new CategoriaModel();
        categoria.setId(doc.getString("_id"));
        categoria.setNome(doc.getString("nome"));
        return categoria;
    }
}
