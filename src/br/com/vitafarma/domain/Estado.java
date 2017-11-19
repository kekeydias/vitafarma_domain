package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "ESTADOS", catalog = "", schema = "")
@SequenceGenerator(name = "INC_ESTADO_ID", sequenceName = "ESTADO_ID_GEN")
public class Estado extends MyEntity implements Serializable, Comparable<Estado> {
	private static final long serialVersionUID = -8789701511236707194L;
	private static final int MAX_RESULTS = 20;

	public Estado() {
		this(null, null, null);
	}

	public Estado(Long id, String nome, String sigla) {
		this.setId(id);
		this.setNome(nome);
		this.setSigla(sigla == null ? null : sigla.toUpperCase());
	}

	private void copyProperties(Estado estado) {
		this.setNome(estado.getNome());
		this.setSigla(estado.getSigla());
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Override
	public String toString() {
		return ("\nId: " + this.getId() + "\nNome: " + this.getNome() + "\nSigla: " + this.getSigla());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_ESTADO_ID")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "NOME", nullable = false)
	private String nome;

	@Column(name = "SIGLA", nullable = false)
	private String sigla;

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
		Estado estado = Estado.find(this.getId());

		if (estado != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(estado);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Estado merge() {
		Estado estado = Estado.find(this.getId());

		if (estado != null) {
			this.getEntityManager().getTransaction().begin();

			estado = this.getEntityManager().merge(this);
			estado.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return estado;
	}

	@SuppressWarnings("unchecked")
	public static List<Estado> findAll() {
		return (new Estado()).getEntityManager().createQuery(" SELECT o FROM Estado o ").getResultList();
	}

	public static Estado find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		Estado estado = (new Estado()).getEntityManager().find(Estado.class, id);

		return estado;
	}

	@SuppressWarnings("unchecked")
	public static List<Estado> findBy(String nome, String sigla) {
		MyEntity.log("nome", nome);
		MyEntity.log("sigla", sigla);

		boolean emptyFilter = true;

		if (!InputOutputUtils.isBlank(nome) || !InputOutputUtils.isBlank(sigla)) {
			emptyFilter = false;
		}

		// Nome do Estado
		nome = ((nome == null) ? "" : nome);
		nome = ("%" + nome.replace('*', '%') + "%");

		String nomeQuery = ("");

		if (!InputOutputUtils.isBlank(nome)) {
			nomeQuery = " LOWER ( o.nome ) LIKE LOWER ( :nome ) AND ";
		}

		// Sigla do Estado
		sigla = ((sigla == null) ? "" : sigla);
		sigla = ("%" + sigla.replace('*', '%') + "%");

		String siglaQuery = ("");

		if (!InputOutputUtils.isBlank(sigla)) {
			siglaQuery = " LOWER ( o.sigla ) LIKE LOWER ( :sigla ) AND ";
		}

		Query q = (new Estado()).getEntityManager()
				.createQuery(" SELECT o FROM Estado o WHERE " + nomeQuery + siglaQuery + " 1 = 1 ");

		if (!InputOutputUtils.isBlank(nome)) {
			q.setParameter("nome", nome);
		}

		if (!InputOutputUtils.isBlank(sigla)) {
			q.setParameter("sigla", sigla);
		}

		if (emptyFilter) {
			q.setMaxResults(Estado.MAX_RESULTS);
		}

		return q.getResultList();
	}

	public static Boolean checkNomeUnique(String value) {
		MyEntity.log("value", value);

		return (Estado.findBy(value, null).size() == 0);
	}

	public static Boolean checkSiglaUnique(String value) {
		MyEntity.log("value", value);

		return (Estado.findBy(null, value).size() == 0);
	}

	@Override
	public int compareTo(Estado o) {
		if (o == null) {
			return -1;
		}

		return this.getSigla().compareTo(o.getSigla());
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
