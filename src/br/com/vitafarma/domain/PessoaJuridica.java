package br.com.vitafarma.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "PESSOAS_JURIDICAS")
@PrimaryKeyJoinColumn(name = "ID_PESSOA")
public abstract class PessoaJuridica extends Pessoa implements Serializable {
	private static final long serialVersionUID = 3540489835980879612L;

	public PessoaJuridica() {
		this(null, null, null, null, null, null, null, null);
	}

	public PessoaJuridica(Long id, String nome, String nomeFantasia, Long cnpj, Long telefone, String endereco,
			String email, Cidade cidade) {
		super(id, nome, telefone, endereco, email, cidade);

		this.setNomeFantasia(nomeFantasia);
		this.setCnpj(cnpj);
	}

	@Column(nullable = false, name = "CNPJ")
	private Long cnpj;

	@Column(nullable = false, name = "NOME_FANTASIA")
	private String nomeFantasia;

	public Long getCnpj() {
		return this.cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public String getNomeFantasia() {
		return this.nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	@Override
	public String toString() {
		return (super.toString() + "\nNomeFantasia: " + this.getNomeFantasia() + "\nCnpj: " + this.getCnpj());
	}
}
