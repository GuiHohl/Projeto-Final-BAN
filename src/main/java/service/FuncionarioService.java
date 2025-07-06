package service;

import com.mongodb.client.MongoDatabase;
import dao.CargoDAO;
import dao.FuncionarioDAO;
import model.CargoModel;
import model.FuncionarioModel;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class FuncionarioService {

    private final FuncionarioDAO funcionarioDAO;
    private final CargoDAO cargoDAO;

    public FuncionarioService(MongoDatabase database) {
        this.funcionarioDAO = new FuncionarioDAO(database);
        this.cargoDAO = new CargoDAO(database);
    }

    public void criarFuncionarioViaConsole() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Nome do funcionário: ");
            String nome = scanner.nextLine();

            CargoModel cargo = selecionarCargo();
            if (cargo == null) return;

            FuncionarioModel funcionario = new FuncionarioModel();
            funcionario.setId(UUID.randomUUID().toString());
            funcionario.setNome(nome);
            funcionario.setIdCargo(cargo.getId());

            funcionarioDAO.create(funcionario);
            System.out.println("Funcionário cadastrado!");

        } catch (Exception e) {
            System.out.println("Erro ao criar funcionário:");
            e.printStackTrace();
        }
    }

    public void listarFuncionarios() {
        try {
            List<FuncionarioModel> lista = funcionarioDAO.findAll();

            System.out.println("\nFuncionários:");
            for (FuncionarioModel f : lista) {
                CargoModel cargo = cargoDAO.read(f.getIdCargo());
                String cargoNome = (cargo != null) ? cargo.getNome() : "Desconhecido";

                System.out.printf("ID: %s | Nome: %s | Cargo: %s%n",
                        f.getId(), f.getNome(), cargoNome);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar funcionários:");
            e.printStackTrace();
        }
    }

    public void editarFuncionario() {
        try {
            FuncionarioModel funcionario = selecionarFuncionario();
            if (funcionario == null) return;

            Scanner scanner = new Scanner(System.in);
            System.out.print("Novo nome para \"" + funcionario.getNome() + "\": ");
            String novoNome = scanner.nextLine();

            CargoModel novoCargo = selecionarCargo();
            if (novoCargo == null) return;

            funcionario.setNome(novoNome);
            funcionario.setIdCargo(novoCargo.getId());

            funcionarioDAO.update(funcionario);
            System.out.println("Funcionário atualizado!");

        } catch (Exception e) {
            System.out.println("Erro ao editar funcionário:");
            e.printStackTrace();
        }
    }

    public void excluirFuncionario() {
        try {
            FuncionarioModel funcionario = selecionarFuncionario();
            if (funcionario == null) return;

            funcionarioDAO.delete(funcionario.getId());
            System.out.println("Funcionário excluído!");

        } catch (Exception e) {
            System.out.println("Erro ao excluir funcionário:");
            e.printStackTrace();
        }
    }

    private FuncionarioModel selecionarFuncionario() throws Exception {
        List<FuncionarioModel> lista = funcionarioDAO.findAll();
        if (lista.isEmpty()) {
            System.out.println("Nenhum funcionário encontrado.");
            return null;
        }

        System.out.println("\nSelecione um funcionário:");
        for (int i = 0; i < lista.size(); i++) {
            FuncionarioModel f = lista.get(i);
            CargoModel cargo = cargoDAO.read(f.getIdCargo());
            String cargoNome = (cargo != null) ? cargo.getNome() : "Desconhecido";
            System.out.printf("%d - %s (%s)%n", i + 1, f.getNome(), cargoNome);
        }

        System.out.print("Digite o número: ");
        Scanner scanner = new Scanner(System.in);
        int escolha = Integer.parseInt(scanner.nextLine());

        if (escolha < 1 || escolha > lista.size()) {
            System.out.println("Opção inválida.");
            return null;
        }

        return lista.get(escolha - 1);
    }

    private CargoModel selecionarCargo() throws Exception {
        List<CargoModel> cargos = cargoDAO.findAll();
        if (cargos.isEmpty()) {
            System.out.println("Nenhum cargo encontrado.");
            return null;
        }

        System.out.println("\nSelecione um cargo:");
        for (int i = 0; i < cargos.size(); i++) {
            System.out.printf("%d - %s%n", i + 1, cargos.get(i).getNome());
        }

        System.out.print("Digite o número: ");
        Scanner scanner = new Scanner(System.in);
        int escolha = Integer.parseInt(scanner.nextLine());

        if (escolha < 1 || escolha > cargos.size()) {
            System.out.println("Opção inválida.");
            return null;
        }

        return cargos.get(escolha - 1);
    }
}
