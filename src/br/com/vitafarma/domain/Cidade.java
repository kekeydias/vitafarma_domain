package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "CIDADES", catalog = "", schema = "")
@SequenceGenerator(name = "INC_CIDADE_ID", sequenceName = "CIDADE_ID_GEN")
public class Cidade extends MyEntity implements Serializable, Comparable<Cidade> {
	private static final long serialVersionUID = -8789701511236707194L;
	private static final int MAX_RESULTS = 20;

	public Cidade() {
		this(null, null, null);
	}

	public Cidade(Long id, String nome, Estado estado) {
		this.setId(id);
		this.setNome(nome);
		this.setEstado(estado);
	}

	private void copyProperties(Cidade cidade) {
		this.setNome(cidade.getNome());
		this.setEstado(cidade.getEstado());
	}

	@Override
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

	public Estado getEstado() {
		return this.estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return ("\nId: " + this.getId() + "\nNome: " + this.getNome() + "\nEstado: " + this.getEstado());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_CIDADE_ID")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "NOME", nullable = true)
	private String nome = null;

	@NotNull
	@ManyToOne(targetEntity = Estado.class, optional = false)
	@JoinColumn(name = "CIDADE_ESTADO_ID", nullable = false)
	private Estado estado = null;

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
		Cidade cidade = Cidade.find(this.getId());

		if (cidade != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(cidade);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Cidade merge() {
		Cidade cidade = Cidade.find(this.getId());

		if (cidade != null) {
			this.getEntityManager().getTransaction().begin();

			cidade = this.getEntityManager().merge(this);
			cidade.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return cidade;
	}

	@SuppressWarnings("unchecked")
	public static List<Cidade> findAll() {
		return (new Cidade()).getEntityManager().createQuery(" SELECT o FROM Cidade o ").getResultList();
	}

	public static int count() {
		Query q = (new Cidade()).getEntityManager().createQuery(" SELECT count(o) FROM Cidade o ");

		return ((Number) q.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public static List<Cidade> findByEstado(Estado estado) {
		Cidade.log("estado", estado);

		if (estado == null) {
			return Collections.emptyList();
		}

		Query q = (new Cidade()).getEntityManager().createQuery( //
				" SELECT o FROM Cidade o " + //
						" WHERE o.estado = :estado "); //

		q.setParameter("estado", estado);

		return q.getResultList();
	}

	public static Cidade find(Long id) {
		Cidade.log("id", id);

		if (id == null) {
			return null;
		}

		Cidade cidade = (new Cidade()).getEntityManager().find(Cidade.class, id);

		return cidade;
	}

	@SuppressWarnings("unchecked")
	public static List<Cidade> findBy(String nomeCidade, String nomeEstado) {
		Cidade.log("nomeCidade", nomeCidade);
		Cidade.log("nomeEstado", nomeEstado);

		boolean emptyFilter = true;
		if (!InputOutputUtils.isBlank(nomeCidade) || !InputOutputUtils.isBlank(nomeEstado)) {
			emptyFilter = false;
		}

		// Cidade
		nomeCidade = ((nomeCidade == null) ? "" : nomeCidade);
		nomeCidade = ("%" + nomeCidade.replace('*', '%') + "%");

		String nomeCidadeQuery = "";
		if (!InputOutputUtils.isBlank(nomeCidade)) {
			nomeCidadeQuery = " LOWER ( o.nome ) LIKE LOWER ( :nomeCidade ) AND ";
		}

		// Estado
		nomeEstado = ((nomeEstado == null) ? "" : nomeEstado);
		nomeEstado = ("%" + nomeEstado.replace('*', '%') + "%");

		String nomeEstadoQuery = "";
		if (!InputOutputUtils.isBlank(nomeEstado)) {
			nomeEstadoQuery = " LOWER ( o.estado.nome ) LIKE LOWER ( :nomeEstado ) AND ";
		}

		Query q = (new Cidade()).getEntityManager()
				.createQuery(" SELECT o FROM Cidade o WHERE " + nomeCidadeQuery + nomeEstadoQuery + " 1 = 1 ");

		if (!InputOutputUtils.isBlank(nomeCidade)) {
			q.setParameter("nomeCidade", nomeCidade);
		}

		if (!InputOutputUtils.isBlank(nomeEstado)) {
			q.setParameter("nomeEstado", nomeEstado);
		}

		// Verifica se deve-se limitar o tamanho da busca
		if (emptyFilter) {
			q.setMaxResults(Cidade.MAX_RESULTS);
		}

		return q.getResultList();
	}

	@Override
	public int hashCode() {
		int result = 19457;

		if (this.getEstado() != null) {
			result *= (this.getEstado().hashCode());
		}

		if (this.getNome() != null) {
			result *= (this.getNome().hashCode());
		}

		if (this.getId() != null) {
			result *= this.getId().hashCode();
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Cidade)) {
			return false;
		}

		Cidade other = (Cidade) obj;

		if (this.getEstado() == null) {
			if (other.getEstado() != null) {
				return false;
			}
		} else if (!this.getEstado().equals(other.getEstado())) {
			return false;
		}

		if (this.getNome() == null) {
			if (other.getNome() != null) {
				return false;
			}
		} else if (!this.getNome().equals(other.getNome())) {
			return false;
		}

		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!this.getId().equals(other.getId())) {
			return false;
		}

		return true;
	}

	public static Boolean checkCidadeEstadoUnique(String value, List<Long> idsDomains) {
		Cidade.log("value", value);
		Cidade.log("idsDomains", idsDomains);

		Long estadoId = null;
		if (idsDomains != null && idsDomains.size() != 0) {
			estadoId = idsDomains.get(0);
		}

		// Verifica se existe uma cidade com o mesmo nome nesse estado
		if (estadoId != null) {
			Estado estado = Estado.find(estadoId);
			if (estado != null) {
				Cidade.findBy(value, estado.getNome());
				return (Cidade.findBy(value, estado.getNome()).size() == 0);
			}
		}

		// Procura por qualquer cidade com o mesmo nome,
		// independentemente do estado que esteja cadastrada
		return (Cidade.findBy(value, null).isEmpty());
	}

	@Override
	public int compareTo(Cidade o) {
		if (o == null) {
			return -1;
		}

		return this.getNome().compareTo(o.getNome());
	}
}
