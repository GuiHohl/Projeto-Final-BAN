package service;

import dao.FuncionarioDAO;
import dao.PagamentoDAO;
import dao.ProdutoDAO;
import dto.RelatorioFaturamentoDiarioDTO;
import dto.RelatorioFuncionariosComMaisComandasDTO;
import dto.RelatorioProdutosMaisVendidosDTO;
import dto.RelatorioVendasPorMetodoPagamentoDTO;
import model.PagamentoModel;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RelatorioService {

    private final PagamentoDAO pagamentoDAO;
    private final ProdutoDAO produtoDAO;
    private final FuncionarioDAO funcionarioDAO;

    public RelatorioService(MongoDatabase database) {
        this.pagamentoDAO = new PagamentoDAO(database);
        this.produtoDAO = new ProdutoDAO(database);
        this.funcionarioDAO = new FuncionarioDAO(database);
    }

    public void relatorioFaturamentoDiario() {
        try {
            List<RelatorioFaturamentoDiarioDTO> relatorio = pagamentoDAO.findFaturamentoDiario();

            System.out.println("\nRelatório de Faturamento Diário:");
            System.out.printf("%-15s %-15s %-15s%n", "Data", "Total Comandas", "Faturamento Total");

            for (RelatorioFaturamentoDiarioDTO dto : relatorio) {
                System.out.println(dto);
            }
        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório de faturamento diário:");
            e.printStackTrace();
        }
    }

    public void relatorioProdutosMaisVendidos() {
        try {
            List<RelatorioProdutosMaisVendidosDTO> relatorio = produtoDAO.findProdutosMaisVendidos();

            System.out.println("\nRelatório de Produtos Mais Vendidos:");
            System.out.printf("%-30s %-15s %-15s%n", "Produto", "Vendas", "Faturamento");

            for (RelatorioProdutosMaisVendidosDTO dto : relatorio) {
                System.out.println(dto);
            }
        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório de produtos mais vendidos:");
            e.printStackTrace();
        }
    }

    public void relatorioVendasPorMetodoPagamento() {
        try {
            List<RelatorioVendasPorMetodoPagamentoDTO> relatorio = pagamentoDAO.findVendasPorMetodo();

            System.out.println("\nRelatório de Vendas por Método de Pagamento:");
            System.out.printf("%-20s %-15s %-15s%n", "Método", "Pagamentos", "Total Faturamento");

            for (RelatorioVendasPorMetodoPagamentoDTO dto : relatorio) {
                System.out.printf("%-20s %-15d R$ %-13.2f%n",
                        dto.getMetodoPagamento(),
                        dto.getTotalPagamentos(),
                        dto.getTotalVendas());
            }
        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório de vendas por método de pagamento:");
            e.printStackTrace();
        }
    }


    public void relatorioFuncionariosComMaisComandas() {
        try {
            List<RelatorioFuncionariosComMaisComandasDTO> relatorio = funcionarioDAO.findFuncionariosComMaisComandas();

            System.out.println("\nRelatório de Funcionários com Mais Comandas Atendidas:");
            System.out.printf("%-30s %-15s %-15s%n", "Funcionário", "Cargo", "Comandas Atendidas");

            for (RelatorioFuncionariosComMaisComandasDTO dto : relatorio) {
                System.out.println(dto);
            }
        } catch (Exception e) {
            System.out.println("Erro ao gerar relatório de funcionários com mais comandas atendidas:");
            e.printStackTrace();
        }
    }
}
