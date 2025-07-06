package menu;

import com.mongodb.client.MongoDatabase;
import db.ConnectionFactory;
import service.ProdutoService;
import service.RelatorioService;

import java.sql.Connection;
import java.util.Scanner;

public class ProdutoMenu {

    public void exibirMenu() {
        try {
            MongoDatabase database = ConnectionFactory.connect("projetofinalban");

            ProdutoService service = new ProdutoService(database);
            Scanner scanner = new Scanner(System.in);
            int opcao;

            do {
                System.out.println("\n=== MENU DE PRODUTOS ===");
                System.out.println("1 - Listar produtos");
                System.out.println("2 - Cadastrar novo produto");
                System.out.println("3 - Editar produto");
                System.out.println("4 - Excluir produto");
                System.out.println("0 - Voltar");
                System.out.print("Escolha uma opção: ");

                try {
                    opcao = Integer.parseInt(scanner.nextLine());

                    switch (opcao) {
                        case 1 -> service.listarProdutos();
                        case 2 -> service.cadastrarProduto();
                        case 3 -> service.editarProduto();
                        case 4 -> service.excluirProduto();
                        case 0 -> System.out.println("Retornando ao menu principal...");
                        default -> System.out.println("Opção inválida.");
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida.");
                    opcao = -1;
                }

            } while (opcao != 0);
        } catch (Exception e) {
            System.out.println("Erro no menu de produtos:");
            e.printStackTrace();
        }
    }
}
