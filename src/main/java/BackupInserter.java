import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BackupInserter {

    private final MongoDatabase database;

    public BackupInserter(MongoDatabase database) {
        this.database = database;
    }

    public void importar(String nomeColecao, String arquivoJson) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("backup/" + arquivoJson);
            if (inputStream == null) {
                System.out.println("Arquivo não encontrado: " + arquivoJson);
                return;
            }

            String json = new Scanner(inputStream, StandardCharsets.UTF_8).useDelimiter("\\A").next();

            List<Document> documentos = Document.parse("{\"array\":" + json + "}").getList("array", Document.class);

            MongoCollection<Document> colecao = database.getCollection(nomeColecao);
            ReplaceOptions options = new ReplaceOptions().upsert(true);

            for (Document doc : documentos) {
                colecao.replaceOne(new Document("_id", doc.get("_id")), doc, options);
            }

            System.out.println("Inserção concluída para: " + nomeColecao);
        } catch (Exception e) {
            System.out.println("Erro ao importar " + arquivoJson);
            e.printStackTrace();
        }
    }

    public void executarTodos() {
        importar("produtos", "produtos.json");
        importar("categorias", "categorias.json");
        importar("adicionais", "adicionais.json");
        importar("funcionarios", "funcionarios.json");
        importar("cargos", "cargos.json");
        importar("comandas", "comandas.json");
        importar("pedidos", "pedidos.json");
        importar("pagamentos", "pagamentos.json");
    }
}
