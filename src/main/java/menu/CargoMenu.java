package menu;

import com.mongodb.client.MongoDatabase;
import db.ConnectionFactory;
import service.CargoService;

import java.sql.Connection;
import java.util.Scanner;

public class CargoMenu {

    public void exibirMenu() {
        try {
            MongoDatabase database = ConnectionFactory.connect("projetofinalban");

            CargoService cargoService = new CargoService(database);
            Scanner scanner = new Scanner(System.in);
            int opcao;

            do {
                System.out.println("\n=== MENU DE CARGOS ===");
                System.out.println("1 - Listar cargos");
                System.out.println("2 - Cadastrar novo cargo");
                System.out.println("3 - Editar cargo existente");
                System.out.println("4 - Excluir cargo");
                System.out.println("0 - Sair");
                System.out.print("Escolha uma opção: ");

                opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        cargoService.listarCargos();
                        break;
                    case 2:
                        cargoService.criarCargo();
                        break;
                    case 3:
                        cargoService.editarCargo();
                        break;
                    case 4:
                        cargoService.excluirCargo();
                        break;
                    case 0:
                        System.out.println("Saindo do menu de cargos...");
                        break;
                    default:
                        System.out.println("Opção inválida, tente novamente.");
                }

            } while (opcao != 0);

        } catch (Exception e) {
            System.out.println("Erro ao acessar o menu de cargos:");
            e.printStackTrace();
        }
    }
}
