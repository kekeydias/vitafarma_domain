package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
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
@Table(name = "ENTRADA_PRODUTOS", catalog = "", schema = "")
@SequenceGenerator(name = "INC_ENT_PRD_ID", sequenceName = "ENT_PRD_ID_GEN")
public class EntradaProduto extends MyEntity implements Serializable, Comparable<EntradaProduto> {
	private static final long serialVersionUID = 4544686926489096340L;
	private static final int MAX_RESULTS = 20;

	public EntradaProduto() {
		this(null, null, null, null, null, null, null, null);
	}

	public EntradaProduto(Long id, Date dataEntrada, Double quantidade, Double precoUnitario, Fornecedor fornecedor,
			Produto produto, NotaFiscal notaFiscal, EntradaProdutoGroup entradaProdutoGroup) {
		this.setId(id);
		this.setDataEntrada(dataEntrada);
		this.setQuantidade(quantidade);
		this.setPrecoUnitario(precoUnitario);
		this.setFornecedor(fornecedor);
		this.setProduto(produto);
		this.setNotaFiscal(notaFiscal);
		this.setEntradaProdutoGroup(entradaProdutoGroup);
	}

	private void copyProperties(EntradaProduto entradaProduto) {
		this.setDataEntrada(entradaProduto.getDataEntrada());
		this.setQuantidade(entradaProduto.getQuantidade());
		this.setPrecoUnitario(entradaProduto.getPrecoUnitario());
		this.setFornecedor(entradaProduto.getFornecedor());
		this.setProduto(entradaProduto.getProduto());
		this.setNotaFiscal(entradaProduto.getNotaFiscal());
		this.setEntradaProdutoGroup(entradaProduto.getEntradaProdutoGroup());
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "\nId: " + this.getId() + "\nProduto: " + this.getProduto() + "\nFornecedor: " + this.getFornecedor()
				+ "\nNotaFiscal: " + this.getNotaFiscal() + "\nData: " + this.getDataEntrada() + "\nQuantidade: "
				+ this.getQuantidade() + "\nPreco Unitario: " + this.getPrecoUnitario();
	}

	public Date getDataEntrada() {
		return this.dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Double getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public Double getPrecoUnitario() {
		return this.precoUnitario;
	}

	public void setPrecoUnitario(Double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public Fornecedor getFornecedor() {
		return this.fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Produto getProduto() {
		return this.produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public NotaFiscal getNotaFiscal() {
		return this.notaFiscal;
	}

	public void setNotaFiscal(NotaFiscal notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public EntradaProdutoGroup getEntradaProdutoGroup() {
		return this.entradaProdutoGroup;
	}

	public void setEntradaProdutoGroup(EntradaProdutoGroup entradaProdutoGroup) {
		this.entradaProdutoGroup = entradaProdutoGroup;
	}

	public Double getValorTotalItem() {
		Double result = (this.getPrecoUnitario() * this.getQuantidade());
		return result;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_ENT_PRD_ID")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "DATA_ENTRADA", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Type(type = "timestamp")
	private Date dataEntrada;

	@Column(name = "QUANTIDADE", nullable = true, precision = 5)
	private Double quantidade = 0.0;

	@Column(name = "PRECO_UNITARIO", nullable = true, precision = 2)
	private Double precoUnitario = 0.0;

	@NotNull
	@ManyToOne(targetEntity = Fornecedor.class, optional = false)
	@JoinColumn(name = "ENT_PRD_FORN_ID", nullable = false)
	private Fornecedor fornecedor = null;

	@NotNull
	@ManyToOne(targetEntity = Produto.class, optional = false)
	@JoinColumn(name = "ENT_PRD_PRD_ID", nullable = false)
	private Produto produto = null;

	@ManyToOne(targetEntity = NotaFiscal.class, optional = true)
	@JoinColumn(name = "ENT_PRD_NF_ID", nullable = true)
	private NotaFiscal notaFiscal;

	@NotNull
	@ManyToOne(targetEntity = EntradaProdutoGroup.class, optional = false)
	@JoinColumn(name = "ENT_PRD_GRP_ID", nullable = false)
	private EntradaProdutoGroup entradaProdutoGroup;

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
		EntradaProduto entradaProduto = EntradaProduto.find(this.getId());

		if (entradaProduto != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(entradaProduto);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected EntradaProduto merge() {
		EntradaProduto entradaProduto = EntradaProduto.find(this.getId());

		if (entradaProduto != null) {
			this.getEntityManager().getTransaction().begin();

			entradaProduto = this.getEntityManager().merge(this);
			entradaProduto.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return entradaProduto;
	}

	@SuppressWarnings("unchecked")
	public static List<EntradaProduto> findAll() {
		return (new EntradaProduto()).getEntityManager().createQuery(" SELECT o FROM EntradaProduto o ")
				.getResultList();
	}

	public static EntradaProduto find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		EntradaProduto entradaProduto = (new EntradaProduto()).getEntityManager().find(EntradaProduto.class, id);

		return entradaProduto;
	}

	public static List<EntradaProduto> findByNomeProduto(String nomeProduto) {
		MyEntity.log("nomeProduto", nomeProduto);
		return EntradaProduto.findBy(null, nomeProduto, null, null, null, null);
	}

	@SuppressWarnings("unchecked")
	public static List<EntradaProduto> findBy(Long codigoProduto, String nomeProduto, Long codigoFornecedor,
			String nomeFornecedor, Date dataEntrada, String codigoNotaFiscal) {
		MyEntity.log("codigoProduto", codigoProduto);
		MyEntity.log("nomeProduto", nomeProduto);
		MyEntity.log("codigoFornecedor", codigoFornecedor);
		MyEntity.log("nomeFornecedor", nomeFornecedor);
		MyEntity.log("dataEntrada", dataEntrada);
		MyEntity.log("codigoNotaFiscal", codigoNotaFiscal);

		boolean emptyFilter = true;
		if (!InputOutputUtils.isBlank(codigoProduto) || !InputOutputUtils.isBlank(nomeProduto)
				|| !InputOutputUtils.isBlank(codigoFornecedor) || !InputOutputUtils.isBlank(nomeFornecedor)
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

		// Código do produto
		String codigoProdutoQuery = "";
		if (!InputOutputUtils.isBlank(codigoProduto)) {
			codigoProdutoQuery = " o.produto.id = :codigoProduto AND ";
		}

		// Nome do produto
		nomeProduto = ((nomeProduto == null) ? "" : nomeProduto);
		nomeProduto = ("%" + nomeProduto.replace('*', '%') + "%");

		String nomeProdutoQuery = "";
		if (!InputOutputUtils.isBlank(nomeProduto)) {
			nomeProdutoQuery = " LOWER ( o.produto.nome ) LIKE LOWER ( :nomeProduto ) AND ";
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

		Query q = (new EntradaProduto()).getEntityManager()
				.createQuery(" SELECT o FROM EntradaProduto o WHERE " + codigoProdutoQuery + nomeProdutoQuery
						+ codigoFornecedorQuery + nomeFornecedorQuery + dataEntradaQuery + codigoNotaFiscalQuery
						+ " 1 = 1 ");

		if (!InputOutputUtils.isBlank(codigoProduto)) {
			q.setParameter("codigoProduto", codigoProduto);
		}

		if (!InputOutputUtils.isBlank(nomeProduto)) {
			q.setParameter("nomeProduto", nomeProduto);
		}

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
			q.setMaxResults(EntradaProduto.MAX_RESULTS);
		}

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<EntradaProduto> findByProduto(Produto produto) {
		MyEntity.log("produto", produto);

		if (produto == null) {
			return Collections.emptyList();
		}

		Query q = (new EntradaProduto()).getEntityManager()
				.createQuery(" SELECT o FROM EntradaProduto o WHERE o.produto = :produto ");

		q.setParameter("produto", produto);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<EntradaProduto> findByGroup(EntradaProdutoGroup entradaProdutoGroup) {
		MyEntity.log("entradaProdutoGroup", entradaProdutoGroup);

		if (entradaProdutoGroup == null) {
			return Collections.emptyList();
		}

		Query q = (new EntradaProdutoGroup()).getEntityManager()
				.createQuery(" SELECT o FROM EntradaProduto o WHERE o.entradaProdutoGroup = :entradaProdutoGroup ");

		q.setParameter("entradaProdutoGroup", entradaProdutoGroup);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<EntradaProduto> findBy(Produto produto, Fornecedor fornecedor, Date dataInicio, Date dataFim) {
		MyEntity.log("produto", produto);
		MyEntity.log("fornecedor", fornecedor);
		MyEntity.log("dataInicio", dataInicio);
		MyEntity.log("dataFim", dataFim);

		// Produto
		String produtoQuery = "";
		if (!InputOutputUtils.isBlank(produto)) {
			produtoQuery = " o.produto = :produto AND ";
		}

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

		Query q = (new EntradaProduto()).getEntityManager().createQuery(" SELECT o FROM EntradaProduto o WHERE "
				+ produtoQuery + fornecedorQuery + dataInicioQuery + dataFimQuery + "1 = 1 ");

		if (!InputOutputUtils.isBlank(produto)) {
			q.setParameter("produto", produto);
		}

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
	public int compareTo(EntradaProduto o) {
		if (o == null) {
			return -1;
		}

		int result = this.getEntradaProdutoGroup().compareTo(o.getEntradaProdutoGroup());

		if (result == 0) {
			result = this.getProduto().compareTo(o.getProduto());

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
