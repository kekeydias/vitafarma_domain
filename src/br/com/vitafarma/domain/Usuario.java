package br.com.vitafarma.domain;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.vitafarma.util.Encryption;

@Entity
@Table(name = "USUARIOS")
public class Usuario extends MyEntity implements Serializable, Comparable<Usuario> {
	private static final long serialVersionUID = 2505879126546359228L;

	public Long getId() {
		if (this.getUsername() == null) {
			return null;
		}

		return Long.parseLong(((Integer) (this.getUsername().hashCode())).toString());
	}

	public Usuario() {
		this(null, null, null, null, null, null);
	}

	public Usuario(String nome, String email, String username, String password, Boolean enabled, Authority authority) {
		this.setNome(nome);
		this.setEmail(email);
		this.setUsername(username);
		this.setPassword(password);
		this.setEnabled(enabled);
		this.setAuthority(authority);
	}

	private void copyProperties(Usuario usuario) {
		this.setNome(usuario.getNome());
		this.setEmail(usuario.getEmail());
		this.setPassword(usuario.getPassword());
		this.setEnabled(usuario.getEnabled());
		this.setAuthority(usuario.getAuthority());
	}

	@Id
	@Column(nullable = false, name = "USERNAME")
	@Size(min = 1, max = 50)
	private String username = null;

	@Column(nullable = false, name = "PASSWORD")
	@Size(min = 1, max = 255)
	private String password = null;

	@Column(nullable = false, name = "NOME")
	@Size(min = 1, max = 500)
	private String nome = null;

	@Column(nullable = false, name = "EMAIL")
	@Size(min = 1, max = 100)
	private String email = null;

	@Column(nullable = true, name = "ENABLED")
	private Boolean enabled = null;

	@NotNull
	@ManyToOne(targetEntity = Authority.class, optional = false)
	@JoinColumn(nullable = false, name = "USUARIO_AUTHORITY_USERNAME")
	private transient Authority authority = null;

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Authority getAuthority() {
		return this.authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("\nNome: ").append(getNome()).append(", ");
		sb.append("\nEmail: ").append(getEmail()).append(", ");
		sb.append("\nUsername: ").append(getUsername()).append(", ");
		sb.append("\nPassword: ").append(getPassword()).append(", ");
		sb.append("\nEnabled: ").append(getEnabled()).append(", ");
		sb.append("\nAuthority: ").append(getAuthority());

		return sb.toString();
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
		Usuario usuario = Usuario.find(this.getUsername());

		if (usuario != null) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(usuario);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}
	}

	protected Usuario merge() {
		Usuario usuario = Usuario.find(this.getUsername());

		if (usuario != null) {
			this.getEntityManager().getTransaction().begin();

			usuario = this.getEntityManager().merge(this);
			usuario.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return usuario;
	}

	public static Usuario find(String username) {
		MyEntity.log("username", username);

		if (username == null) {
			return null;
		}

		Usuario usuario = (new Usuario()).getEntityManager().find(Usuario.class, username);

		return usuario;
	}

	public static Usuario findByUsername(String username) {
		MyEntity.log("username", username);

		Query q = (new Usuario()).getEntityManager()
				.createQuery(" SELECT o FROM Usuario o WHERE " + " LOWER( o.username ) LIKE LOWER( :username ) ");

		username = ("%" + username.replace('*', '%') + "%");
		q.setParameter("username", username);

		return (q.getResultList().size() == 0 ? null : (Usuario) q.getResultList().get(0));
	}

	private static Usuario createMasterData() throws NoSuchAlgorithmException {
		Authority authority = Authority.findAll().get(0);

		Usuario usuario = new Usuario("VITAFARMA", "suporte@vitafarma.com.br", "vitafarma",
				Encryption.toMD5("vitafarma2012"), true, authority);

		usuario.save();

		return usuario;
	}

	@SuppressWarnings("unchecked")
	public static List<Usuario> findAll() throws NoSuchAlgorithmException {
		Query q = (new Usuario()).getEntityManager().createQuery(" SELECT o FROM Usuario o ");

		List<Usuario> result = q.getResultList();

		if (result.size() == 0) {
			Usuario usuario = Usuario.createMasterData();

			result = new ArrayList<Usuario>();

			result.add(usuario);
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Usuario)) {
			return false;
		}

		Usuario other = (Usuario) obj;

		if (this.getUsername() == null) {
			if (other.getUsername() != null) {
				return false;
			}
		} else if (!this.getUsername().equals(other.getUsername())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = (prime * result + ((this.getUsername() == null) ? 0 : this.getUsername().hashCode()));

		return result;
	}

	@Override
	public int compareTo(Usuario o) {
		if (o == null) {
			return -1;
		}

		return this.getNome().compareTo(o.getNome());
	}
}
