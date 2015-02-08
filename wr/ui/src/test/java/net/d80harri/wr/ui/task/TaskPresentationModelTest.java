package net.d80harri.wr.ui.task;

import net.d80harri.wr.service.model.TaskDto;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TaskPresentationModelTest {
	
	@Test
	public void testParentChildRelation() throws Throwable {
		TaskPresentationModel root = new TaskPresentationModel(new TaskDto("root"));
		root.getChildren().add(new TaskPresentationModel(new TaskDto("child")));
		
		Assertions.assertThat(root.getChildren()).hasSize(1);
		Assertions.assertThat(root.getChildren().get(0).getParent()).isSameAs(root);
	}
	
	@Test
	public void testParentChildRelation2() throws Throwable {
		TaskDto root = new TaskDto("root");
		TaskDto sub = new TaskDto("sub");
		root.addChild(sub);
		
		TaskPresentationModel tpm = new TaskPresentationModel(root);
		
		Assertions.assertThat(tpm.getChildren()).hasSize(1);
	}
	
	@Test
	public void indent() throws Throwable {
		TaskPresentationModel root = new TaskPresentationModel(new TaskDto("root"));
		TaskPresentationModel sub = new TaskPresentationModel(new TaskDto("sub"));
		
		Assertions.assertThat(root.getParent()).isNull();
		Assertions.assertThat(sub.getParent()).isNull();
		Assertions.assertThat(root.getChildren()).isEmpty();
		Assertions.assertThat(sub.getChildren()).isEmpty();
		
		root.getChildren().add(sub);
		
		Assertions.assertThat(root.getParent()).isNull();
		Assertions.assertThat(sub.getParent()).isSameAs(root);
		Assertions.assertThat(root.getChildren()).contains(sub);
		Assertions.assertThat(root.getChildren()).hasSize(1);
	}
}
