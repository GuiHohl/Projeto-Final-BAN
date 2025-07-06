package service;

import com.mongodb.client.MongoDatabase;
import dao.ComandaDAO;
import dao.FuncionarioDAO;
import model.ComandaModel;
import model.FuncionarioModel;
import model.enums.StatusComanda;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ComandaService {

    private final ComandaDAO comandaDAO;
    private final FuncionarioDAO funcionarioDAO;

    public ComandaService(MongoDatabase database) {
        this.comandaDAO = new ComandaDAO(database);
        this.funcionarioDAO = new FuncionarioDAO(database);
    }

    public void criarComanda() {
        Scanner scanner = new Scanner(System.in);
        try {
            FuncionarioModel funcionario = selecionarFuncionario();
            if (funcionario == null) return;

            System.out.print("Número da mesa: ");
            int numMesa = Integer.parseInt(scanner.nextLine());

            ComandaModel comanda = new ComandaModel();
            comanda.setId(UUID.randomUUID().toString());
            comanda.setIdFuncionario(funcionario.getId());
            comanda.setDataAbertura(new Date());
            comanda.setNumMesa(numMesa);
            comanda.setStatus(StatusComanda.ABERTA);

            comandaDAO.create(comanda);
            System.out.println("Comanda aberta com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao abrir comanda:");
            e.printStackTrace();
        }
    }

    public void listarComandas() {
        try {
            List<ComandaModel> lista = comandaDAO.findAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            System.out.println("\nComandas:");
            for (ComandaModel c : lista) {
                FuncionarioModel f = funcionarioDAO.read(c.getIdFuncionario());
                String nomeFuncionario = (f != null) ? f.getNome() : "Desconhecido";

                String dataFormatada = c.getDataAbertura().toInstant()
                        .atZone(TimeZone.getDefault().toZoneId())
                        .toLocalDateTime()
                        .format(formatter);

                System.out.printf("ID: %s | Mesa: %d | Aberta por: %s | Status: %s | Abertura: %s%n",
                        c.getId(), c.getNumMesa(), nomeFuncionario, c.getStatus().name(), dataFormatada);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar comandas:");
            e.printStackTrace();
        }
    }

    public void cancelarComanda() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<ComandaModel> lista = comandaDAO.findAll();
            List<ComandaModel> abertas = lista.stream()
                    .filter(c -> c.getStatus() == StatusComanda.ABERTA)
                    .toList();

            if (abertas.isEmpty()) {
                System.out.println("Nenhuma comanda em aberto encontrada.");
                return;
            }

            System.out.println("\nSelecione a comanda para cancelar:");
            for (int i = 0; i < abertas.size(); i++) {
                ComandaModel c = abertas.get(i);
                System.out.printf("%d - Comanda %s (Mesa %d)%n", i + 1, c.getId(), c.getNumMesa());
            }

            System.out.print("Digite o número da comanda: ");
            int escolha = Integer.parseInt(scanner.nextLine());

            if (escolha < 1 || escolha > abertas.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            ComandaModel selecionada = abertas.get(escolha - 1);
            selecionada.setStatus(StatusComanda.CANCELADA);
            comandaDAO.update(selecionada);

            System.out.println("Comanda " + selecionada.getId() + " foi cancelada.");

        } catch (Exception e) {
            System.out.println("Erro ao cancelar comanda:");
            e.printStackTrace();
        }
    }

    public void excluirComanda() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<ComandaModel> lista = comandaDAO.findAll();
            if (lista.isEmpty()) {
                System.out.println("Nenhuma comanda para excluir.");
                return;
            }

            System.out.println("\nSelecione a comanda para excluir:");
            for (int i = 0; i < lista.size(); i++) {
                System.out.printf("%d - Comanda %s (Mesa %d)%n",
                        i + 1, lista.get(i).getId(), lista.get(i).getNumMesa());
            }

            System.out.print("Digite o número da comanda: ");
            int escolha = Integer.parseInt(scanner.nextLine());

            if (escolha < 1 || escolha > lista.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            comandaDAO.delete(lista.get(escolha - 1).getId());
            System.out.println("Comanda excluída com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao excluir comanda:");
            e.printStackTrace();
        }
    }

    private FuncionarioModel selecionarFuncionario() throws Exception {
        Scanner scanner = new Scanner(System.in);
        List<FuncionarioModel> lista = funcionarioDAO.findAll();
        if (lista.isEmpty()) {
            System.out.println("Nenhum funcionário disponível.");
            return null;
        }

        System.out.println("\nSelecione o funcionário que abrirá a comanda:");
        for (int i = 0; i < lista.size(); i++) {
            System.out.printf("%d - %s%n", i + 1, lista.get(i).getNome());
        }

        System.out.print("Digite o número: ");
        int escolha = Integer.parseInt(scanner.nextLine());

        if (escolha < 1 || escolha > lista.size()) {
            System.out.println("Opção inválida.");
            return null;
        }

        return lista.get(escolha - 1);
    }
}
