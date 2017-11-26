package br.com.vitafarma.domain;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
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
import javax.validation.constraints.Size;

import br.com.vitafarma.util.FormatUtils;

@Entity
@Table(name = "CENARIOS", catalog = "", schema = "")
@SequenceGenerator(name = "INC_CENARIO_ID", sequenceName = "CENARIO_ID_GEN")
public class Cenario extends MyEntity implements Serializable, Comparable<Cenario> {
	private static final long serialVersionUID = -8610380359760552949L;

	public Cenario() {
		this(null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	public Cenario(Long id, String nome, Date dataCriacao, Date dataAtualizacao, String versaoSistema, Long cnpj,
			String inscricaoEstadual, String inscricaoMunipal, String coo, Usuario criadoPor, Usuario atualizadoPor,
			String endereco1, String endereco2) {
		this.setId(id);
		this.setNome(nome);
		this.setDataCriacao(dataCriacao);
		this.setDataAtualizacao(dataAtualizacao);
		this.setVersaoSistema(versaoSistema);
		this.setCnpj(cnpj);
		this.setInscricaoEstadual(inscricaoEstadual);
		this.setInscricaoMunicipal(inscricaoMunipal);
		this.setCoo(coo);
		this.setCriadoPor(criadoPor);
		this.setAtualizadoPor(atualizadoPor);
		this.setEndereco1(endereco1);
		this.setEndereco2(endereco2);
	}

	private void copyProperties(Cenario cenario) {
		if (cenario == null) {
			return;
		}

		this.setNome(cenario.getNome());
		this.setEndereco1(cenario.getEndereco2());
		this.setEndereco2(cenario.getEndereco2());
		this.setDataCriacao(cenario.getDataCriacao());
		this.setDataAtualizacao(cenario.getDataAtualizacao());
		this.setVersaoSistema(cenario.getVersaoSistema());
		this.setCnpj(cenario.getCnpj());
		this.setInscricaoEstadual(cenario.getInscricaoEstadual());
		this.setInscricaoMunicipal(cenario.getInscricaoMunicipal());
		this.setCoo(cenario.getCoo());
		this.setCriadoPor(cenario.getCriadoPor());
		this.setAtualizadoPor(cenario.getAtualizadoPor());
	}

	@NotNull
	@ManyToOne(targetEntity = Usuario.class, optional = false)
	@JoinColumn(name = "CEN_CREATOR_ID", nullable = false)
	private Usuario criadoPor = null;

	@NotNull
	@ManyToOne(targetEntity = Usuario.class, optional = false)
	@JoinColumn(name = "CEN_UPDATER_ID", nullable = false)
	private Usuario atualizadoPor = null;

	@Column(nullable = false, name = "NOME")
	@Size(min = 1, max = 50)
	private String nome = null;

	@Column(nullable = false, name = "DATA_CRIACAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao = null;

	@Column(nullable = true, name = "CEN_DT_ATUALIZACAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAtualizacao = null;

	@Column(nullable = false, name = "CNPJ")
	private Long cnpj = null;

	@Column(nullable = false, name = "INSCRICAO_ESTADUAL")
	private String inscricaoEstadual = null;

	@Column(nullable = false, name = "INSCRICAO_MUNICIPAL")
	private String inscricaoMunicipal = null;

	@Column(nullable = false, name = "COO")
	private String coo = null;

	@Column(nullable = false, name = "VERSAO_SISTEMA")
	private String versaoSistema = null;

	@Column(nullable = false, name = "ENDERECO1")
	private String endereco1 = null;

	@Column(nullable = false, name = "ENDERECO2")
	private String endereco2 = null;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("\nId: ").append(this.getId()).append(", ");
		sb.append("\nCriadoPor: ").append(this.getCriadoPor()).append(", ");
		sb.append("\nAtualizadoPor: ").append(this.getAtualizadoPor()).append(", ");
		sb.append("\nNome: ").append(this.getNome()).append(", ");
		sb.append("\nEndereco1: ").append(this.getEndereco1()).append(", ");
		sb.append("\nEndereco2: ").append(this.getEndereco2()).append(", ");
		sb.append("\nDataCriacao: ").append(this.getDataCriacao()).append(", ");
		sb.append("\nDataAtualizacao: ").append(this.getDataAtualizacao()).append(", ");
		sb.append("\nCnpj: ").append(this.getCnpj()).append(", ");
		sb.append("\nInscricao Estadual: ").append(this.getInscricaoEstadual()).append(", ");
		sb.append("\nInscricao Municipal: ").append(this.getInscricaoMunicipal()).append(", ");
		sb.append("\nCoo: ").append(this.getCoo()).append(", ");
		sb.append("\nVersao do Sistema: ").append(this.getVersaoSistema());

		return sb.toString();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_CENARIO_ID")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Override
	protected void persist() {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().persist(this);
		this.getEntityManager().flush();
		this.getEntityManager().getTransaction().commit();
		this.getEntityManager().refresh(this);
	}

	public void remove() {
		Cenario cenario = Cenario.find(this.getId());

		if (cenario != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(cenario);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	@Override
	protected Cenario merge() {
		Cenario cenario = Cenario.find(this.getId());

		if (cenario != null) {
			this.getEntityManager().getTransaction().begin();

			cenario = this.getEntityManager().merge(this);
			cenario.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return cenario;
	}

	private static Cenario createMasterData() throws NoSuchAlgorithmException {
		Usuario usuario = Usuario.findAll().get(0);
		Date today = new Date();
		String versaoSistema = FormatUtils.getDateString(today);

		Cenario cenario = new Cenario(null, "Vitafarma", today, today, versaoSistema, 12345678901234L,
				"InscricaoEstadual", "InscricaoMunipal", "COO", usuario, usuario, "endereco1", "endereco2");

		cenario.persist();
		return cenario;
	}

	@SuppressWarnings("unchecked")
	public static Cenario getCurrentCenario() throws NoSuchAlgorithmException {
		Cenario cenario = null;

		Query q = (new Cenario()).getEntityManager().createQuery(" SELECT o FROM Cenario o ");

		List<Cenario> resultList = q.getResultList();

		if (resultList.isEmpty()) {
			cenario = Cenario.createMasterData();
		} else {
			Collections.sort(resultList);

			cenario = resultList.get(resultList.size() - 1);
		}

		return cenario;
	}

	@SuppressWarnings("unchecked")
	public static List<Cenario> findAll() throws NoSuchAlgorithmException {
		Query q = (new Cenario()).getEntityManager().createQuery(" SELECT o FROM Cenario o ");

		List<Cenario> result = q.getResultList();

		if (result.isEmpty()) {
			result.add(Cenario.createMasterData());
		}

		return result;
	}

	public static Cenario find(Long id) {
		MyEntity.log("id", id);

		if (id == null) {
			return null;
		}

		return (new Cenario()).getEntityManager().find(Cenario.class, id);
	}

	public Usuario getCriadoPor() {
		return this.criadoPor;
	}

	public void setCriadoPor(Usuario criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Usuario getAtualizadoPor() {
		return this.atualizadoPor;
	}

	public void setAtualizadoPor(Usuario atualizadoPor) {
		this.atualizadoPor = atualizadoPor;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataAtualizacao() {
		return this.dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public Long getCnpj() {
		return this.cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public String getInscricaoEstadual() {
		return this.inscricaoEstadual;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public String getInscricaoMunicipal() {
		return this.inscricaoMunicipal;
	}

	public void setInscricaoMunicipal(String inscricaoMunicipal) {
		this.inscricaoMunicipal = inscricaoMunicipal;
	}

	public String getCoo() {
		return this.coo;
	}

	public void setCoo(String coo) {
		this.coo = coo;
	}

	public String getVersaoSistema() {
		return this.versaoSistema;
	}

	public void setVersaoSistema(String versaoSistema) {
		this.versaoSistema = versaoSistema;
	}

	public String getEndereco1() {
		return this.endereco1;
	}

	public void setEndereco1(String endereco1) {
		this.endereco1 = endereco1;
	}

	public String getEndereco2() {
		return this.endereco2;
	}

	public void setEndereco2(String endereco2) {
		this.endereco2 = endereco2;
	}

	@Override
	public int compareTo(Cenario o) {
		if (o == null) {
			return 1;
		}

		return this.getId().compareTo(o.getId());
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
