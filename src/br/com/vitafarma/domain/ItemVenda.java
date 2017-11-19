package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "ITENS_VENDA")
@SequenceGenerator(name = "INC_IT_VENDA_ID", sequenceName = "IT_VENDA_ID_GEN")
public class ItemVenda extends MyEntity implements Serializable, Comparable<ItemVenda> {
	private static final long serialVersionUID = -3829726706123503965L;

	public ItemVenda() {
		this(null, null, null, 0.0, 0.0, 0.0, null);
	}

	public ItemVenda(Long id, Produto produto, Venda venda, Double precoUnitario, Double quantidade, Double desconto,
			CupomVenda cupomVenda) {
		this.setId(id);
		this.setProduto(produto);
		this.setVenda(venda);
		this.setPrecoUnitario(precoUnitario);
		this.setQuantidade(quantidade);
		this.setDesconto(desconto == null ? 0.0 : desconto);
		this.setCupomVenda(cupomVenda);
	}

	private void copyProperties(ItemVenda itemVenda) {
		this.setProduto(itemVenda.getProduto());
		this.setVenda(itemVenda.getVenda());
		this.setPrecoUnitario(itemVenda.getPrecoUnitario());
		this.setQuantidade(itemVenda.getQuantidade());
		this.setDesconto(itemVenda.getDesconto());
		this.setCupomVenda(itemVenda.getCupomVenda());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_IT_VENDA_ID")
	@Column(nullable = false, name = "ID")
	private Long id;

	@Column(nullable = false, name = "PRECO_UNITARIO")
	private Double precoUnitario = 0.0;

	@Column(nullable = false, name = "QUANTIDADE")
	private Double quantidade = 0.0;

	@Column(nullable = true, name = "DESCONTO")
	private Double desconto = 0.0;

	@NotNull
	@ManyToOne(targetEntity = Produto.class, optional = false)
	@JoinColumn(name = "IT_VENDA_PRD_ID", nullable = false)
	private transient Produto produto = null;

	@NotNull
	@ManyToOne(targetEntity = Venda.class, optional = false)
	@JoinColumn(name = "IT_VENDA_VENDA_ID", nullable = false)
	private transient Venda venda = null;

	@ManyToOne(targetEntity = CupomVenda.class, optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "IT_VENDA_CUPOM_ID", nullable = true)
	private transient CupomVenda cupomVenda = null;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Produto getProduto() {
		return this.produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Double getPrecoUnitario() {
		return this.precoUnitario;
	}

	public void setPrecoUnitario(Double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public Double getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public Double getDesconto() {
		return this.desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Venda getVenda() {
		return this.venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}

	public CupomVenda getCupomVenda() {
		return this.cupomVenda;
	}

	public void setCupomVenda(CupomVenda cupomVenda) {
		this.cupomVenda = cupomVenda;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof ItemVenda)) {
			return false;
		}

		ItemVenda itemVenda = (ItemVenda) obj;

		// Se todos os campos principais são nulos,
		// consideramos que os objetos são diferentes
		if (this.id == null && itemVenda.id == null) {
			return false;
		}

		if (id == null) {
			if (itemVenda.id != null) {
				return false;
			}
		} else if (!id.equals(itemVenda.id)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		String result = "";

		if (this.getVenda() != null) {
			result += "\nVenda : " + this.getVenda().toString();
		}

		if (this.getProduto() != null) {
			result += "\nProduto : " + this.getProduto().toString();
		}

		if (this.getPrecoUnitario() != null) {
			result += "\nPreco Unitario : " + this.getPrecoUnitario().toString();
		}

		if (this.getQuantidade() != null) {
			result += "\nQuantidade : " + this.getQuantidade().toString();
		}

		if (this.getDesconto() != null) {
			result += "\nDesconto : " + this.getDesconto().toString();
		}

		if (this.getCupomVenda() != null) {
			result += "\nCupom da Venda : " + this.getCupomVenda().toString();
		}

		// Informa que o objeto é nulo
		if (result.equals("")) {
			result = "<vazio>";
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<ItemVenda> findByVenda(Venda venda) {
		MyEntity.log("venda", venda);

		if (venda == null) {
			return Collections.emptyList();
		}

		Query q = (new ItemVenda()).getEntityManager()
				.createQuery(" SELECT o FROM ItemVenda o WHERE o.venda = :venda ");

		q.setParameter("venda", venda);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<ItemVenda> findByProduto(Produto produto) {
		MyEntity.log("produto", produto);

		if (produto == null) {
			return Collections.emptyList();
		}

		Query q = (new ItemVenda()).getEntityManager()
				.createQuery(" SELECT o FROM ItemVenda o WHERE o.produto = :produto ");

		q.setParameter("produto", produto);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<ItemVenda> findByCliente(Cliente cliente) {
		MyEntity.log("cliente", cliente);

		if (cliente == null) {
			return Collections.emptyList();
		}

		Query q = (new ItemVenda()).getEntityManager()
				.createQuery(" SELECT o FROM ItemVenda o WHERE o.venda.cliente = :cliente ");

		q.setParameter("cliente", cliente);

		return q.getResultList();
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
		ItemVenda itemVenda = ItemVenda.find(this.getId());

		if (itemVenda != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(itemVenda);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected ItemVenda merge() {
		ItemVenda itemVenda = ItemVenda.find(this.getId());

		if (itemVenda != null) {
			this.getEntityManager().getTransaction().begin();

			itemVenda = this.getEntityManager().merge(this);
			itemVenda.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return itemVenda;
	}

	@SuppressWarnings("unchecked")
	public static List<ItemVenda> findAll() {
		return (new ItemVenda()).getEntityManager().createQuery(" SELECT o FROM ItemVenda o ").getResultList();
	}

	public static ItemVenda find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		ItemVenda itemVenda = (new ItemVenda()).getEntityManager().find(ItemVenda.class, id);

		return itemVenda;
	}

	@SuppressWarnings("unchecked")
	public static List<ItemVenda> findByCupomVenda(CupomVenda cupomVenda) {
		MyEntity.log("cupomVenda", cupomVenda);

		if (cupomVenda == null) {
			return Collections.emptyList();
		}

		Query q = (new CupomVenda()).getEntityManager()
				.createQuery(" SELECT o FROM ItemVenda o WHERE o.cupomVenda = :cupomVenda ");

		q.setParameter("cupomVenda", cupomVenda);
		List<ItemVenda> resultList = (List<ItemVenda>) q.getResultList();
		return resultList;
	}

	public Double getPrecoFinal() {
		Double result = InputOutputUtils.getPrecoFinal(this.getPrecoUnitario(), this.getQuantidade(),
				this.getDesconto());

		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<ItemVenda> findBy(Produto produto, Cliente cliente, Date dataInicio, Date dataFim) {
		MyEntity.log("produto", produto);
		MyEntity.log("cliente", cliente);
		MyEntity.log("dataInicio", dataInicio);
		MyEntity.log("dataFim", dataFim);

		// Produto
		String produtoQuery = "";
		if (!InputOutputUtils.isBlank(produto)) {
			produtoQuery = " o.produto = :produto AND ";
		}

		// Cliente
		String clienteQuery = "";
		if (!InputOutputUtils.isBlank(cliente)) {
			clienteQuery = " o.venda.cliente = :cliente AND ";
		}

		// Data de início
		String dataInicioQuery = "";
		if (!InputOutputUtils.isBlank(dataInicio)) {
			dataInicioQuery = " o.venda.dataVenda >= :dataInicio AND ";
		}

		// Data de fim
		String dataFimQuery = "";
		if (!InputOutputUtils.isBlank(dataFim)) {
			dataFimQuery = " o.venda.dataVenda <= :dataFim AND ";
		}

		Query q = (new ItemVenda()).getEntityManager().createQuery(" SELECT o FROM ItemVenda o WHERE " + produtoQuery
				+ clienteQuery + dataInicioQuery + dataFimQuery + "1 = 1 ");

		if (!InputOutputUtils.isBlank(produto)) {
			q.setParameter("produto", produto);
		}

		if (!InputOutputUtils.isBlank(cliente)) {
			q.setParameter("cliente", cliente);
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
	public int compareTo(ItemVenda o) {
		if (o == null) {
			return -1;
		}

		int result = this.getVenda().compareTo(o.getVenda());

		if (result == 0) {
			result = this.getProduto().compareTo(o.getProduto());

			if (result == 0) {
				result = this.getId().compareTo(o.getId());
			}
		}

		return result;
	}
}
