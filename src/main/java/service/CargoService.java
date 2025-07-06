package service;

import com.mongodb.client.MongoDatabase;
import dao.CargoDAO;
import model.CargoModel;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class CargoService {

    private final CargoDAO cargoDAO;

    public CargoService(MongoDatabase database) {
        this.cargoDAO = new CargoDAO(database);
    }

    public void criarCargo() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o nome do novo cargo:");
        String nome = scanner.nextLine();

        CargoModel novoCargo = new CargoModel();
        novoCargo.setId(UUID.randomUUID().toString());
        novoCargo.setNome(nome);

        try {
            cargoDAO.create(novoCargo);
            System.out.println("Cargo cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar cargo:");
            e.printStackTrace();
        }
    }

    public void listarCargos() {
        try {
            List<CargoModel> cargos = cargoDAO.findAll();
            System.out.println("\nLista de Cargos:");
            for (CargoModel cargo : cargos) {
                System.out.printf("ID: %s | Nome: %s%n", cargo.getId(), cargo.getNome());
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar cargos:");
            e.printStackTrace();
        }
    }

    public void editarCargo() {
        try {
            CargoModel cargoSelecionado = selecionarCargo();
            if (cargoSelecionado == null) return;

            Scanner scanner = new Scanner(System.in);
            System.out.print("Novo nome para o cargo \"" + cargoSelecionado.getNome() + "\": ");
            String novoNome = scanner.nextLine();

            cargoSelecionado.setNome(novoNome);
            cargoDAO.update(cargoSelecionado);

            System.out.println("Cargo atualizado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao editar cargo:");
            e.printStackTrace();
        }
    }

    public void excluirCargo() {
        try {
            CargoModel cargoSelecionado = selecionarCargo();
            if (cargoSelecionado == null) return;

            cargoDAO.delete(cargoSelecionado.getId());
            System.out.println("Cargo excluído com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao excluir cargo:");
            e.printStackTrace();
        }
    }

    private CargoModel selecionarCargo() {
        try {
            List<CargoModel> cargos = cargoDAO.findAll();
            if (cargos.isEmpty()) {
                System.out.println("Nenhum cargo cadastrado.");
                return null;
            }

            System.out.println("\nSelecione um cargo:");
            for (int i = 0; i < cargos.size(); i++) {
                System.out.printf("%d - %s%n", i + 1, cargos.get(i).getNome());
            }

            System.out.print("Digite o número correspondente: ");
            Scanner scanner = new Scanner(System.in);
            int escolha = Integer.parseInt(scanner.nextLine());

            if (escolha < 1 || escolha > cargos.size()) {
                System.out.println("Opção inválida.");
                return null;
            }

            return cargos.get(escolha - 1);

        } catch (Exception e) {
            System.out.println("Erro ao selecionar cargo:");
            e.printStackTrace();
            return null;
        }
    }
}
