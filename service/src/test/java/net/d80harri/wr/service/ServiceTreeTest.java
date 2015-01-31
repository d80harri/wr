package net.d80harri.wr.service;

import java.util.Arrays;

import net.d80harri.wr.db.TaskDao;
import net.d80harri.wr.db.model.Task;
import net.d80harri.wr.service.model.TaskDto;

import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

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
		when(dao.getAll()).thenReturn(Arrays.asList(new Task(1, 4), new Task(2, 3)));
		assertThat(service.getAllTrees()).hasSize(1);
		
		when(dao.getAll()).thenReturn(Arrays.asList(new Task(1, 4), new Task(2, 3), new Task(5, 6)));
		assertThat(service.getAllTrees()).hasSize(2);
		
		when(dao.getAll()).thenReturn(Arrays.asList(new Task(1, 2), new Task(3, 4), new Task(5, 6)));
		assertThat(service.getAllTrees()).hasSize(3);
	}
}
