package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Query;
import javax.persistence.Table;

import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "VENDEDORES")
@PrimaryKeyJoinColumn(name = "ID_FUNCIONARIO")
public class Vendedor extends Funcionario implements Serializable {
	private static final long serialVersionUID = -4740553752497832269L;
	private static final int MAX_RESULTS = 20;

	public Vendedor() {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	public Vendedor(Long id, String nome, Long cpf, Long telefone, String endereco, String email, Date dataAdimissao,
			Date dataDemissao, Double salario, Cidade cidade) {
		super(id, nome, cpf, telefone, endereco, email, dataAdimissao, dataDemissao, salario, cidade);
	}

	// Usa copyProperties da superclasse Funcionario
	// private void copyProperties(Vendedor vendedor) {
	// super.copyProperties(vendedor);
	// }

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public void refresh() {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().refresh(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	@Override
	public void detach() {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().detach(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	@Override
	protected void persist() {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().persist(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	@Override
	public void remove() {
		Vendedor vendedor = Vendedor.findVendedor(this.getId());

		if (vendedor != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(vendedor);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	@Override
	protected Vendedor merge() {
		Vendedor vendedor = Vendedor.findVendedor(this.getId());

		if (vendedor != null) {
			this.getEntityManager().getTransaction().begin();

			vendedor = this.getEntityManager().merge(this);
			vendedor.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return vendedor;
	}

	@SuppressWarnings("unchecked")
	public static List<Vendedor> findAllVendedores() {
		Query q = (new Vendedor()).getEntityManager().createQuery(" SELECT o FROM Vendedor o ");

		return q.getResultList();
	}

	public static Vendedor findVendedor(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		return (new Vendedor()).getEntityManager().find(Vendedor.class, id);
	}

	@SuppressWarnings("unchecked")
	public static List<Vendedor> findVendedoresBy(String nome, Long cpf, Long telefone, String endereco, String email) {
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

		Query q = (new Vendedor()).getEntityManager().createQuery(" SELECT o FROM Vendedor o WHERE " + nomeQuery
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
			q.setMaxResults(Vendedor.MAX_RESULTS);
		}

		return q.getResultList();
	}

	/*
	 * Verifica se o cpf informado é único.
	 * 
	 * TRUE ==> NÃO existe nenhum vendedor cadastrado no sistema com o cpf
	 * informado
	 * 
	 * FALSE ==> existe pelo menos um vendedor cadastrado no sistema com o cpf
	 * informado
	 */
	public static Boolean checkCodigoUnique(String value) {
		MyEntity.log("value", value);

		Long cpf = Long.parseLong(value);

		return (Vendedor.findVendedoresBy(null, cpf, null, null, null).isEmpty());
	}
}
