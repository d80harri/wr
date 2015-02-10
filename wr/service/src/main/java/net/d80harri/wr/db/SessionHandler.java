package net.d80harri.wr.db;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;
import net.d80harri.wr.db.model.Task;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Table;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;

public class SessionHandler implements Closeable {
	private static final String CHANGELOGFILE = "migrate/_changelog.xml";
	private SessionFactory sessionFactory;
	private Session session = null;

	private SessionHandler(String url) {
		this.setDatabaseUrl(url);
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
	}

	public void setDatabaseUrl(String url) {
		Configuration config = new org.hibernate.cfg.Configuration()
				.setProperty("hibernate.dialect",
						"org.hibernate.dialect.H2Dialect")
				.setProperty("hibernate.connection.driver_class",
						"org.h2.Driver")
				.setProperty("hibernate.connection.url", url)
				.setProperty("hibernate.current_session_context_class",
						"managed").setProperty("hibernate.show_sql", "true")
						.setProperty("hibernate.connection.pool_size", "1");

		Reflections r = new Reflections("net.d80harri");
		for (Class<?> clazz : r.getTypesAnnotatedWith(Table.class)) {
			config.addAnnotatedClass(clazz);
		}

		config.addAnnotatedClass(Task.class);

		final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(config.getProperties()).build();
		sessionFactory = config.buildSessionFactory(serviceRegistry);
	}

	public Session getSession() {
		if (this.session == null || !this.session.isOpen()) {
			this.session = sessionFactory.openSession();
		}
		return this.session;
	}

	public void close() {
		if (this.sessionFactory != null) {
			this.sessionFactory.close();
		}
	}

	private static SessionHandler currentHandler;

	public static void configure(String database) {
		currentHandler = new SessionHandler(database);
		migrate();
	}
	
	private static void migrate() {		
		SessionHandler.getInstance().getSession().doWork(new Work() {
		    public void execute(Connection connection) throws SQLException {
		        migrate(connection);
		    }
		});
	}
	
	private static void migrate(Connection connection) {
        try {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
			
			Liquibase liquibase = new Liquibase(CHANGELOGFILE, new ResourceAccessor() {
				
				public ClassLoader toClassLoader() {
					return this.getClass().getClassLoader();
				}
				
				public Set<String> list(String relativeTo, String path,
						boolean includeFiles, boolean includeDirectories, boolean recursive)
						throws IOException {
					return null;
				}
				
				public Set<InputStream> getResourcesAsStream(String path)
						throws IOException {
					InputStream stream = this.getClass().getResourceAsStream("/" + path);
					Set<InputStream> result = new HashSet<InputStream>();
					result.add(stream);
					return result;
				}
			}, database);
			
			liquibase.update(new Contexts());
		} catch (LiquibaseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static SessionHandler getInstance() {
		return currentHandler;
	}

}
