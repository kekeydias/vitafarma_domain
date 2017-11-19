package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.Collections;
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
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "CUPONS_VENDA", catalog = "", schema = "")
@SequenceGenerator(name = "INC_CUPOM_ID", sequenceName = "CUPOM_ID_GEN")
public class CupomVenda extends MyEntity implements Serializable, Comparable<CupomVenda> {
	private static final long serialVersionUID = 7871371469402612360L;

	public CupomVenda() {
		this(null, null, null, null, null);
	}

	public CupomVenda(Long id, Double descontoVenda, Double totalVenda, String versaoSistema, Venda venda) {
		this.setId(id);
		this.setDescontoVenda(descontoVenda);
		this.setTotalVenda(totalVenda);
		this.setVenda(venda);
		this.setVersaoSistema(versaoSistema);
	}

	private void copyProperties(CupomVenda cupomVenda) {
		this.setDescontoVenda(cupomVenda.getDescontoVenda());
		this.setTotalVenda(cupomVenda.getTotalVenda());
		this.setVenda(cupomVenda.getVenda());
		this.setVersaoSistema(cupomVenda.getVersaoSistema());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_CUPOM_ID")
	@Column(nullable = false, name = "ID")
	private Long id = null;

	@NotNull
	@ManyToOne(targetEntity = Venda.class, optional = true)
	@JoinColumn(name = "CUPOM_VENDA_ID", nullable = true)
	private transient Venda venda = null;

	@Column(nullable = false, name = "TOTAL_VENDA")
	private Double totalVenda;

	@Column(nullable = false, name = "DESCONTO_VENDA")
	private Double descontoVenda = null;

	@Column(nullable = false, name = "VERSAO_SISTEMA")
	private String versaoSistema = null;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Venda getVenda() {
		return this.venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}

	public Double getTotalVenda() {
		return this.totalVenda;
	}

	public void setTotalVenda(Double totalVenda) {
		this.totalVenda = totalVenda;
	}

	public Double getDescontoVenda() {
		return this.descontoVenda;
	}

	public void setDescontoVenda(Double descontoVenda) {
		this.descontoVenda = descontoVenda;
	}

	public String getVersaoSistema() {
		return this.versaoSistema;
	}

	public void setVersaoSistema(String versaoSistema) {
		this.versaoSistema = versaoSistema;
	}

	@Override
	public String toString() {
		String result = ("\nId :" + this.getId() + "\nVersão do Sistema :" + this.getVersaoSistema() + "\nDesconto :"
				+ this.getDescontoVenda() + "\nTotal :" + this.getTotalVenda() + "\nVenda :" + this.getVenda());

		return result;
	}

	public static CupomVenda find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		CupomVenda cupomVenda = (new CupomVenda()).getEntityManager().find(CupomVenda.class, id);

		return cupomVenda;
	}

	@SuppressWarnings("unchecked")
	public static CupomVenda findByVenda(Venda venda) {
		MyEntity.log("venda", venda);

		if (venda == null) {
			return null;
		}

		// Retorna o último cupom emitido dessa venda
		Query q = (new CupomVenda()).getEntityManager()
				.createQuery(" SELECT o FROM CupomVenda o WHERE o.venda = :venda ");

		q.setParameter("venda", venda);
		List<CupomVenda> resultList = q.getResultList();

		if (resultList == null || resultList.size() == 0) {
			return null;
		}

		Collections.sort(resultList);
		return resultList.get(resultList.size() - 1);
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
		CupomVenda cupomVenda = CupomVenda.find(this.getId());

		if (cupomVenda != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(cupomVenda);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected CupomVenda merge() {
		CupomVenda cupomVenda = CupomVenda.find(this.getId());

		if (cupomVenda != null) {
			this.getEntityManager().getTransaction().begin();

			cupomVenda = this.getEntityManager().merge(this);
			cupomVenda.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return cupomVenda;
	}

	public void updateItensVenda() {
		List<ItemVenda> itensVenda = ItemVenda.findByVenda(this.getVenda());

		if (itensVenda.size() == 0) {
			return;
		}

		try {
			for (ItemVenda itemVenda : itensVenda) {
				itemVenda.setCupomVenda(this);

				itemVenda.merge();
			}
		} catch (Exception ex) {
			Logger logger = Logger.getLogger("CupomVenda");

			logger.warning("Erro ao atualizar o cupomVenda de um objeto itemVenda: " + ex.getCause() + " - "
					+ ex.getMessage());

			System.err.println(ex);
		}
	}

	@Override
	public int compareTo(CupomVenda o) {
		if (o == null) {
			return -1;
		}

		int result = this.getVenda().compareTo(o.getVenda());

		if (result == 0) {
			result = this.getId().compareTo(o.getId());
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
