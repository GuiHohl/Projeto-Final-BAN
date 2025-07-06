package dao;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dto.RelatorioFuncionariosComMaisComandasDTO;
import model.FuncionarioModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class FuncionarioDAO {

    private final MongoCollection<Document> collection;
    private final MongoDatabase database;

    public FuncionarioDAO(MongoDatabase database) {
        this.database = database;
        this.collection = database.getCollection("funcionarios");
    }

    public void create(FuncionarioModel funcionario) {
        Document doc = new Document()
                .append("_id", funcionario.getId())
                .append("nome", funcionario.getNome())
                .append("idCargo", funcionario.getIdCargo());
        collection.insertOne(doc);
    }

    public FuncionarioModel read(String id) {
        Document doc = collection.find(eq("_id", id)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    public void update(FuncionarioModel funcionario) {
        Bson updates = combine(
                set("nome", funcionario.getNome()),
                set("idCargo", funcionario.getIdCargo())
        );
        collection.updateOne(eq("_id", funcionario.getId()), updates);
    }

    public void delete(String id) {
        collection.deleteOne(eq("_id", id));
    }

    public List<FuncionarioModel> findAll() {
        List<FuncionarioModel> lista = new ArrayList<>();
        for (Document doc : collection.find()) {
            lista.add(fromDocument(doc));
        }
        return lista;
    }

    public List<RelatorioFuncionariosComMaisComandasDTO> findFuncionariosComMaisComandas() {
        MongoCollection<Document> comandaCollection = database.getCollection("comandas");
        MongoCollection<Document> cargoCollection = database.getCollection("cargos");

        List<RelatorioFuncionariosComMaisComandasDTO> resultado = new ArrayList<>();

        List<Bson> pipeline = Arrays.asList(
                new Document("$lookup",
                        new Document("from", "comandas")
                                .append("localField", "_id")
                                .append("foreignField", "idFuncionario")
                                .append("as", "comandas")),
                new Document("$lookup",
                        new Document("from", "cargos")
                                .append("localField", "idCargo")
                                .append("foreignField", "_id")
                                .append("as", "cargo")),
                new Document("$unwind", "$cargo"),
                new Document("$project",
                        new Document("nome", 1)
                                .append("cargo", "$cargo.nome")
                                .append("total_comandas", new Document("$size", "$comandas"))),
                new Document("$sort", new Document("total_comandas", -1))
        );

        AggregateIterable<Document> aggregate = collection.aggregate(pipeline);
        for (Document doc : aggregate) {
            resultado.add(new RelatorioFuncionariosComMaisComandasDTO(
                    doc.getString("nome"),
                    doc.getString("cargo"),
                    doc.getInteger("total_comandas")
            ));
        }

        return resultado;
    }

    private FuncionarioModel fromDocument(Document doc) {
        FuncionarioModel f = new FuncionarioModel();
        f.setId(doc.getString("_id"));
        f.setNome(doc.getString("nome"));
        f.setIdCargo(doc.getString("idCargo"));
        return f;
    }
}
