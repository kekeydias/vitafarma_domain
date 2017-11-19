package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "AUTHORITIES")
public class Authority extends MyEntity implements Serializable {
	private static final long serialVersionUID = 8739246006672184100L;

	public Authority() {
		this(null, null);
	}

	public Authority(String username, String authority) {
		this.setUsername(username);
		this.setAuthority(authority);
	}

	private void copyProperties(Authority authority) {
		this.setAuthority(authority.getAuthority());
	}

	@Id
	@NotNull
	@Column(name = "USERNAME")
	@Size(min = 1, max = 50)
	private String username;

	@Column(name = "AUTHORITY")
	private String authority;

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
	}

	public void remove() {
		if (this.getEntityManager().contains(this)) {
			this.getEntityManager().getTransaction().begin();
			this.getEntityManager().remove(this);
			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		} else {
			Authority authority = this.getEntityManager().find(Authority.class, this.getUsername());

			if (authority != null) {
				this.getEntityManager().getTransaction().begin();
				this.getEntityManager().remove(authority);
				this.getEntityManager().flush();
				this.getEntityManager().getTransaction().commit();
			}
		}
	}

	@Override
	protected Authority merge() {
		Authority authority = this.getEntityManager().find(Authority.class, this.getUsername());

		if (authority != null) {
			this.getEntityManager().getTransaction().begin();

			authority = this.getEntityManager().merge(this);
			authority.copyProperties(this);

			this.getEntityManager().flush();
			this.getEntityManager().getTransaction().commit();
		}

		return authority;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Username: ").append(this.getUsername()).append(", ");
		sb.append("Authority: ").append(this.getAuthority()).append(", ");

		return sb.toString();
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAuthority() {
		return this.authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public static Authority find(String username) {
		MyEntity.log("username", username);

		if (username == null) {
			return null;
		}

		return (new Authority()).getEntityManager().find(Authority.class, username);
	}

	public static Authority findByUsername(String username) {
		MyEntity.log("username", username);

		Query q = (new Authority()).getEntityManager()
				.createQuery(" SELECT o FROM Authority o WHERE " + " LOWER(o.username ) LIKE LOWER( :username ) ");

		username = ("%" + username.replace('*', '%') + "%");
		q.setParameter("username", username);

		return (q.getResultList().isEmpty() ? null : (Authority) q.getResultList().get(0));
	}

	private static Authority createMasterData() {
		Authority authority = new Authority("admin", "ROLE_ADMIN");

		authority.persist();

		return authority;
	}

	@SuppressWarnings("unchecked")
	public static List<Authority> findAll() {
		Query q = (new Authority()).getEntityManager().createQuery(" SELECT o FROM Authority o ");

		List<Authority> result = q.getResultList();

		if (result.isEmpty()) {
			Authority authority = Authority.createMasterData();

			result = new ArrayList<Authority>();
			result.add(authority);
		}

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = (prime * result + ((this.getUsername() == null) ? 0 : this.getUsername().hashCode()));

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Authority)) {
			return false;
		}

		Authority other = (Authority) obj;

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
	protected Long getId() {
		return 0L;
	}
}
