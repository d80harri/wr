package net.d80harri.wr.db;

import static org.assertj.core.api.Assertions.assertThat;
import net.d80harri.wr.DBUnitTest;
import net.d80harri.wr.db.model.Task;
import net.d80harri.wr.service.WrService;

import org.junit.Test;

public class TreeTest extends DBUnitTest {
	
	private TaskDao dao = new TaskDao();
	
	@Test
	public void readSubtreeAssertCount() {
		assertThat(dao.getSubtree(1)).hasSize(13);
		assertThat(dao.getSubtree(2)).hasSize(6);
		assertThat(dao.getSubtree(3)).hasSize(5);
		assertThat(dao.getSubtree(4)).hasSize(1);
		assertThat(dao.getSubtree(5)).hasSize(3);
		assertThat(dao.getSubtree(6)).hasSize(2);
		assertThat(dao.getSubtree(7)).hasSize(1);
		assertThat(dao.getSubtree(8)).hasSize(1);
		assertThat(dao.getSubtree(9)).hasSize(1);
		assertThat(dao.getSubtree(10)).hasSize(1);
		assertThat(dao.getSubtree(11)).hasSize(1);
		assertThat(dao.getSubtree(12)).hasSize(1);
		assertThat(dao.getSubtree(13)).hasSize(1);
	}
	
	@Test
	public void readPathAssertCount() {
		assertThat(dao.getPath(1)).hasSize(1);
		assertThat(dao.getPath(2)).hasSize(2);
		assertThat(dao.getPath(3)).hasSize(2);
		assertThat(dao.getPath(4)).hasSize(2);
		assertThat(dao.getPath(5)).hasSize(3);
		assertThat(dao.getPath(6)).hasSize(3);
		assertThat(dao.getPath(7)).hasSize(4);
		assertThat(dao.getPath(8)).hasSize(4);
		assertThat(dao.getPath(9)).hasSize(4);
		assertThat(dao.getPath(10)).hasSize(3);
		assertThat(dao.getPath(11)).hasSize(3);
		assertThat(dao.getPath(12)).hasSize(3);
		assertThat(dao.getPath(13)).hasSize(3);
	}
	
	@Test
	public void addTask() {
		long insertedId = dao.addTask(1L, new Task("New task"));
		
		assertThat(dao.getSubtree(1)).hasSize(14);
		assertThat(dao.getPath(1)).hasSize(1);
		
		assertThat(dao.getSubtree(insertedId)).hasSize(1);
		assertThat(dao.getPath(insertedId)).hasSize(2);
	}
	
	@Test
	public void addRootTask() {
		long idOfInserted;
		
		idOfInserted = dao.addTask(null, new Task("New Root task"));
		assertThat(dao.getSubtree(idOfInserted)).hasSize(1);
		readSubtreeAssertCount();
		
		dao.deleteAll();
		idOfInserted = dao.addTask(null, new Task("New Root task"));
		
		assertThat(dao.getSubtree(idOfInserted)).hasSize(1);
	}
	
	@Test
	public void deleteSubtree() {
		dao.deleteSubtree(3L);
		assertThat(dao.getSubtree(1)).hasSize(8);
		
		dao.deleteSubtree(9L);
		assertThat(dao.getSubtree(1)).hasSize(7);
		
		dao.deleteSubtree(1L);
		assertThat(dao.getSubtree(1)).hasSize(0);
	}
	

	public static void main(String[] args) {
		WrService wr = new WrService();
		wr.getAllTrees();
	}
}
