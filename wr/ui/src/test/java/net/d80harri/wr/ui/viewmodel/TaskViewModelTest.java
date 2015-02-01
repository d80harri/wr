package net.d80harri.wr.ui.viewmodel;

import net.d80harri.wr.service.model.TaskDto;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

public class TaskViewModelTest {

	@Test
	public void test1() throws Throwable {
		TaskDto root = new TaskDto("root");
		root.addChild(new TaskDto("firstChild"));
		root.addChild(new TaskDto("secondChild"));
		
		TaskViewModel model = new TaskViewModel(root, null, true);
		
		assertThat(model.getChildrenViews()).hasSize(2);
		
		model.addNewChild();
		assertThat(model.getChildrenViews()).hasSize(3);
	}
}
