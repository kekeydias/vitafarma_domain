package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.vitafarma.util.FormatUtils;
import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "FUNCIONARIOS")
@PrimaryKeyJoinColumn(name = "ID_PESSOA_FISICA")
public class Funcionario extends PessoaFisica implements Serializable, Comparable<Funcionario> {
	private static final long serialVersionUID = 8999018906526829704L;
	private static final int MAX_RESULTS = 20;

	public Funcionario() {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	public Funcionario(Long id, String nome, Long cpf, Long telefone, String endereco, String email, Date dataAdimissao,
			Date dataDemissao, Double salario, Cidade cidade) {
		super(id, nome, cpf, telefone, endereco, email, cidade);
		this.setDataAdimissao(dataAdimissao);
		this.setDataDemissao(dataDemissao);
		this.setSalario(salario);
	}

	protected void copyProperties(Funcionario funcionario) {
		this.setNome(funcionario.getNome());
		this.setTelefone(funcionario.getTelefone());
		this.setEndereco(funcionario.getEndereco());
		this.setEmail(funcionario.getEmail());
		this.setCidade(funcionario.getCidade());
		this.setCpf(funcionario.getCpf());
		this.setDataAdimissao(funcionario.getDataAdimissao());
		this.setDataDemissao(funcionario.getDataDemissao());
		this.setSalario(funcionario.getSalario());
	}

	public Date getDataAdimissao() {
		return this.dataAdimissao;
	}

	public void setDataAdimissao(Date dataAdimissao) {
		this.dataAdimissao = dataAdimissao;
	}

	public Date getDataDemissao() {
		return this.dataDemissao;
	}

	public void setDataDemissao(Date dataDemissao) {
		this.dataDemissao = dataDemissao;
	}

	public Double getSalario() {
		return this.salario;
	}

	public void setSalario(Double salario) {
		this.salario = salario;
	}

	@Override
	public String toString() {
		String demissao = "";

		if (this.getDataDemissao() != null) {
			demissao = "\nData de Demissao: " + FormatUtils.getDateString(this.getDataDemissao());
		}

		return super.toString() + "\nSalario: " + String.format("%.2f", this.getSalario()) + "\nData de Adimissao: "
				+ FormatUtils.getDateString(this.getDataAdimissao()) + demissao;
	}

	@Column(name = "DATA_ADIMISSAO", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataAdimissao;

	@Column(name = "DATA_DEMISSAO")
	@Temporal(TemporalType.DATE)
	private Date dataDemissao;

	@Column(name = "SALARIO", nullable = false)
	private Double salario;

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
		Funcionario funcionario = Funcionario.find(this.getId());

		if (funcionario != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(funcionario);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Funcionario merge() {
		Funcionario funcionario = Funcionario.find(this.getId());

		if (funcionario != null) {
			this.getEntityManager().getTransaction().begin();

			funcionario = this.getEntityManager().merge(this);
			funcionario.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return funcionario;
	}

	@SuppressWarnings("unchecked")
	public static List<Funcionario> findAll() {
		return (new Funcionario()).getEntityManager().createQuery(" SELECT o FROM Funcionario o ").getResultList();
	}

	public static Funcionario find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		Funcionario funcionario = (new Funcionario()).getEntityManager().find(Funcionario.class, id);

		return funcionario;
	}

	@SuppressWarnings("unchecked")
	public static List<Funcionario> findBy(String nome, Long cpf, Long telefone, String endereco, String email) {
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
			telefoneQuery = " o.telefone = :telefone AND ";
		}

		// Nome
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

		Query q = (new Funcionario()).getEntityManager().createQuery(" SELECT o FROM Funcionario o WHERE " + nomeQuery
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

		// Verifica se deve-se limitar o tamanho da busca
		if (emptyFilter) {
			q.setMaxResults(Funcionario.MAX_RESULTS);
		}

		return q.getResultList();
	}

	/*
	 * Verifica se o cpf informado é único.
	 * 
	 * TRUE ==> NÃO existe nenhum funcionario cadastrado no sistema com o cpf
	 * informado
	 * 
	 * FALSE ==> existe pelo menos um funcionario cadastrado no sistema com o
	 * cpf informado
	 */
	public static Boolean checkCodigoUnique(String value) {
		MyEntity.log("value", value);
		Long cpf = Long.parseLong(value);
		return (Funcionario.findBy(null, cpf, null, null, null).size() == 0);
	}

	@Override
	public int compareTo(Funcionario o) {
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
