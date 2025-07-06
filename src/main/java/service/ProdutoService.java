package service;

import dao.CategoriaDAO;
import dao.ProdutoDAO;
import model.CategoriaModel;
import model.ProdutoModel;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ProdutoService {

    private final ProdutoDAO produtoDAO;
    private final CategoriaDAO categoriaDAO;

    public ProdutoService(com.mongodb.client.MongoDatabase database) {
        this.produtoDAO = new ProdutoDAO(database);
        this.categoriaDAO = new CategoriaDAO(database);
    }

    public void cadastrarProduto() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Nome do produto: ");
            String nome = scanner.nextLine();

            System.out.print("Descrição: ");
            String descricao = scanner.nextLine();

            System.out.print("Preço (ex: 42.90): ");
            BigDecimal preco = new BigDecimal(scanner.nextLine());

            CategoriaModel categoria = selecionarCategoria();
            if (categoria == null) return;

            ProdutoModel produto = new ProdutoModel();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setIdCategoria(categoria.getId()); // Mongo usa _id como String

            produtoDAO.create(produto);
            System.out.println("Produto cadastrado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar produto:");
            e.printStackTrace();
        }
    }

    public void listarProdutos() {
        try {
            List<ProdutoModel> lista = produtoDAO.findAll();
            System.out.println("\nProdutos:");
            for (ProdutoModel p : lista) {
                CategoriaModel cat = categoriaDAO.read(p.getIdCategoria());
                String nomeCategoria = (cat != null) ? cat.getNome() : "Sem categoria";
                System.out.printf("ID: %s | %s | R$ %.2f | Categoria: %s%n",
                        p.getId(), p.getNome(), p.getPreco(), nomeCategoria);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar produtos:");
            e.printStackTrace();
        }
    }

    public void editarProduto() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<ProdutoModel> lista = produtoDAO.findAll();
            if (lista.isEmpty()) {
                System.out.println("Nenhum produto encontrado.");
                return;
            }

            System.out.println("\nSelecione um produto para editar:");
            for (int i = 0; i < lista.size(); i++) {
                System.out.printf("%d - %s%n", i + 1, lista.get(i).getNome());
            }

            System.out.print("Número do produto: ");
            int escolha = Integer.parseInt(scanner.nextLine());
            if (escolha < 1 || escolha > lista.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            ProdutoModel produto = lista.get(escolha - 1);

            System.out.print("Novo nome (" + produto.getNome() + "): ");
            produto.setNome(scanner.nextLine());

            System.out.print("Nova descrição: ");
            produto.setDescricao(scanner.nextLine());

            System.out.print("Novo preço: ");
            produto.setPreco(new BigDecimal(scanner.nextLine()));

            CategoriaModel novaCat = selecionarCategoria();
            if (novaCat != null) {
                produto.setIdCategoria(novaCat.getId());
            }

            produtoDAO.update(produto);
            System.out.println("Produto atualizado!");

        } catch (Exception e) {
            System.out.println("Erro ao editar produto:");
            e.printStackTrace();
        }
    }

    public void excluirProduto() {
        Scanner scanner = new Scanner(System.in);
        try {
            List<ProdutoModel> lista = produtoDAO.findAll();
            if (lista.isEmpty()) {
                System.out.println("Nenhum produto para excluir.");
                return;
            }

            System.out.println("\nSelecione o produto a excluir:");
            for (int i = 0; i < lista.size(); i++) {
                System.out.printf("%d - %s%n", i + 1, lista.get(i).getNome());
            }

            System.out.print("Número do produto: ");
            int escolha = Integer.parseInt(scanner.nextLine());

            if (escolha < 1 || escolha > lista.size()) {
                System.out.println("Opção inválida.");
                return;
            }

            produtoDAO.delete(lista.get(escolha - 1).getId());
            System.out.println("Produto removido!");

        } catch (Exception e) {
            System.out.println("Erro ao excluir produto:");
            e.printStackTrace();
        }
    }

    private CategoriaModel selecionarCategoria() throws Exception {
        List<CategoriaModel> categorias = categoriaDAO.findAll();
        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
            return null;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nSelecione uma categoria:");
        for (int i = 0; i < categorias.size(); i++) {
            System.out.printf("%d - %s%n", i + 1, categorias.get(i).getNome());
        }

        System.out.print("Número da categoria: ");
        int escolha = Integer.parseInt(scanner.nextLine());

        if (escolha < 1 || escolha > categorias.size()) {
            System.out.println("Categoria inválida.");
            return null;
        }

        return categorias.get(escolha - 1);
    }
}
