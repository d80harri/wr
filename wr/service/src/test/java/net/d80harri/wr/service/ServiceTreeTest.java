package net.d80harri.wr.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import net.d80harri.wr.db.TaskDao;
import net.d80harri.wr.db.model.Task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServiceTreeTest {
	
	@Mock
	private TaskDao dao;
	private WrService service;
	
	@Before public void initMocks() {
		TransactionAspect.isActive = false;
        MockitoAnnotations.initMocks(this);
        service = new WrService(dao);
    }

	
	@Test
	public void testRetrieveTree() throws Throwable {
		when(dao.getAll()).thenReturn(Arrays.asList(new Task("1", 1, 4), new Task("1.1", 2, 3)));
		assertThat(service.getAllTrees()).hasSize(1);
		assertThat(service.getAllTrees().get(0).getTitle()).isEqualTo("1");
		assertThat(service.getAllTrees().get(0).getChildren()).hasSize(1);
		assertThat(service.getAllTrees().get(0).getChildren().get(0).getTitle()).isEqualTo("1.1");
		
		when(dao.getAll()).thenReturn(Arrays.asList(new Task("1", 1, 4), new Task("1.1", 2, 3), new Task("2", 5, 6)));
		assertThat(service.getAllTrees()).hasSize(2);
		
		when(dao.getAll()).thenReturn(Arrays.asList(new Task("1", 1, 2), new Task("2", 3, 4), new Task("3", 5, 6)));
		assertThat(service.getAllTrees()).hasSize(3);
		assertThat(service.getAllTrees().get(0).getTitle()).isEqualTo("1");
		assertThat(service.getAllTrees().get(1).getTitle()).isEqualTo("2");
		assertThat(service.getAllTrees().get(2).getTitle()).isEqualTo("3");
	}


	@Test
	public void tempTest() {
		when(dao.getAll()).thenReturn(Arrays.asList(new Task("1", 1, 2), new Task("2", 3, 4), new Task("3", 5, 6)));
		assertThat(service.getAllTrees()).hasSize(3);
		assertThat(service.getAllTrees().get(0).getTitle()).isEqualTo("1");
		assertThat(service.getAllTrees().get(1).getTitle()).isEqualTo("2");
		assertThat(service.getAllTrees().get(2).getTitle()).isEqualTo("3");
	}
}
