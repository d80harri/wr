package net.d80harri.wr;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.d80harri.wr.db.SessionHandler;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;

public abstract class DBUnitTest {
	private static final String DB_URL = "jdbc:h2:~/wr;AUTO_SERVER=true";
	
	private final Connection connection;
	
	public DBUnitTest() {
		SessionHandler.configure(DB_URL);
		SessionHandler.getInstance().getSession().close();
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		try {
			this.connection = DriverManager.getConnection(
					DB_URL, "", "");
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Before
	public void setup() throws Exception {
		handleSetUpOperation();
		SessionHandler.getInstance().getSession().beginTransaction();
	}
	
	@After
	public void teardown() {
		SessionHandler.getInstance().getSession().getTransaction().commit();
	}
	
	private void handleSetUpOperation() throws Exception {
		final IDatabaseConnection conn = getConnection();
		final IDataSet data = getDataSet();
		try {
			DatabaseOperation.CLEAN_INSERT.execute(conn, data);
		} finally {
			conn.close();
		}
	}

	private IDataSet getDataSet() throws IOException, DataSetException {
		return new FlatXmlDataSetBuilder().build(this.getClass().getResource(
				this.getClass().getSimpleName() + ".xml"));
	}

	private IDatabaseConnection getConnection()
			throws ClassNotFoundException, SQLException, DatabaseUnitException {
		Class.forName("org.h2.Driver");
		return new DatabaseConnection(connection);
	}

}
