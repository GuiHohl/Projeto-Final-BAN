package service;

import com.mongodb.client.MongoDatabase;
import dao.*;
import model.*;
import model.enums.StatusComanda;
import model.enums.StatusPedido;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final ComandaDAO comandaDAO;
    private final ProdutoDAO produtoDAO;
    private final AdicionalDAO adicionalDAO;

    public PedidoService(MongoDatabase database) {
        this.pedidoDAO = new PedidoDAO(database);
        this.comandaDAO = new ComandaDAO(database);
        this.produtoDAO = new ProdutoDAO(database);
        this.adicionalDAO = new AdicionalDAO(database);
    }

    public void criarPedido() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<ComandaModel> comandas = comandaDAO.findAll();
            List<ComandaModel> abertas = comandas.stream()
                    .filter(c -> c.getStatus() == StatusComanda.ABERTA)
                    .toList();

            if (abertas.isEmpty()) {
                System.out.println("Nenhuma comanda aberta encontrada.");
                return;
            }

            System.out.println("\nSelecione a comanda:");
            for (int i = 0; i < abertas.size(); i++) {
                ComandaModel c = abertas.get(i);
                System.out.printf("%d - Comanda #%s | Mesa %d%n", i + 1, c.getId(), c.getNumMesa());
            }

            int escolha = Integer.parseInt(scanner.nextLine());
            if (escolha < 1 || escolha > abertas.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            ComandaModel comandaSelecionada = abertas.get(escolha - 1);

            PedidoModel pedido = new PedidoModel();
            pedido.setId(UUID.randomUUID().toString());
            pedido.setIdComanda(comandaSelecionada.getId());
            pedido.setDataPedido(new Date());
            pedido.setStatus(StatusPedido.EM_PREPARO);

            List<ProdutoPedidoModel> listaProdutos = new ArrayList<>();

            boolean continuar = true;
            while (continuar) {
                List<ProdutoModel> produtos = produtoDAO.findAll();
                for (int i = 0; i < produtos.size(); i++) {
                    ProdutoModel p = produtos.get(i);
                    System.out.printf("%d - %s (R$ %.2f)%n", i + 1, p.getNome(), p.getPreco());
                }

                System.out.print("Selecione o produto: ");
                int prodIndex = Integer.parseInt(scanner.nextLine()) - 1;

                if (prodIndex < 0 || prodIndex >= produtos.size()) {
                    System.out.println("Produto inválido.");
                    continue;
                }

                ProdutoModel produtoSelecionado = produtos.get(prodIndex);
                ProdutoPedidoModel produtoPedido = new ProdutoPedidoModel();
                produtoPedido.setIdProduto(produtoSelecionado.getId());

                List<AdicionalPedidoModel> adicionaisSelecionados = new ArrayList<>();

                System.out.print("Deseja adicionar adicionais para este produto? (s/n): ");
                if (scanner.nextLine().equalsIgnoreCase("s")) {
                    List<AdicionalModel> adicionais = adicionalDAO.findAll();
                    for (int i = 0; i < adicionais.size(); i++) {
                        AdicionalModel a = adicionais.get(i);
                        System.out.printf("%d - %s (R$ %.2f)%n", i + 1, a.getNome(), a.getPreco());
                    }

                    while (true) {
                        System.out.print("Número do adicional (ou 0 para encerrar): ");
                        int adIndex = Integer.parseInt(scanner.nextLine());
                        if (adIndex == 0) break;

                        if (adIndex < 1 || adIndex > adicionais.size()) {
                            System.out.println("Inválido.");
                            continue;
                        }

                        AdicionalModel adicionalSel = adicionais.get(adIndex - 1);
                        System.out.print("Quantidade: ");
                        int qtd = Integer.parseInt(scanner.nextLine());

                        AdicionalPedidoModel adicionalPedido = new AdicionalPedidoModel();
                        adicionalPedido.setIdAdicional(adicionalSel.getId());
                        adicionalPedido.setQuantidade(qtd);

                        adicionaisSelecionados.add(adicionalPedido);
                    }
                }

                produtoPedido.setAdicionais(adicionaisSelecionados);
                listaProdutos.add(produtoPedido);

                System.out.print("Adicionar outro produto ao pedido? (s/n): ");
                continuar = scanner.nextLine().equalsIgnoreCase("s");
            }

            pedido.setProdutos(listaProdutos);
            pedidoDAO.create(pedido);
            System.out.println("Pedido registrado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao registrar pedido:");
            e.printStackTrace();
        }
    }

    public void listarPedidos() {
        try {
            List<PedidoModel> pedidos = pedidoDAO.findAll();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (PedidoModel pedido : pedidos) {
                System.out.printf("\nPedido #%s | Comanda: %s | Status: %s | Data: %s%n",
                        pedido.getId(),
                        pedido.getIdComanda(),
                        pedido.getStatus().name(),
                        sdf.format(pedido.getDataPedido()));

                BigDecimal total = BigDecimal.ZERO;

                for (ProdutoPedidoModel produto : pedido.getProdutos()) {
                    ProdutoModel prod = produtoDAO.read(produto.getIdProduto());
                    if (prod != null) {
                        System.out.printf("  %s (R$ %.2f)%n", prod.getNome(), prod.getPreco());
                        total = total.add(prod.getPreco());
                    }

                    for (AdicionalPedidoModel ad : produto.getAdicionais()) {
                        AdicionalModel adicional = adicionalDAO.read(ad.getIdAdicional());
                        if (adicional != null) {
                            BigDecimal sub = adicional.getPreco().multiply(BigDecimal.valueOf(ad.getQuantidade()));
                            total = total.add(sub);
                            System.out.printf("    %s x%d (R$ %.2f)%n", adicional.getNome(), ad.getQuantidade(), sub);
                        }
                    }
                }

                System.out.printf("  Total: R$ %.2f%n", total);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar pedidos:");
            e.printStackTrace();
        }
    }

    public void alterarStatusPedido() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<PedidoModel> lista = pedidoDAO.findAll();

            if (lista.isEmpty()) {
                System.out.println("Nenhum pedido para atualizar.");
                return;
            }

            System.out.println("Selecione o pedido para alterar o status:");
            for (int i = 0; i < lista.size(); i++) {
                PedidoModel p = lista.get(i);
                System.out.printf("%d - Pedido #%s | Status atual: %s%n", i + 1, p.getId(), p.getStatus());
            }

            System.out.print("Digite o número: ");
            int escolha = Integer.parseInt(scanner.nextLine());
            if (escolha < 1 || escolha > lista.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            PedidoModel pedido = lista.get(escolha - 1);

            System.out.println("Selecione o novo status:");
            StatusPedido[] statusList = StatusPedido.values();
            for (int i = 0; i < statusList.length; i++) {
                System.out.printf("%d - %s%n", i + 1, statusList[i].name());
            }

            System.out.print("Digite o número correspondente: ");
            int statusEscolhido = Integer.parseInt(scanner.nextLine());
            if (statusEscolhido < 1 || statusEscolhido > statusList.length) {
                System.out.println("Status inválido.");
                return;
            }

            pedido.setStatus(statusList[statusEscolhido - 1]);

            pedidoDAO.update(pedido);
            System.out.println("Status atualizado com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao alterar status do pedido:");
            e.printStackTrace();
        }
    }

    public void excluirPedido() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<PedidoModel> lista = pedidoDAO.findAll();
            if (lista.isEmpty()) {
                System.out.println("Nenhum pedido para excluir.");
                return;
            }

            System.out.println("Selecione o pedido para exclusão:");
            for (int i = 0; i < lista.size(); i++) {
                PedidoModel p = lista.get(i);
                System.out.printf("%d - Pedido #%s (Comanda: %s)%n", i + 1, p.getId(), p.getIdComanda());
            }

            int escolha = Integer.parseInt(scanner.nextLine());
            if (escolha < 1 || escolha > lista.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            pedidoDAO.delete(lista.get(escolha - 1).getId());
            System.out.println("Pedido excluído com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao excluir pedido:");
            e.printStackTrace();
        }
    }
}
