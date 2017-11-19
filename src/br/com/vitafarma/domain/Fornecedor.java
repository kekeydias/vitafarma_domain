package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Query;
import javax.persistence.Table;

import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "FORNECEDORES")
@PrimaryKeyJoinColumn(name = "ID_PESSOA_JURIDICA")
public class Fornecedor extends PessoaJuridica implements Serializable, Comparable<Fornecedor> {
	private static final long serialVersionUID = 8999018906526829704L;
	private static final int MAX_RESULTS = 20;

	public Fornecedor() {
		this(null, null, null, null, null, null, null, null);
	}

	public Fornecedor(Long id, String nome, Long telefone, String endereco, String email, String nomeFantasia,
			Long cnpj, Cidade cidade) {
		super(id, nome, nomeFantasia, cnpj, telefone, endereco, email, cidade);
	}

	private void copyProperties(Fornecedor fornecedor) {
		this.setNome(fornecedor.getNome());
		this.setTelefone(fornecedor.getTelefone());
		this.setEndereco(fornecedor.getEndereco());
		this.setEmail(fornecedor.getEmail());
		this.setCidade(fornecedor.getCidade());
		this.setNomeFantasia(fornecedor.getNomeFantasia());
		this.setCnpj(fornecedor.getCnpj());
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
		Fornecedor fornecedor = Fornecedor.find(this.getId());

		if (fornecedor != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(fornecedor);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Fornecedor merge() {
		Fornecedor fornecedor = Fornecedor.find(this.getId());

		if (fornecedor != null) {
			this.getEntityManager().getTransaction().begin();

			fornecedor = this.getEntityManager().merge(this);
			fornecedor.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return fornecedor;
	}

	@SuppressWarnings("unchecked")
	public static List<Fornecedor> findAll() {
		return (new Fornecedor()).getEntityManager().createQuery(" SELECT o FROM Fornecedor o ").getResultList();
	}

	public static Fornecedor find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		Fornecedor fornecedor = (new Fornecedor()).getEntityManager().find(Fornecedor.class, id);

		return fornecedor;
	}

	@SuppressWarnings("unchecked")
	public static List<Fornecedor> findBy(String nome, String nomeFantasia, Long cnpj, Long telefone, String endereco,
			String email) {
		MyEntity.log("nome", nome);
		MyEntity.log("nomeFantasia", nomeFantasia);
		MyEntity.log("cnpj", cnpj);
		MyEntity.log("telefone", telefone);
		MyEntity.log("endereco", endereco);
		MyEntity.log("email", email);

		boolean emptyFilter = true;
		if (!InputOutputUtils.isBlank(nome) || !InputOutputUtils.isBlank(nomeFantasia)
				|| !InputOutputUtils.isBlank(cnpj) || !InputOutputUtils.isBlank(telefone)
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

		// Nome Fantasia
		nomeFantasia = ((nomeFantasia == null) ? "" : nomeFantasia);
		nomeFantasia = ("%" + nomeFantasia.replace('*', '%') + "%");

		String nomeFantasiaQuery = "";
		if (!InputOutputUtils.isBlank(nomeFantasia)) {
			nomeFantasiaQuery = " LOWER ( o.nomeFantasia ) LIKE LOWER ( :nomeFantasia ) AND ";
		}

		// Cnpj
		String cnpjQuery = "";
		if (!InputOutputUtils.isBlank(cnpj)) {
			cnpjQuery = " o.cnpj = :cnpj AND ";
		}

		// Telefone
		String telefoneQuery = "";
		if (!InputOutputUtils.isBlank(telefone)) {
			telefoneQuery = " o.telefone = :telefone AND ";
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

		Query q = (new Fornecedor()).getEntityManager().createQuery(" SELECT o FROM Fornecedor o WHERE " + nomeQuery
				+ nomeFantasiaQuery + cnpjQuery + telefoneQuery + enderecoQuery + emailQuery + " 1 = 1 ");

		if (!InputOutputUtils.isBlank(nome)) {
			q.setParameter("nome", nome);
		}

		if (!InputOutputUtils.isBlank(nomeFantasia)) {
			q.setParameter("nomeFantasia", nomeFantasia);
		}

		if (!InputOutputUtils.isBlank(cnpj)) {
			q.setParameter("cnpj", cnpj);
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
			q.setMaxResults(Fornecedor.MAX_RESULTS);
		}

		return q.getResultList();
	}

	/*
	 * Verifica se o cpf informado é único.
	 * 
	 * TRUE ==> NÃO existe nenhum fornecedor cadastrado no sistema com o cnpj
	 * informado
	 * 
	 * FALSE ==> existe pelo menos um fornecedor cadastrado no sistema com o
	 * cnpj informado
	 */
	public static Boolean checkCodigoUnique(String value) {
		MyEntity.log("value", value);
		Long cnpj = Long.parseLong(value);
		return (Fornecedor.findBy(null, null, cnpj, null, null, null).size() == 0);
	}

	@Override
	public int compareTo(Fornecedor o) {
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
