package br.com.vitafarma.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "PESSOAS")
@SequenceGenerator(name = "INC_PESSOA_ID", sequenceName = "PESSOA_ID_GEN")
public abstract class Pessoa extends MyEntity implements Serializable {
	private static final long serialVersionUID = 6807614048195762603L;

	public Pessoa(Long id, String nome, Long telefone, String endereco, String email, Cidade cidade) {
		this.setId(id);
		this.setNome(nome);
		this.setTelefone(telefone);
		this.setEndereco(endereco);
		this.setEmail(email);
		this.setCidade(cidade);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "INC_PESSOA_ID")
	@Column(nullable = false, name = "ID")
	private Long id = null;

	@Column(nullable = false, name = "NOME")
	private String nome = null;

	@Column(nullable = true, name = "TELEFONE")
	private Long telefone = null;

	@Column(nullable = true, name = "ENDERECO")
	private String endereco = null;

	@Column(nullable = true, name = "EMAIL")
	private String email = null;

	@NotNull
	@ManyToOne(targetEntity = Cidade.class, optional = false)
	@JoinColumn(name = "PESSOA_CIDADE_ID", nullable = false)
	private transient Cidade cidade = null;

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

	public Long getTelefone() {
		return this.telefone;
	}

	public void setTelefone(Long telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEndereco() {
		return this.endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public Cidade getCidade() {
		return this.cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	@Override
	public String toString() {
		return "\nId: " + this.getId() + "\nNome: " + this.getNome() + "\nTelefone: " + this.getTelefone()
				+ "\nEndereco: " + this.getEndereco() + "\nCidade: " + this.getCidade() + "\nEstado: "
				+ this.getCidade().getEstado().toString() + "\nEmail: " + this.getEmail();
	}
}
