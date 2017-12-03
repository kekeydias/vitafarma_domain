package br.com.vitafarma.domain;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import junit.framework.TestCase;

public abstract class MyEntity extends TestCase implements Serializable {
	private static final long serialVersionUID = -3406219789412053319L;

	private static Logger LOGGER = null;

	enum LogType {
		INFO, DEBUG, WARNING, ERROR;

		public Level getLevel() {
			switch (this) {
			case INFO:
				return Level.INFO;
			case DEBUG:
				return Level.FINE;
			case WARNING:
				return Level.WARNING;
			case ERROR:
				return Level.SEVERE;
			default:
				break;
			}

			return Level.INFO;
		}

		public boolean isError() {
			return (this == ERROR);
		}
	}

	static {
		try {
			MyEntity.LOGGER = Logger.getLogger(MyEntity.class.getName());
		} catch (Exception ex) {
			ex.printStackTrace();

			if (ex.getCause() != null) {
				ex.getCause().printStackTrace();
			}

			System.err.println("ERRO AO INICIAR OBJETO LOGGER. PASSAMOS A UTILIZAR SAIDA PADRAO DE LOG.");
			System.err.println(ex);

			System.err.println("Cause: " + ex.getCause());
			System.err.println("Message: " + ex.getMessage());
			System.err.println("Localized Message: " + ex.getLocalizedMessage());

			MyEntity.LOGGER = null;
		}
	}

	@PersistenceContext
	private static EntityManager em = null;

	private static EntityManagerFactory emf = null;

	public EntityManager getEntityManager() {
		if (MyEntity.em == null) {
			MyEntity.emf = Persistence.createEntityManagerFactory("persistenceProjectDB");

			try {
				MyEntity.em = MyEntity.emf.createEntityManager();
			} catch (Exception ex) {
				ex.printStackTrace();

				if (ex.getCause() != null) {
					ex.getCause().printStackTrace();
				}

				System.out.println("\nErro: " + ex.getMessage() + '\n');
				System.out.println(ex);
				System.err.println("\nErro: " + ex.getMessage() + '\n');
				System.err.println(ex);

				RuntimeException re = new RuntimeException("erro ao carregar persistenceProjectDB: " + ex.getMessage());
				re.initCause(ex);
				throw re;
			}
		}

		return MyEntity.em;
	}

	public static void log(Object value) {
		MyEntity.log(LogType.INFO, value);
	}

	protected static void log(LogType type, Object value) {
		if (MyEntity.LOGGER == null) {
			System.out.println(value);
		} else {
			LogRecord lr = new LogRecord(type.getLevel(), (value == null ? "<null>" : value.toString()));

			MyEntity.LOGGER.log(lr);
		}

		if (type.isError()) {
			System.err.println(new Throwable("Erro de Persistencia").getStackTrace());
		}
	}

	protected static void log(LogType type, String atributte, Object value) {
		String log = ("atributte --> " + atributte + "; " + "value --> " + value);

		MyEntity.log(type, log);
	}

	protected static void log(String atributte, Object value) {
		String log = ("atributte --> " + atributte + "; " + "value --> " + value);

		MyEntity.log(LogType.INFO, log);
	}

	public final void save() {
		if (this.getId() == null) {
			this.persist();
		} else {
			this.merge();
		}
	}

	protected abstract Long getId();

	protected abstract void persist();

	protected abstract MyEntity merge();

	@Override
	public int hashCode() {
		if (this.getId() == null) {
			return super.hashCode();
		}

		return this.getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj.getClass().equals(this.getClass()))) {
			return false;
		}

		MyEntity other = (MyEntity) obj;

		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else {
			if (!this.getId().equals(other.getId())) {
				return false;
			}
		}

		return true;
	}
}
