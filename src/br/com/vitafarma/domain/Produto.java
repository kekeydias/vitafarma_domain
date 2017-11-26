package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Table(name = "PRODUTOS", catalog = "", schema = "")
@SequenceGenerator(name = "INC_PRD_ID", sequenceName = "PRD_ID_GEN")
public class Produto extends MyEntity implements Serializable, Comparable<Produto> {
	private static final long serialVersionUID = 4544686926489096340L;

	private static final int MAX_RESULTS = 20;

	public Produto() {
		this(null, null, null, null, 0.0, 0.0, null, null, null);
	}

	public Produto(Long id, Long medAbc, String nome, String descricao, Double preco, Double estoque,
			String unidadeEntrada, String unidadeVenda, Laboratorio laboratorio) {
		this.setId(id);
		this.setMedAbc(medAbc);
		this.setNome(nome);
		this.setDescricao(descricao);
		this.setPreco(preco);
		this.setEstoque(estoque);
		this.setUnidadeEntrada(unidadeEntrada);
		this.setUnidadeVenda(unidadeVenda);
		this.setLaboratorio(laboratorio);
		this.check();
	}

	private void copyProperties(Produto produto) {
		if (produto == null) {
			return;
		}

		this.setNome(produto.getNome());
		this.setDescricao(produto.getDescricao());
		this.setPreco(produto.getPreco());
		this.setEstoque(produto.getEstoque());
		this.setUnidadeEntrada(produto.getUnidadeEntrada());
		this.setUnidadeVenda(produto.getUnidadeVenda());
		this.setLaboratorio(produto.getLaboratorio());
		this.setMedAbc(produto.getMedAbc());
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

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Double getPreco() {
		return this.preco;
	}

	public void setPreco(Double preco) {
		this.preco = (preco == null ? 0.0 : preco);
	}

	public Double getEstoque() {
		return this.estoque;
	}

	public void setEstoque(Double estoque) {
		this.estoque = (estoque == null ? 0.0 : estoque);
	}

	public String getUnidadeEntrada() {
		return this.unidadeEntrada;
	}

	public void setUnidadeEntrada(String unidadeEntrada) {
		this.unidadeEntrada = unidadeEntrada;
	}

	public String getUnidadeVenda() {
		return this.unidadeVenda;
	}

	public void setUnidadeVenda(String unidadeVenda) {
		this.unidadeVenda = unidadeVenda;
	}

	public Laboratorio getLaboratorio() {
		return this.laboratorio;
	}

	public void setLaboratorio(Laboratorio laboratorio) {
		this.laboratorio = laboratorio;
	}

	@Override
	public String toString() {
		String nomeLab = null;

		if (this.getLaboratorio() != null) {
			nomeLab = this.getLaboratorio().toString();
		}

		return ("\nId: " + this.getId() + "\nNome: " + this.getNome() + "\nDescricao: " + this.getDescricao()
				+ "\nPreco: " + this.getPreco() + "\nEstoque: " + this.getEstoque() + "\nUnidade de Entrada: "
				+ this.getUnidadeEntrada() + "\nUnidade de Venda: " + this.getUnidadeVenda() + "\nLaboratorio: "
				+ nomeLab);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_PRD_ID")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "NOME", nullable = true)
	private String nome = null;

	@Column(name = "DESCRICAO", nullable = true)
	private String descricao = null;

	@Column(name = "PRECO", nullable = true)
	private Double preco = 0.0;

	@Column(name = "ESTOQUE", nullable = true, precision = 2, length = 10)
	private Double estoque = 0.0;

	@Column(name = "CODUNICOMPRA", nullable = true)
	private String unidadeEntrada = null;

	@Column(name = "CODUNIVENDA2", nullable = true)
	private String unidadeVenda = null;

	@ManyToOne(targetEntity = Laboratorio.class, optional = true)
	@JoinColumn(name = "PRD_LAB_ID", nullable = true)
	private Laboratorio laboratorio = null;

	@NotNull
	@Column(name = "MED_ABC", nullable = false, unique = false)
	private Long medAbc = null;

	public Long getMedAbc() {
		return this.medAbc;
	}

	public void setMedAbc(Long medAbc) {
		this.medAbc = medAbc;
	}

	private void check() {
		if (this.estoque == null) {
			this.estoque = 0.0;
		}

		if (this.preco == null) {
			this.preco = 0.0;
		}
	}

	public void refresh() {
		this.check();

		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().refresh(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	public void detach() {
		this.check();

		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().detach(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	protected void persist() {
		this.check();

		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().persist(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
	}

	public void remove() {
		Produto produto = Produto.find(this.getId());

		if (produto != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(produto);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Produto merge() {
		Produto produto = Produto.find(this.getId());

		if (produto != null) {
			this.getEntityManager().getTransaction().begin();
			this.check();

			produto = this.getEntityManager().merge(this);
			produto.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();

			produto.check();
		}

		return produto;
	}

	public static List<Produto> checkList(List<Produto> list) {
		if (list == null) {
			return null;
		}

		for (Produto p : list) {
			if (p != null) {
				p.check();
			}
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Produto> findAll() {
		List<Produto> list = (new Produto()).getEntityManager().createQuery(" SELECT o FROM Produto o ")
				.getResultList();

		return Produto.checkList(list);
	}

	@SuppressWarnings("unchecked")
	public static List<Produto> findMaxResultsLimited(int max_results) {
		List<Produto> list = (new Produto()).getEntityManager().createQuery(" SELECT o FROM Produto o ")
				.setMaxResults(max_results).getResultList();

		return Produto.checkList(list);
	}

	public static Produto find(Long id) {
		if (id == null) {
			return null;
		}

		Produto produto = (new Produto()).getEntityManager().find(Produto.class, id);

		if (produto != null) {
			produto.check();
		}

		return produto;
	}

	@SuppressWarnings("unchecked")
	public static List<Produto> findBy(Long codigo, Long medAbc, String nomeDescricao, Double preco,
			String nomeLaboratorio) {
		MyEntity.log("codigo", codigo);
		MyEntity.log("medAbc", medAbc);
		MyEntity.log("nomeDescricao", nomeDescricao);
		MyEntity.log("preco", preco);
		MyEntity.log("nomeLaboratorio", nomeLaboratorio);

		boolean emptyFilter = true;

		if (!InputOutputUtils.isBlank(codigo) || !InputOutputUtils.isBlank(medAbc)
				|| !InputOutputUtils.isBlank(nomeDescricao) || !InputOutputUtils.isBlank(preco)
				|| !InputOutputUtils.isBlank(nomeLaboratorio)) {
			emptyFilter = false;
		} else {
			return Produto.findMaxResultsLimited(Produto.MAX_RESULTS);
		}

		// Código do produto
		String codigoQuery = ("");

		if (!InputOutputUtils.isBlank(codigo)) {
			codigoQuery += (" o.id = :codigo AND ");
		}

		// MedAbc
		String medAbcQuery = ("");

		if (!InputOutputUtils.isBlank(medAbc)) {
			medAbcQuery += " o.medAbc = :medAbc AND ";
		}

		// Nome/Descrição do produto
		String nomeDescricaoQuery = ("");

		if (!InputOutputUtils.isBlank(nomeDescricao)) {
			nomeDescricao = ((nomeDescricao == null) ? "" : nomeDescricao);
			nomeDescricao = ("%" + nomeDescricao.replace('*', '%') + "%");

			// A busca levarará em consideração tanto
			// o nome quanto a descrição do produto informado
			nomeDescricaoQuery = (" ( LOWER ( o.nome ) LIKE LOWER ( :nomeDescricao ) "
					+ " OR LOWER ( o.descricao ) LIKE LOWER ( :nomeDescricao ) ) AND ");
		}

		// Preco do produto
		String precoQuery = ("");

		if (!InputOutputUtils.isBlank(preco)) {
			precoQuery += " o.preco = :preco AND ";
		}

		// Nome do laboratório
		String laboratorioQuery = ("");

		if (!InputOutputUtils.isBlank(nomeLaboratorio)) {
			nomeLaboratorio = ((nomeLaboratorio == null) ? "" : nomeLaboratorio);
			nomeLaboratorio = ("%" + nomeLaboratorio.replace('*', '%') + "%");

			// A busca levarará em consideração tanto
			// o nome quanto nome fantasia do laboratório informado
			laboratorioQuery = (" ( LOWER ( o.laboratorio.nome ) LIKE LOWER ( :nomeLaboratorio ) "
					+ " OR LOWER ( o.laboratorio.nomeFantasia ) LIKE LOWER ( :nomeLaboratorio ) ) AND ");
		}

		Query q = (new Produto()).getEntityManager()
				.createQuery(" SELECT o FROM Produto o " //
						+ " WHERE " + codigoQuery + medAbcQuery + nomeDescricaoQuery //
						+ precoQuery + laboratorioQuery + " 1 = 1 "); //

		if (!InputOutputUtils.isBlank(codigo)) {
			q.setParameter("codigo", codigo);
		}

		if (!InputOutputUtils.isBlank(medAbc)) {
			q.setParameter("medAbc", medAbc);
		}

		if (!InputOutputUtils.isBlank(nomeDescricao)) {
			q.setParameter("nomeDescricao", nomeDescricao);
		}

		if (!InputOutputUtils.isBlank(nomeLaboratorio)) {
			q.setParameter("nomeLaboratorio", nomeLaboratorio);
		}

		if (!InputOutputUtils.isBlank(preco)) {
			q.setParameter("preco", preco);
		}

		// Verifica se deve-se limitar o tamanho da busca
		if (emptyFilter) {
			q.setMaxResults(Produto.MAX_RESULTS);
		}

		return Produto.checkList(q.getResultList());
	}

	@SuppressWarnings("unchecked")
	public static List<Produto> findBy(Laboratorio laboratorio) {
		MyEntity.log("laboratorio", laboratorio);

		// Código do produto
		String laboratorioQuery = ("");

		if (!InputOutputUtils.isBlank(laboratorio)) {
			laboratorioQuery += (" o.laboratorio = :laboratorio AND ");
		} else {
			return findMaxResultsLimited(Produto.MAX_RESULTS);
		}

		Query q = (new Produto()).getEntityManager() //
				.createQuery(" SELECT o FROM Produto o " //
						+ " WHERE " + laboratorioQuery + " 1 = 1 "); //

		if (!InputOutputUtils.isBlank(laboratorio)) {
			q.setParameter("laboratorio", laboratorio);
		}

		return Produto.checkList(q.getResultList());
	}

	public static Produto findBy(Long medAbc) {
		MyEntity.log("medAbc", medAbc);

		// Código MED_ABC
		String medAbcQuery = ("");

		if (!InputOutputUtils.isBlank(medAbc)) {
			medAbcQuery += (" o.medAbc = :medAbc AND ");
		} else {
			return null;
		}

		Query q = (new Produto()).getEntityManager() //
				.createQuery(" SELECT o FROM Produto o " //
						+ " WHERE " + medAbcQuery + " 1 = 1 "); //

		if (!InputOutputUtils.isBlank(medAbc)) {
			q.setParameter("medAbc", medAbc);
		}

		Object result = q.getSingleResult();

		if (result != null && result instanceof Produto) {
			Produto p = (Produto) result;

			p.check();

			return p;
		}

		return null;
	}

	public static Map<Long, Produto> buildCodigoToProdutoMap(List<Produto> produtos) {
		if (produtos == null) {
			return null;
		}

		Map<Long, Produto> produtosMap = new HashMap<Long, Produto>();

		for (Produto produto : produtos) {
			if (produto != null) {
				produtosMap.put(produto.getId(), produto);
			}
		}

		return produtosMap;
	}

	public static Map<Long, Produto> buildMedAbcToProdutoAbcFarmaMap(List<Produto> produtos) {
		if (produtos == null) {
			return null;
		}

		Map<Long, Produto> produtosMap = new HashMap<Long, Produto>();

		for (Produto produto : produtos) {
			if (produto != null) {
				produtosMap.put(produto.getMedAbc(), produto);
			}
		}

		return produtosMap;
	}

	public static boolean updateEstoque(List<Produto> produtos) {
		if (produtos == null) {
			return true;
		}

		boolean success = true;

		for (Produto produto : produtos) {
			success = (success && Produto.updateEstoque(produto));
		}

		return success;
	}

	public static boolean updateEstoque(Produto produto) {
		MyEntity.log("produto", produto);

		if (produto == null) {
			return false;
		}

		try {
			// Total de entradas feitas desse produto
			Double totalEntradas = 0.0;

			List<EntradaProduto> entradasProduto = EntradaProduto.findByProduto(produto);

			for (EntradaProduto entradaProduto : entradasProduto) {
				totalEntradas += entradaProduto.getQuantidade();
			}

			// Total de vendas feitas desse produto
			Double totalVendas = 0.0;

			List<ItemVenda> itensVendaProduto = ItemVenda.findByProduto(produto);

			for (ItemVenda itemVenda : itensVendaProduto) {
				totalVendas += itemVenda.getQuantidade();
			}

			// Atualiza o estoque com o saldo entre
			// as entradas e as saídas do produto
			Double estoque = (totalEntradas - totalVendas);

			produto.setEstoque(estoque);
			produto.merge();
		} catch (Exception ex) {
			System.out.println("Error : " + ex.getCause() + " - " + ex.getMessage());
			log(ex);
			return false;
		}

		return true;
	}

	public static Boolean checkCodigoUnique(String value) {
		MyEntity.log("value", value);

		Long medAbcfarma = Long.parseLong(value);

		return (Produto.findBy(null, medAbcfarma, null, null, null).size() == 0);
	}

	@Override
	public int compareTo(Produto o) {
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
