package service;

import com.mongodb.client.MongoDatabase;
import dao.AdicionalDAO;
import model.AdicionalModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AdicionalService {

    private final AdicionalDAO adicionalDAO;

    public AdicionalService(MongoDatabase database) {
        this.adicionalDAO = new AdicionalDAO(database);
    }

    public void cadastrarAdicional() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Nome do adicional: ");
            String nome = scanner.nextLine();

            System.out.print("Preço do adicional: ");
            BigDecimal preco = new BigDecimal(scanner.nextLine());

            AdicionalModel adicional = new AdicionalModel();
            adicional.setId(UUID.randomUUID().toString());
            adicional.setNome(nome);
            adicional.setPreco(preco);

            adicionalDAO.create(adicional);
            System.out.println("Adicional cadastrado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar adicional:");
            e.printStackTrace();
        }
    }

    public void listarAdicionais() {
        try {
            List<AdicionalModel> lista = adicionalDAO.findAll();

            System.out.println("\nLista de adicionais:");
            for (AdicionalModel a : lista) {
                System.out.printf("ID: %s | %s | R$ %.2f%n", a.getId(), a.getNome(), a.getPreco());
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar adicionais:");
            e.printStackTrace();
        }
    }

    public void editarAdicional() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<AdicionalModel> lista = adicionalDAO.findAll();
            if (lista.isEmpty()) {
                System.out.println("Nenhum adicional encontrado.");
                return;
            }

            System.out.println("\nSelecione um adicional para editar:");
            for (int i = 0; i < lista.size(); i++) {
                System.out.printf("%d - %s%n", i + 1, lista.get(i).getNome());
            }

            System.out.print("Número do adicional: ");
            int escolha = Integer.parseInt(scanner.nextLine());

            if (escolha < 1 || escolha > lista.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            AdicionalModel adicional = lista.get(escolha - 1);

            System.out.print("Novo nome: ");
            adicional.setNome(scanner.nextLine());

            System.out.print("Novo preço: ");
            adicional.setPreco(new BigDecimal(scanner.nextLine()));

            adicionalDAO.update(adicional);
            System.out.println("Adicional atualizado!");

        } catch (Exception e) {
            System.out.println("Erro ao editar adicional:");
            e.printStackTrace();
        }
    }

    public void excluirAdicional() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<AdicionalModel> lista = adicionalDAO.findAll();
            if (lista.isEmpty()) {
                System.out.println("Nenhum adicional para excluir.");
                return;
            }

            System.out.println("\nSelecione o adicional a excluir:");
            for (int i = 0; i < lista.size(); i++) {
                System.out.printf("%d - %s%n", i + 1, lista.get(i).getNome());
            }

            System.out.print("Número do adicional: ");
            int escolha = Integer.parseInt(scanner.nextLine());

            if (escolha < 1 || escolha > lista.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            adicionalDAO.delete(lista.get(escolha - 1).getId());
            System.out.println("Adicional removido!");

        } catch (Exception e) {
            System.out.println("Erro ao excluir adicional:");
            e.printStackTrace();
        }
    }
}
