import com.mongodb.client.MongoDatabase;
import db.ConnectionFactory;
import menu.MainMenu;


public class Main {
    public static void main(String[] args) {
        MongoDatabase database = ConnectionFactory.connect("projetofinalban");
        BackupInserter inserter = new BackupInserter(database);
        inserter.executarTodos();

        new MainMenu().exibirMenu();
    }
}
