package net.d80harri.wr.ui.viewmodel;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CreatingTasksTest {
	public ApplicationViewModel vm = new ApplicationViewModel();
	
	@Test
	public void test() throws Throwable {
		TaskViewModel newTask = vm.addTaskToSelected();
		Assertions.assertThat(vm.getSelectedTask()).isSameAs(newTask);
	}
	
}
