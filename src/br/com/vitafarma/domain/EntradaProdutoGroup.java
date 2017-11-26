package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import br.com.vitafarma.util.InputOutputUtils;

@Entity
@Table(name = "ENT_PRD_GROUP", catalog = "", schema = "")
@SequenceGenerator(name = "INC_ENT_PRD_GRP_ID", sequenceName = "ENT_PRD_GRP_ID_GEN")
public class EntradaProdutoGroup extends MyEntity implements Serializable, Comparable<EntradaProdutoGroup> {
	private static final long serialVersionUID = 4544686926489096340L;
	private static final int MAX_RESULTS = 20;

	public EntradaProdutoGroup() {
		this(null, null, null, null);
	}

	public EntradaProdutoGroup(Long id, Date dataEntrada, Fornecedor fornecedor, NotaFiscal notaFiscal) {
		this.setId(id);
		this.setDataEntrada(dataEntrada);
		this.setFornecedor(fornecedor);
		this.setNotaFiscal(notaFiscal);
	}

	private void copyProperties(EntradaProdutoGroup entradaProdutoGroup) {
		this.setDataEntrada(entradaProdutoGroup.getDataEntrada());
		this.setFornecedor(entradaProdutoGroup.getFornecedor());
		this.setNotaFiscal(entradaProdutoGroup.getNotaFiscal());
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "\nId: " + this.getId() + "\nFornecedor: " + this.getFornecedor() + "\nNotaFiscal: "
				+ this.getNotaFiscal() + "\nData: " + this.getDataEntrada();
	}

	public Date getDataEntrada() {
		return this.dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Fornecedor getFornecedor() {
		return this.fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public NotaFiscal getNotaFiscal() {
		return this.notaFiscal;
	}

	public void setNotaFiscal(NotaFiscal notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public Double getValorTotalEntrada() {
		Double result = 0.0;
		List<EntradaProduto> itensEntrada = EntradaProduto.findByGroup(this);

		for (EntradaProduto item : itensEntrada) {
			result += (item.getPrecoUnitario() * item.getQuantidade());
		}

		return result;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_ENT_PRD_GRP_ID")
	@Column(name = "ID", nullable = false)
	private Long id = null;

	@Column(name = "DATA_ENTRADA", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Type(type = "timestamp")
	private Date dataEntrada = null;

	@NotNull
	@ManyToOne(targetEntity = Fornecedor.class, optional = false)
	@JoinColumn(name = "ENT_PRD_GRP_FORN_ID", nullable = false)
	private Fornecedor fornecedor = null;

	@ManyToOne(targetEntity = NotaFiscal.class, optional = true)
	@JoinColumn(name = "ENT_PRD_GRP_NF_ID", nullable = true)
	private NotaFiscal notaFiscal = null;

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

	@Override
	protected void persist() {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().persist(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	public void remove() {
		EntradaProdutoGroup entradaProdutoGroup = EntradaProdutoGroup.find(this.getId());

		if (entradaProdutoGroup != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(entradaProdutoGroup);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	@Override
	protected EntradaProdutoGroup merge() {
		EntradaProdutoGroup entradaProdutoGroup = EntradaProdutoGroup.find(this.getId());

		if (entradaProdutoGroup != null) {
			this.getEntityManager().getTransaction().begin();

			entradaProdutoGroup = this.getEntityManager().merge(this);
			entradaProdutoGroup.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return entradaProdutoGroup;
	}

	@SuppressWarnings("unchecked")
	public static List<EntradaProdutoGroup> findAll() {
		return (new EntradaProdutoGroup()).getEntityManager().createQuery(" SELECT o FROM EntradaProdutoGroup o ")
				.getResultList();
	}

	public static EntradaProdutoGroup find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		return (new EntradaProdutoGroup()).getEntityManager().find(EntradaProdutoGroup.class, id);
	}

	@SuppressWarnings("unchecked")
	public static List<EntradaProdutoGroup> findBy(Long codigoFornecedor, String nomeFornecedor, Date dataEntrada,
			String codigoNotaFiscal) {
		MyEntity.log("codigoFornecedor", codigoFornecedor);
		MyEntity.log("nomeFornecedor", nomeFornecedor);
		MyEntity.log("dataEntrada", dataEntrada);
		MyEntity.log("codigoNotaFiscal", codigoNotaFiscal);

		boolean emptyFilter = true;
		if (!InputOutputUtils.isBlank(codigoFornecedor) || !InputOutputUtils.isBlank(nomeFornecedor)
				|| !InputOutputUtils.isBlank(dataEntrada) || !InputOutputUtils.isBlank(codigoNotaFiscal)) {
			emptyFilter = false;
		}

		// Código do fornecedor
		String codigoFornecedorQuery = "";
		if (!InputOutputUtils.isBlank(codigoFornecedor)) {
			codigoFornecedorQuery = " o.fornecedor.id = :codigoFornecedor AND ";
		}

		// Nome do fornecedor
		nomeFornecedor = ((nomeFornecedor == null) ? "" : nomeFornecedor);
		nomeFornecedor = ("%" + nomeFornecedor.replace('*', '%') + "%");

		String nomeFornecedorQuery = "";
		if (!InputOutputUtils.isBlank(nomeFornecedor)) {
			nomeFornecedorQuery = " LOWER ( o.fornecedor.nome ) LIKE LOWER ( :nomeFornecedor ) AND ";
		}

		// Data da entrada
		String dataEntradaQuery = "";
		if (!InputOutputUtils.isBlank(dataEntrada)) {
			dataEntradaQuery = " o.dataEntrada >= :dataEntrada AND o.dataEntrada < :dataEntrada1 AND ";
		}

		// Nota Fiscal
		codigoNotaFiscal = ((codigoNotaFiscal == null) ? "" : codigoNotaFiscal);
		codigoNotaFiscal = ("%" + codigoNotaFiscal.replace('*', '%') + "%");

		String codigoNotaFiscalQuery = "";
		if (!InputOutputUtils.isBlank(codigoNotaFiscal)) {
			codigoNotaFiscalQuery = " LOWER ( o.notaFiscal.codigo ) LIKE LOWER ( :codigoNotaFiscal ) AND ";
		}

		Query q = (new EntradaProduto()).getEntityManager().createQuery(" SELECT o FROM EntradaProdutoGroup o WHERE "
				+ codigoFornecedorQuery + nomeFornecedorQuery + dataEntradaQuery + codigoNotaFiscalQuery + " 1 = 1 ");

		if (!InputOutputUtils.isBlank(codigoFornecedor)) {
			q.setParameter("codigoFornecedor", codigoFornecedor);
		}

		if (!InputOutputUtils.isBlank(nomeFornecedor)) {
			q.setParameter("nomeFornecedor", nomeFornecedor);
		}

		if (!InputOutputUtils.isBlank(dataEntrada)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dataEntrada);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.DATE, 1);

			q.setParameter("dataEntrada", dataEntrada);
			q.setParameter("dataEntrada1", calendar.getTime());
		}

		if (!InputOutputUtils.isBlank(codigoNotaFiscal)) {
			q.setParameter("codigoNotaFiscal", codigoNotaFiscal);
		}

		if (emptyFilter) {
			q.setMaxResults(EntradaProdutoGroup.MAX_RESULTS);
		}

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<EntradaProdutoGroup> findBy(Fornecedor fornecedor, Date dataInicio, Date dataFim) {
		MyEntity.log("fornecedor", fornecedor);
		MyEntity.log("dataInicio", dataInicio);
		MyEntity.log("dataFim", dataFim);

		// Fornecedor
		String fornecedorQuery = "";
		if (!InputOutputUtils.isBlank(fornecedor)) {
			fornecedorQuery = " o.fornecedor = :fornecedor AND ";
		}

		// Data de início
		String dataInicioQuery = "";
		if (!InputOutputUtils.isBlank(dataInicio)) {
			dataInicioQuery = " o.dataEntrada >= :dataInicio AND ";
		}

		// Data de fim
		String dataFimQuery = "";
		if (!InputOutputUtils.isBlank(dataFim)) {
			dataFimQuery = " o.dataEntrada <= :dataFim AND ";
		}

		Query q = (new EntradaProduto()).getEntityManager().createQuery(" SELECT o FROM EntradaProdutoGroup o WHERE "
				+ fornecedorQuery + dataInicioQuery + dataFimQuery + "1 = 1 ");

		if (!InputOutputUtils.isBlank(fornecedor)) {
			q.setParameter("fornecedor", fornecedor);
		}

		if (!InputOutputUtils.isBlank(dataInicio)) {
			q.setParameter("dataInicio", dataInicio);
		}

		if (!InputOutputUtils.isBlank(dataFim)) {
			q.setParameter("dataFim", dataFim);
		}

		return q.getResultList();
	}

	@Override
	public int compareTo(EntradaProdutoGroup o) {
		if (o == null) {
			return -1;
		}

		int result = this.getFornecedor().compareTo(o.getFornecedor());
		if (result == 0) {
			result = this.getDataEntrada().compareTo(o.getDataEntrada());
			if (result == 0) {
				result = this.getId().compareTo(o.getId());
			}
		}

		return result;
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
