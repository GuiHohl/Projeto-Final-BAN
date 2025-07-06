package service;

import com.mongodb.client.MongoDatabase;
import dao.*;
import model.*;
import model.enums.MetodoPagamento;
import model.enums.StatusComanda;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PagamentoService {

    private final PagamentoDAO pagamentoDAO;
    private final ComandaDAO comandaDAO;
    private final PedidoDAO pedidoDAO;
    private final ProdutoDAO produtoDAO;
    private final AdicionalDAO adicionalDAO;

    public PagamentoService(MongoDatabase database) {
        this.pagamentoDAO = new PagamentoDAO(database);
        this.comandaDAO = new ComandaDAO(database);
        this.pedidoDAO = new PedidoDAO(database);
        this.produtoDAO = new ProdutoDAO(database);
        this.adicionalDAO = new AdicionalDAO(database);
    }

    public void registrarPagamento() {
        Scanner scanner = new Scanner(System.in);

        try {
            List<ComandaModel> comandas = comandaDAO.findAll();
            List<PagamentoModel> pagamentos = pagamentoDAO.findAll();

            Set<String> comandasPagas = pagamentos.stream()
                    .map(PagamentoModel::getIdComanda)
                    .collect(Collectors.toSet());

            List<ComandaModel> abertasSemPagamento = comandas.stream()
                    .filter(c -> c.getStatus() == StatusComanda.ABERTA && !comandasPagas.contains(c.getId()))
                    .toList();

            if (abertasSemPagamento.isEmpty()) {
                System.out.println("Nenhuma comanda aberta sem pagamento.");
                return;
            }

            System.out.println("\nSelecione a comanda para pagamento:");
            for (int i = 0; i < abertasSemPagamento.size(); i++) {
                ComandaModel c = abertasSemPagamento.get(i);
                System.out.printf("%d - Comanda #%s | Mesa: %d%n", i + 1, c.getId(), c.getNumMesa());
            }

            System.out.print("Digite o número: ");
            int escolha = Integer.parseInt(scanner.nextLine());
            if (escolha < 1 || escolha > abertasSemPagamento.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            ComandaModel comanda = abertasSemPagamento.get(escolha - 1);
            BigDecimal total = calcularTotalComanda(comanda.getId());

            System.out.printf("Valor total da comanda: R$ %.2f%n", total);
            System.out.print("Valor do pagamento: ");
            BigDecimal valor = new BigDecimal(scanner.nextLine());

            System.out.print("Método de pagamento (DINHEIRO, CARTAO, PIX): ");
            String metodoStr = scanner.nextLine().toUpperCase();

            MetodoPagamento metodo;
            try {
                metodo = MetodoPagamento.valueOf(metodoStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Método de pagamento inválido.");
                return;
            }

            PagamentoModel pagamento = new PagamentoModel();
            pagamento.setIdComanda(comanda.getId());
            pagamento.setValor(valor);
            pagamento.setMetodoPagamento(metodo);
            pagamento.setDataPagamento(new Date());

            pagamentoDAO.create(pagamento);

            comanda.setStatus(StatusComanda.FECHADA);
            comandaDAO.update(comanda);

            System.out.println("Pagamento registrado com sucesso e comanda fechada!");

        } catch (Exception e) {
            System.out.println("Erro ao registrar pagamento:");
            e.printStackTrace();
        }
    }

    private BigDecimal calcularTotalComanda(String idComanda) throws Exception {
        BigDecimal total = BigDecimal.ZERO;

        List<PedidoModel> pedidos = pedidoDAO.findAllByComanda(idComanda);
        for (PedidoModel pedido : pedidos) {
            for (ProdutoPedidoModel produto : pedido.getProdutos()) {
                ProdutoModel prodModel = produtoDAO.read(produto.getIdProduto());
                if (prodModel != null) {
                    total = total.add(prodModel.getPreco());
                }

                for (AdicionalPedidoModel ad : produto.getAdicionais()) {
                    AdicionalModel adicional = adicionalDAO.read(ad.getIdAdicional());
                    if (adicional != null) {
                        total = total.add(adicional.getPreco().multiply(BigDecimal.valueOf(ad.getQuantidade())));
                    }
                }
            }
        }

        return total;
    }

    public void listarPagamentos() {
        try {
            List<PagamentoModel> lista = pagamentoDAO.findAll();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            if (lista.isEmpty()) {
                System.out.println("Nenhum pagamento registrado.");
                return;
            }

            System.out.println("\nPagamentos:");
            for (PagamentoModel p : lista) {
                System.out.printf("Pagamento #%s | Comanda: %s | R$ %.2f | Método: %s | Data: %s%n",
                        p.getId(), p.getIdComanda(), p.getValor(),
                        p.getMetodoPagamento().name(), sdf.format(p.getDataPagamento()));
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar pagamentos:");
            e.printStackTrace();
        }
    }
}
