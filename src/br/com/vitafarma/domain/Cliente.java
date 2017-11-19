package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Query;
import javax.persistence.Table;

import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "CLIENTES")
@PrimaryKeyJoinColumn(name = "ID_PESSOA_FISICA")
public class Cliente extends PessoaFisica implements Serializable, Comparable<Cliente> {
	private static final long serialVersionUID = -4740553752497832269L;
	private static final int MAX_RESULTS = 20;

	public Cliente() {
		this(null, null, null, null, null, null, null);
	}

	public Cliente(Long id, String nome, Long cpf, Long telefone, String endereco, String email, Cidade cidade) {
		super(id, nome, cpf, telefone, endereco, email, cidade);
	}

	private void copyProperties(Cliente cliente) {
		this.setNome(cliente.getNome());
		this.setTelefone(cliente.getTelefone());
		this.setEndereco(cliente.getEndereco());
		this.setEmail(cliente.getEmail());
		this.setCidade(cliente.getCidade());
		this.setCpf(cliente.getCpf());
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public void refresh() {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().refresh(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	public void detach() {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().detach(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	protected void persist() {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().persist(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	public void remove() {
		Cliente cliente = Cliente.find(this.getId());

		if (cliente != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(cliente);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Cliente merge() {
		Cliente cliente = Cliente.find(this.getId());

		if (cliente != null) {
			this.getEntityManager().getTransaction().begin();

			cliente = this.getEntityManager().merge(this);
			cliente.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return cliente;
	}

	@SuppressWarnings("unchecked")
	public static List<Cliente> findAll() {
		return (new Cliente()).getEntityManager().createQuery(" SELECT o FROM Cliente o ").getResultList();
	}

	public static Cliente find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		Cliente cliente = (new Cliente()).getEntityManager().find(Cliente.class, id);

		return cliente;
	}

	@SuppressWarnings("unchecked")
	public static List<Cliente> findBy(String nome, Long cpf, Long telefone, String endereco, String email) {
		MyEntity.log("nome", nome);
		MyEntity.log("cpf", cpf);
		MyEntity.log("telefone", telefone);
		MyEntity.log("endereco", endereco);
		MyEntity.log("email", email);

		boolean emptyFilter = true;

		if (!InputOutputUtils.isBlank(nome) || !InputOutputUtils.isBlank(cpf) || !InputOutputUtils.isBlank(telefone)
				|| !InputOutputUtils.isBlank(endereco) || !InputOutputUtils.isBlank(email)) {
			emptyFilter = false;
		}

		// Nome
		nome = ((nome == null) ? "" : nome);
		nome = ("%" + nome.replace('*', '%') + "%");

		String nomeQuery = "";
		if (!InputOutputUtils.isBlank(nome)) {
			nomeQuery = " LOWER ( o.nome ) LIKE LOWER ( :nome ) AND ";
		}

		// Cpf
		String cpfQuery = "";
		if (!InputOutputUtils.isBlank(cpf)) {
			cpfQuery = " o.cpf = :cpf AND ";
		}

		// Telefone
		String telefoneQuery = "";
		if (!InputOutputUtils.isBlank(telefone)) {
			telefoneQuery = " o.telefone  = :telefone AND ";
		}

		// Endereco
		endereco = ((endereco == null) ? "" : endereco);
		endereco = ("%" + endereco.replace('*', '%') + "%");

		String enderecoQuery = "";
		if (!InputOutputUtils.isBlank(endereco)) {
			enderecoQuery = " LOWER ( o.endereco ) LIKE LOWER ( :endereco ) AND ";
		}

		// Email
		email = ((email == null) ? "" : email);
		email = ("%" + email.replace('*', '%') + "%");

		String emailQuery = "";
		if (!InputOutputUtils.isBlank(email)) {
			emailQuery = " LOWER ( o.email ) LIKE LOWER ( :email ) AND ";
		}

		Query q = (new Cliente()).getEntityManager().createQuery(" SELECT o FROM Cliente o WHERE " + nomeQuery
				+ cpfQuery + telefoneQuery + enderecoQuery + emailQuery + " 1 = 1 ");

		if (!InputOutputUtils.isBlank(nome)) {
			q.setParameter("nome", nome);
		}

		if (!InputOutputUtils.isBlank(cpf)) {
			q.setParameter("cpf", cpf);
		}

		if (!InputOutputUtils.isBlank(telefone)) {
			q.setParameter("telefone", telefone);
		}

		if (!InputOutputUtils.isBlank(endereco)) {
			q.setParameter("endereco", endereco);
		}

		if (!InputOutputUtils.isBlank(email)) {
			q.setParameter("email", email);
		}

		if (emptyFilter) {
			q.setMaxResults(Cliente.MAX_RESULTS);
		}

		return q.getResultList();
	}

	public Double getValorTotalProdutos() {
		List<ItemVenda> produtosCliente = ItemVenda.findByCliente(this);

		Double result = 0.0;

		for (ItemVenda cp : produtosCliente) {
			result += cp.getPrecoFinal();
		}

		return result;
	}

	/*
	 * Verifica se o cpf informado é único.
	 * 
	 * TRUE ==> NÃO existe nenhum cliente cadastrado no sistema com o cpf
	 * informado
	 * 
	 * FALSE ==> existe pelo menos um cliente cadastrado no sistema com o cpf
	 * informado
	 */
	public static Boolean checkCodigoUnique(String value) {
		MyEntity.log("value", value);
		Long cpf = Long.parseLong(value);
		return (Cliente.findBy(null, cpf, null, null, null).size() == 0);
	}

	@Override
	public int compareTo(Cliente o) {
		if (o == null) {
			return -1;
		}

		return this.getNome().compareTo(o.getNome());
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
