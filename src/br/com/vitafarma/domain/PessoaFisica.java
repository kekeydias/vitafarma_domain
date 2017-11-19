package br.com.vitafarma.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "PESSOAS_FISICAS")
@PrimaryKeyJoinColumn(name = "ID_PESSOA")
public abstract class PessoaFisica extends Pessoa implements Serializable {
	private static final long serialVersionUID = -6270207146443428041L;

	public PessoaFisica() {
		this(null, null, null, null, null, null, null);
	}

	public PessoaFisica(Long id, String nome, Long cpf, Long telefone, String endereco, String email, Cidade cidade) {
		super(id, nome, telefone, endereco, email, cidade);

		this.setCpf(cpf);
	}

	@Column(nullable = false, name = "CPF")
	private Long cpf = null;

	public Long getCpf() {
		return this.cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	@Override
	public String toString() {
		return (super.toString() + "\nCpf: " + this.getCpf());
	}
}
