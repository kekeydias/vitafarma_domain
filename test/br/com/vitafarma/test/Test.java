package br.com.vitafarma.test;

import java.util.List;

import br.com.vitafarma.domain.Cenario;
import br.com.vitafarma.domain.Cidade;
import br.com.vitafarma.domain.Cliente;
import br.com.vitafarma.domain.Estado;
import br.com.vitafarma.domain.Fornecedor;
import br.com.vitafarma.domain.Funcionario;
import br.com.vitafarma.domain.MyEntity;
import br.com.vitafarma.domain.Produto;
import br.com.vitafarma.domain.Usuario;
import br.com.vitafarma.util.FormatUtils;
import junit.framework.TestCase;
import junit.textui.TestRunner;

public class Test extends TestCase {
	private Cidade cidade = null;
	private Estado estado = null;

	public static void main(String[] args) {
		MyEntity.log("\nIniciando a aplicacao.\n");

		TestRunner.run(Test.class);

		MyEntity.log("\nTerminando a aplicacao.\n");
	}

	public void setUp() throws Exception {
		// Carregando cenario
		Cenario cenario = Cenario.findAll().get(0);
		MyEntity.log("\nCenario:\n" + cenario);

		// Carregando usuario
		Usuario usuario = Usuario.findAll().get(0);
		MyEntity.log("\nUsuario:\n" + usuario);

		this.loadCidadeEstado();
	}

	public void loadCidadeEstado() throws Exception {
		// Carregando uma estado qualquer
		List<Estado> estados = Estado.findAll();

		if (estados.size() == 0) {
			this.estado = new Estado();

			this.estado.setId(null);
			this.estado.setNome("Rio de Janeiro");
			this.estado.setSigla("RJ");

			this.estado.save();

			MyEntity.log("\nEstado:\n" + this.estado);
		} else {
			this.estado = estados.get(0);
		}

		// Carregando uma cidade qualquer do estado selecionado acima
		List<Cidade> cidadesEstado = Cidade.findByEstado(this.estado);

		if (cidadesEstado.size() == 0) {
			this.cidade = new Cidade();

			this.cidade.setId(null);
			this.cidade.setNome("Andra dos Reis");
			this.cidade.setEstado(this.estado);

			this.cidade.save();

			MyEntity.log("\nCidade:\n" + this.cidade);
		} else {
			List<Cidade> cidades = Cidade.findAll();

			this.cidade = (cidades.size() == 0 ? null : cidades.get(0));
		}
	}

	public void testProdutos() throws Exception {
		// Carregando produto
		Produto produto = null;
		List<Produto> produtos = Produto.findAll();

		MyEntity.log("\nTotal de produtos: " + produtos.size());

		if (produtos.size() == 0) {
			produto = new Produto();

			produto.setId(null);
			produto.setNome("CEFALEXINA");
			produto.setDescricao("CEFALEXINA 20 MG");
			produto.setPreco(1000.00);
			produto.setMedAbc(300L);

			produto.save();
		} else {
			produto = produtos.get(0);
		}

		MyEntity.log("\nProduto:\n" + produto);

	}

	public void testClientes() throws Exception {
		// Carregando cliente
		Cliente cliente = null;
		List<Cliente> clientes = Cliente.findAll();

		if (clientes.size() == 0) {
			cliente = new Cliente();

			cliente.setId(null);
			cliente.setNome("Cleiton");
			cliente.setCpf(36985214567L);
			cliente.setCidade(this.cidade);

			cliente.save();
		} else {
			cliente = clientes.get(0);
		}

		MyEntity.log("\nCliente:\n" + cliente);

	}

	public void testFornecedores() throws Exception {
		// Carregando fornecedor
		Fornecedor fornecedor = null;

		List<Fornecedor> fornecedores = Fornecedor.findAll();
		MyEntity.log("\nTotal de fornecedores: " + fornecedores.size());

		if (fornecedores.size() == 0) {
			fornecedor = new Fornecedor();

			fornecedor.setId(null);
			fornecedor.setNome("Laboratórios Teuto S.A.");
			fornecedor.setNomeFantasia("Teuto");
			fornecedor.setCnpj(12345678912345L);
			fornecedor.setCidade(this.cidade);

			fornecedor.save();
		} else {
			fornecedor = fornecedores.get(0);

			for (Fornecedor f : fornecedores) {
				MyEntity.log(f);
			}
		}

		MyEntity.log("\nFornecedor:\n" + fornecedor);
	}

	public void testFuncionarios() throws Exception {
		// Carregando funcionario
		Funcionario funcionario = null;
		List<Funcionario> funcionarios = Funcionario.findAll();

		if (funcionarios.size() == 0) {
			funcionario = new Funcionario();

			funcionario.setId(null);
			funcionario.setNome("Cleiton");
			funcionario.setCpf(12365478952L);
			funcionario.setSalario(1000.00);
			funcionario.setDataAdimissao(FormatUtils.getDate(12, 10, 2015));
			funcionario.setDataDemissao(null);
			funcionario.setCidade(this.cidade);

			funcionario.save();
		} else {
			funcionario = funcionarios.get(0);
		}

		MyEntity.log("\nFuncioanario:\n" + funcionario);
	}

	public void testCarregarEntidadeComIdInvalido() throws Exception {
		Long id = -1L;

		Estado estadoError = Estado.find(id);

		MyEntity.log("estadoError: " + estadoError);
	}
}
