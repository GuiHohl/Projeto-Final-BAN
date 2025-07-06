package db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConnectionFactory {

    private static final String CONNECTION_STRING =
            "mongodb+srv://guihohl:sTMDfIaxcpBd1UVj@projetofinalban.lsp7kfa.mongodb.net/?retryWrites=true&w=majority&appName=ProjetoFinalBAN";

    public static MongoDatabase connect(String dbName) {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        return mongoClient.getDatabase(dbName);
    }
}
