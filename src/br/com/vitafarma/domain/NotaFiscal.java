package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "NOTAS_FISCAIS", catalog = "", schema = "")
@SequenceGenerator(name = "INC_NF_ID", sequenceName = "NF_ID_GEN")
public class NotaFiscal extends MyEntity implements Serializable, Comparable<NotaFiscal> {
	private static final long serialVersionUID = 4544686926489096340L;
	private static final int MAX_RESULTS = 20;

	public NotaFiscal() {
		this(null, null, null, null);
	}

	public NotaFiscal(Long id, String codigo, Date data, Fornecedor fornecedor) {
		this.setId(id);
		this.setCodigo(codigo);
		this.setData(data);
		this.setFornecedor(fornecedor);
	}

	private void copyProperties(NotaFiscal notaFiscal) {
		this.setCodigo(notaFiscal.getCodigo());
		this.setData(notaFiscal.getData());
		this.setFornecedor(notaFiscal.getFornecedor());
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Fornecedor getFornecedor() {
		return this.fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Override
	public String toString() {
		String fornecedorStr = (this.getFornecedor() == null ? "--" : this.getFornecedor().toString());

		return ("\nId: " + this.getId() + "\nCodigo: " + this.getCodigo() + "\nData: " + this.getData()
				+ "\nFornecedor: " + fornecedorStr);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_NF_ID")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "CODIGO", nullable = false)
	private String codigo;

	@Column(name = "DATA", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Type(type = "timestamp")
	private Date data;

	@NotNull
	@ManyToOne(targetEntity = Fornecedor.class, optional = false)
	@JoinColumn(name = "NF_FORN_ID", nullable = false)
	private transient Fornecedor fornecedor = null;

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
		NotaFiscal notaFiscal = NotaFiscal.find(this.getId());

		if (notaFiscal != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(notaFiscal);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected NotaFiscal merge() {
		NotaFiscal notaFiscal = NotaFiscal.find(this.getId());

		if (notaFiscal != null) {
			this.getEntityManager().getTransaction().begin();

			notaFiscal = this.getEntityManager().merge(this);
			notaFiscal.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return notaFiscal;
	}

	@SuppressWarnings("unchecked")
	public static List<NotaFiscal> findAll() {
		return (new NotaFiscal()).getEntityManager().createQuery(" SELECT o FROM NotaFiscal o ").getResultList();
	}

	public static NotaFiscal find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		NotaFiscal notaFiscal = (new NotaFiscal()).getEntityManager().find(NotaFiscal.class, id);

		return notaFiscal;
	}

	@SuppressWarnings("unchecked")
	public static List<NotaFiscal> findBy(String codigoNotaFiscal) {
		MyEntity.log("codigoNotaFiscal", codigoNotaFiscal);

		boolean emptyFilter = true;
		if (!InputOutputUtils.isBlank(codigoNotaFiscal)) {
			emptyFilter = false;
		}

		codigoNotaFiscal = ((codigoNotaFiscal == null) ? "" : codigoNotaFiscal);
		codigoNotaFiscal = ("%" + codigoNotaFiscal.replace('*', '%') + "%");

		String codigoNotaFiscalQuery = "";
		if (!InputOutputUtils.isBlank(codigoNotaFiscal)) {
			codigoNotaFiscalQuery = " LOWER ( o.codigoNotaFiscal ) LIKE LOWER ( :codigoNotaFiscal ) AND ";
		}

		Query q = (new NotaFiscal()).getEntityManager()
				.createQuery(" SELECT o FROM NotaFiscal o WHERE " + codigoNotaFiscalQuery + " 1 = 1 ");

		if (!InputOutputUtils.isBlank(codigoNotaFiscal)) {
			q.setParameter("codigoNotaFiscal", codigoNotaFiscal);
		}

		// Verifica se deve-se limitar o tamanho da busca
		if (emptyFilter) {
			q.setMaxResults(NotaFiscal.MAX_RESULTS);
		}

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static NotaFiscal findByCodigoNotaFiscalExactly(String codigo) {
		MyEntity.log("codigo", codigo);

		if (InputOutputUtils.isBlank(codigo)) {
			return null;
		}

		Query q = (new NotaFiscal()).getEntityManager()
				.createQuery(" SELECT o FROM NotaFiscal o " + " WHERE LOWER ( o.codigo ) = " + " LOWER ( :codigo ) ");

		q.setParameter("codigo", codigo);

		List<NotaFiscal> resultList = q.getResultList();

		if (resultList == null || resultList.size() == 0) {
			return null;
		}

		if (resultList.size() >= 2) {
			Logger logger = Logger.getLogger("NotaFiscal");
			logger.warning("Existem " + resultList.size() + " notas fiscais com o mesmo codigo ( " + codigo + " )");
		}

		return resultList.get(0);
	}

	/*
	 * Verifica se o código de nota fiscal informado é único.
	 * 
	 * TRUE ==> NÃO existe nenhum nota fiscal cadastrada no sistema com o código
	 * informado
	 * 
	 * FALSE ==> existe pelo menos uma nota fiscal cadastrada no sistema com o
	 * código informado
	 */
	public static Boolean checkCodigoUnique(String value) {
		MyEntity.log("value", value);
		return (NotaFiscal.findByCodigoNotaFiscalExactly(value) == null);
	}

	@Override
	public int compareTo(NotaFiscal o) {
		if (o == null) {
			return -1;
		}

		return this.getCodigo().compareTo(o.getCodigo());
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
