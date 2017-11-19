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
@Table(name = "VENDAS")
@SequenceGenerator(name = "INC_VENDA_ID", sequenceName = "VENDA_ID_GENERATOR")
public class Venda extends MyEntity implements Serializable, Comparable<Venda> {
	private static final long serialVersionUID = 34923822287015877L;
	private static final int MAX_RESULTS = 20;

	public Venda() {
		this(null, null, null, null, null, null);
	}

	public Venda(Long id, Date dataVenda, Double desconto, Cliente cliente, Cenario cenario) {
		this(id, dataVenda, desconto, cliente, cenario, null);
	}

	public Venda(Long id, Date dataVenda, Double desconto, Cliente cliente, Cenario cenario, CupomVenda cupomVenda) {
		this.setId(id);
		this.setDataVenda(dataVenda);
		this.setDesconto(desconto == null ? 0.0 : desconto);
		this.setCliente(cliente);
		this.setCenario(cenario);
	}

	private void copyProperties(Venda venda) {
		this.setDataVenda(venda.getDataVenda());
		this.setDesconto(venda.getDesconto());
		this.setCliente(venda.getCliente());
		this.setCenario(venda.getCenario());
	}

	@Override
	public String toString() {
		String result = "";
		if (this.getCliente() != null) {
			result += "\nId do Cliente: " + this.getCliente().getId() + "\nNome do Cliente: "
					+ this.getCliente().getNome();
		}

		if (this.getDataVenda() != null) {
			result += "\nData da Venda: " + this.getDataVenda().toString();
		}

		if (this.getDesconto() != null) {
			result += "\nDesconto(%): " + this.getDesconto();
		}

		if (this.getCenario() != null) {
			result += "\nCenario: " + this.getCenario().toString();
		}

		CupomVenda cupomVenda = CupomVenda.findByVenda(this);
		if (cupomVenda != null) {
			result += "\nCupom da Venda: " + cupomVenda.toString();
		}

		if (result == "") {
			result = "<vazio>";
		}

		return result;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_VENDA_ID")
	@Column(nullable = false, name = "ID")
	private Long id = null;

	@Column(nullable = false, name = "DATA_VENDA")
	@Temporal(TemporalType.TIMESTAMP)
	@Type(type = "timestamp")
	private Date dataVenda = null;

	@Column(nullable = true, name = "DESCONTO", precision = 2)
	private Double desconto = 0.0;

	@NotNull
	@ManyToOne(targetEntity = Cenario.class, optional = false)
	@JoinColumn(name = "VENDA_CENARIO_ID", nullable = false)
	private transient Cenario cenario = null;

	@NotNull
	@ManyToOne(targetEntity = Cliente.class, optional = false)
	@JoinColumn(name = "VENDA_CLIENTE_ID", nullable = false)
	private transient Cliente cliente = null;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataVenda() {
		return this.dataVenda;
	}

	public void setDataVenda(Date dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Double getDesconto() {
		return this.desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Cenario getCenario() {
		return this.cenario;
	}

	public void setCenario(Cenario cenario) {
		this.cenario = cenario;
	}

	public Cliente getCliente() {
		return this.cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Double getSubTotalVenda() {
		return (this.getValorTotalVenda() * ((this.getDesconto() / 100.0) + 1.0));
	}

	public Double getValorTotalVenda() {
		Double result = 0.0;
		List<ItemVenda> itensVenda = ItemVenda.findByVenda(this);

		for (ItemVenda itemVenda : itensVenda) {
			result += itemVenda.getPrecoFinal();
		}

		return result;
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
		Venda venda = Venda.find(this.getId());

		if (venda != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(venda);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Venda merge() {
		Venda venda = Venda.find(this.getId());

		if (venda != null) {
			this.getEntityManager().getTransaction().begin();

			venda = this.getEntityManager().merge(this);
			venda.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return venda;
	}

	@SuppressWarnings("unchecked")
	public static List<Venda> findAll() {
		return (new Venda()).getEntityManager().createQuery(" SELECT o FROM Venda o ").getResultList();
	}

	public static Venda find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		Venda venda = (new Venda()).getEntityManager().find(Venda.class, id);
		return venda;
	}

	@SuppressWarnings("unchecked")
	public static List<Venda> findByCliente(Cliente cliente) {
		MyEntity.log("cliente", cliente);

		if (cliente == null) {
			return Collections.emptyList();
		}

		Query q = (new Venda()).getEntityManager().createQuery(" SELECT o FROM Venda o WHERE o.cliente = :cliente ");

		q.setParameter("cliente", cliente);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<Venda> findBy(Long codigoCliente, String nomeCliente, Long cpfCliente, Date dataVenda) {
		MyEntity.log("codigoCliente", codigoCliente);
		MyEntity.log("nomeCliente", nomeCliente);
		MyEntity.log("cpfCliente", cpfCliente);
		MyEntity.log("dataVenda", dataVenda);

		boolean emptyFilter = true;
		if (!InputOutputUtils.isBlank(codigoCliente) || !InputOutputUtils.isBlank(nomeCliente)
				|| !InputOutputUtils.isBlank(cpfCliente) || !InputOutputUtils.isBlank(dataVenda)) {
			emptyFilter = false;
		}

		// Código do Cliente
		String codigoClienteQuery = "";
		if (!InputOutputUtils.isBlank(codigoCliente)) {
			codigoClienteQuery = " o.cliente.id = :codigoCliente AND ";
		}

		// Nome do Cliente
		nomeCliente = ((nomeCliente == null) ? "" : nomeCliente);
		nomeCliente = ("%" + nomeCliente.replace('*', '%') + "%");

		String nomeClienteQuery = "";
		if (!InputOutputUtils.isBlank(nomeCliente)) {
			nomeClienteQuery = " LOWER ( o.cliente.nome ) LIKE LOWER ( :nomeCliente ) AND ";
		}

		// Cpf do Cliente
		String cpfClienteQuery = "";
		if (!InputOutputUtils.isBlank(cpfCliente)) {
			cpfClienteQuery = " o.cliente.cpf = :cpfCliente AND ";
		}

		// Data da Venda
		String dataVendaQuery = "";
		if (!InputOutputUtils.isBlank(dataVenda)) {
			dataVendaQuery = " o.dataVenda >= :dataVenda AND o.dataVenda < :dataVenda1 AND ";
		}

		Query q = (new Venda()).getEntityManager().createQuery(" SELECT o FROM Venda o WHERE " + codigoClienteQuery
				+ nomeClienteQuery + cpfClienteQuery + dataVendaQuery + " 1 = 1 ");

		if (!InputOutputUtils.isBlank(codigoCliente)) {
			q.setParameter("codigoCliente", codigoCliente);
		}

		if (!InputOutputUtils.isBlank(nomeCliente)) {
			q.setParameter("nomeCliente", nomeCliente);
		}

		if (!InputOutputUtils.isBlank(cpfCliente)) {
			q.setParameter("cpfCliente", cpfCliente);
		}

		if (!InputOutputUtils.isBlank(dataVenda)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dataVenda);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.add(Calendar.DATE, 1);

			q.setParameter("dataVenda", dataVenda);
			q.setParameter("dataVenda1", calendar.getTime());
		}

		// Verifica se deve-se limitar o tamanho da busca
		if (emptyFilter) {
			q.setMaxResults(Venda.MAX_RESULTS);
		}

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static Venda findByCupomVenda(CupomVenda cupomVenda) {
		MyEntity.log("cupomVenda", cupomVenda);

		if (cupomVenda == null) {
			return null;
		}

		Query q = (new Venda()).getEntityManager()
				.createQuery(" SELECT o FROM Venda o WHERE o.cupomVenda = :cupomVenda ");

		q.setParameter("cupomVenda", cupomVenda);
		List<Venda> resultList = q.getResultList();
		return (resultList == null || resultList.size() == 0 ? null : resultList.get(0));
	}

	@Override
	public int compareTo(Venda o) {
		if (o == null) {
			return -1;
		}

		return this.getDataVenda().compareTo(o.getDataVenda());
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
