package net.d80harri.wr.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import net.d80harri.wr.db.TaskDao;
import net.d80harri.wr.db.model.Task;
import net.d80harri.wr.service.model.TaskDto;

public class WrService {
	private MapperFactory mapperFactory = new DefaultMapperFactory.Builder()
			.build();
	private MapperFacade mapper = mapperFactory.getMapperFacade();
	private TaskDao dal = new TaskDao();

	public List<TaskDto> getPath(long id) {
		List<Task> task = dal.getPath(id);
		return mapper.mapAsList(task, TaskDto.class);
	}

	@Transactional
	public TaskDto getSubtree(long id) {
		List<TaskDto> trees = toTree(dal.getSubtree(id));
		if (trees.size() == 0)
			return null;
		else if (trees.size() == 1)
			return trees.get(0);
		else
			throw new RuntimeException();
	}
	
	@Transactional
	public List<TaskDto> getAllTrees() {
		return toTree(dal.getAll());
	}

	private List<TaskDto> toTree(List<Task> taskList) {
		List<TaskDto> result = new ArrayList<TaskDto>();

		if (taskList.size() != 0) {

			Task[] tasks = taskList.toArray(new Task[taskList.size()]);
			Arrays.sort(tasks, new Comparator<Task>() {
				public int compare(Task o1, Task o2) {
					return Long.compare(o1.getLeft(), o2.getLeft());
				}
			});

			Map<Task, TaskDto> dtos = new HashMap<Task, TaskDto>();
			dtos.put(tasks[0], mapper.map(tasks[0], TaskDto.class));

			Stack<Task> stack = new Stack<Task>();
			stack.push(tasks[0]);
			result.add(dtos.get(tasks[0]));

			for (int i = 1; i < tasks.length; i++) {
				Task n = tasks[i];
				stack.push(n);
				dtos.put(n, mapper.map(n, TaskDto.class));
				while (stack.peek().getRight() < n.getLeft()) {
					Task t = stack.pop();
					dtos.get(stack.peek()).addChild(dtos.get(t));
					if (stack.isEmpty())
						result.add(dtos.get(stack.peek()));
				}

			}
		}
		return result;
	}

	@Transactional
	public long storeSubtree(Long parentId, TaskDto subtree) {
		Task task = mapper.map(subtree, Task.class);
		dal.addTask(parentId, task);
		subtree.setId(task.getId());

		for (TaskDto child : subtree.getChildren()) {
			storeSubtree(task.getId(), child);
		}
		return task.getId();
	}

	@Transactional
	public void deleteSubtree(long id) {
		dal.deleteSubtree(id);
	}

	@Transactional
	public void updateTask(TaskDto dirty) {
		Task task = dal.getTaskById(dirty.getId());
		mapper.map(dirty, task);
		dal.updateTask(task);
	}
	
	public TaskDto getTaskById(long id) {
		Task task = dal.getTaskById(id);
		return mapper.map(task, TaskDto.class);
	}
}
