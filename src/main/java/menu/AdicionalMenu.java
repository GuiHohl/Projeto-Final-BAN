package menu;

import com.mongodb.client.MongoDatabase;
import db.ConnectionFactory;
import service.AdicionalService;

import java.sql.Connection;
import java.util.Scanner;

public class AdicionalMenu {

    public void exibirMenu() {
        try {
            MongoDatabase database = ConnectionFactory.connect("projetofinalban");

            AdicionalService service = new AdicionalService(database);
            Scanner scanner = new Scanner(System.in);
            int opcao;

            do {
                System.out.println("\n=== MENU DE ADICIONAIS ===");
                System.out.println("1 - Listar adicionais");
                System.out.println("2 - Cadastrar adicional");
                System.out.println("3 - Editar adicional");
                System.out.println("4 - Excluir adicional");
                System.out.println("0 - Voltar");
                System.out.print("Escolha uma opção: ");

                try {
                    opcao = Integer.parseInt(scanner.nextLine());

                    switch (opcao) {
                        case 1 -> service.listarAdicionais();
                        case 2 -> service.cadastrarAdicional();
                        case 3 -> service.editarAdicional();
                        case 4 -> service.excluirAdicional();
                        case 0 -> System.out.println("Voltando...");
                        default -> System.out.println("Opção inválida.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida.");
                    opcao = -1;
                }

            } while (opcao != 0);

        } catch (Exception e) {
            System.out.println("Erro no menu de adicionais:");
            e.printStackTrace();
        }
    }
}
