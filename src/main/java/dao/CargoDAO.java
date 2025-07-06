package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.CargoModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class CargoDAO {

    private final MongoCollection<Document> collection;

    public CargoDAO(MongoDatabase database) {
        this.collection = database.getCollection("cargos");
    }

    public void create(CargoModel cargo) {
        Document doc = new Document()
                .append("_id", cargo.getId())
                .append("nome", cargo.getNome());
        collection.insertOne(doc);
    }

    public CargoModel read(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    public void update(CargoModel cargo) {
        Bson updates = set("nome", cargo.getNome());
        collection.updateOne(eq("_id", cargo.getId()), updates);
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<CargoModel> findAll() {
        List<CargoModel> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    private CargoModel fromDocument(Document doc) {
        CargoModel cargo = new CargoModel();
        cargo.setId(doc.getString("_id"));
        cargo.setNome(doc.getString("nome"));
        return cargo;
    }
}
