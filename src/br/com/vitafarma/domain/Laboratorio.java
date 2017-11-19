package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "LABORATORIOS", catalog = "", schema = "")
@PrimaryKeyJoinColumn(name = "ID_PESSOA_JURIDICA")
public class Laboratorio extends PessoaJuridica implements Serializable, Comparable<Laboratorio> {
	private static final long serialVersionUID = 4544686926489096340L;
	private static final int MAX_RESULTS = 20;

	public Laboratorio() {
		this(null, null, null, null, null, null, null, null, null);
	}

	public Laboratorio(Long id, Long medLab, String nome, String nomeFantasia, Long cnpj, Long telefone,
			String endereco, String email, Cidade cidade) {
		super(id, nome, nomeFantasia, cnpj, telefone, endereco, email, cidade);

		this.setMedLab(medLab);
	}

	private void copyProperties(Laboratorio laboratorio) {
		this.setNome(laboratorio.getNome());
		this.setTelefone(laboratorio.getTelefone());
		this.setEndereco(laboratorio.getEndereco());
		this.setEmail(laboratorio.getEmail());
		this.setCidade(laboratorio.getCidade());
		this.setNomeFantasia(laboratorio.getNomeFantasia());
		this.setCnpj(laboratorio.getCnpj());
		this.setMedLab(laboratorio.getMedLab());
	}

	@NotNull
	@Column(name = "MED_LAB", nullable = false, unique = true)
	private Long medLab = null;

	public Long getMedLab() {
		return this.medLab;
	}

	public void setMedLab(Long medLab) {
		this.medLab = medLab;
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
		Laboratorio laboratorio = Laboratorio.find(this.getId());

		if (laboratorio != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(laboratorio);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Laboratorio merge() {
		Laboratorio laboratorio = Laboratorio.find(this.getId());

		if (laboratorio != null) {
			this.getEntityManager().getTransaction().begin();

			laboratorio = this.getEntityManager().merge(this);
			laboratorio.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return laboratorio;
	}

	@SuppressWarnings("unchecked")
	public static List<Laboratorio> findAll() {
		return (new Laboratorio()).getEntityManager().createQuery(" SELECT o FROM Laboratorio o ").getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<Laboratorio> findMaxResultsLimited(int max_results) {
		MyEntity.log("max_results", max_results);

		return (new Laboratorio()).getEntityManager().createQuery(" SELECT o FROM Laboratorio o ")
				.setMaxResults(max_results).getResultList();
	}

	public static Laboratorio find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		Laboratorio laboratorio = (new Laboratorio()).getEntityManager().find(Laboratorio.class, id);

		return laboratorio;
	}

	@SuppressWarnings("unchecked")
	public static List<Laboratorio> findBy(String nome, String nomeFantasia, Long cnpj, Long telefone, String endereco,
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

		String emailQuery = ("");

		if (!InputOutputUtils.isBlank(email)) {
			emailQuery = " LOWER ( o.email ) LIKE LOWER ( :email ) AND ";
		}

		Query q = ((new Fornecedor()).getEntityManager().createQuery(" SELECT o FROM Laboratorio o WHERE " + nomeQuery
				+ nomeFantasiaQuery + cnpjQuery + telefoneQuery + enderecoQuery + emailQuery + " 1 = 1 "));

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
			q.setMaxResults(Laboratorio.MAX_RESULTS);
		}

		return q.getResultList();
	}

	public static Laboratorio findByMedLab(Long medLab) {
		MyEntity.log("medLab", medLab);

		// MedLab
		String medLabQuery = ("");

		if (!InputOutputUtils.isBlank(medLab)) {
			medLabQuery = " o.medLab = :medLab AND ";
		}

		Query q = (new Fornecedor()).getEntityManager()
				.createQuery(" SELECT o FROM Laboratorio o WHERE " + medLabQuery + " 1 = 1 ");

		if (!InputOutputUtils.isBlank(medLab)) {
			q.setParameter("medLab", medLab);
		}

		Laboratorio laboratorio = (Laboratorio) q.getSingleResult();

		return laboratorio;
	}

	public static Map<Long, Laboratorio> buildNomeToLaboratorioMap(List<Laboratorio> laboratorios) {
		Map<Long, Laboratorio> laboratoriosMap = new HashMap<Long, Laboratorio>();

		for (Laboratorio laboratorio : laboratorios) {
			laboratoriosMap.put(laboratorio.getMedLab(), laboratorio);
		}

		return laboratoriosMap;
	}

	public static Boolean checkMedLabUnique(String value) {
		MyEntity.log("value", value);

		Long medLab = Long.parseLong(value);

		return (Laboratorio.findByMedLab(medLab) == null);
	}

	public static Boolean checkCnpjUnique(String value) {
		MyEntity.log("value", value);

		Long cnpj = Long.parseLong(value);

		return (Laboratorio.findBy(null, null, cnpj, null, null, null).size() == 0);
	}

	@Override
	public int compareTo(Laboratorio o) {
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
