package net.d80harri.wr.db;

import java.util.List;

import net.d80harri.wr.db.model.Task;

import org.hibernate.Session;

public class TaskDao {

	public long addTask(Long parentId, Task task) {
		Session session = SessionHandler.getInstance().getSession();
		long myRight;
		if (parentId == null) {
			Long max = (Long) session.createQuery("select max(t.right) from Task t")
					.list().iterator().next();
			if (max == null) max = 0L;
			myRight = max+1;
		} else {
			Task parent = (Task) session.get(Task.class, parentId);
			myRight = parent.getRight();
		}

		session.createQuery(
				"update Task t set t.right = t.right+2 where t.right >= :parentRight")
				.setParameter("parentRight", myRight).executeUpdate();
		session.createQuery(
				"update Task t set t.left = t.left+2 where t.left > :parentLeft")
				.setParameter("parentLeft", myRight).executeUpdate();

		task.setLeft(myRight);
		task.setRight(myRight+1);

		session.save(task);
		return task.getId();
	}

	public List<Task> getSubtree(long id) {
		Session session = SessionHandler.getInstance().getSession();

		return session
				.createQuery(
						"select t from Task t, Task parent where t.left BETWEEN parent.left and parent.right and parent.id = :id order by t.left)")
				.setParameter("id", id).list();
	}

	public List<Task> getPath(long id) {
		Session session = SessionHandler.getInstance().getSession();
		return session
				.createQuery(
						"select parent from Task as node, Task as parent where node.left BETWEEN parent.left AND parent.right AND node.id = :id order by parent.left")
						.setParameter("id", id)
						.list();
	}

	public void deleteSubtree(long id) {
		Session session = SessionHandler.getInstance().getSession();
		Task task = getTask(id);

		if (task != null) {
			session.createQuery(
					"DELETE FROM Task t where t.left between :left and :right")
					.setParameter("left", task.getLeft())
					.setParameter("right", task.getRight()).executeUpdate();

			session.createQuery(
					"update Task t set t.right = t.right-:width where t.right >= :right")
					.setParameter("right", task.getRight())
					.setParameter("width", task.getRight() - task.getLeft())
					.executeUpdate();

			session.createQuery(
					"update Task t set t.left = t.left-:width where t.left > :right")
					.setParameter("right", task.getRight())
					.setParameter("width", task.getRight() - task.getLeft())
					.executeUpdate();
		}
	}

	private Task getTask(long id) {
		Session session = SessionHandler.getInstance().getSession();

		return (Task) session.get(Task.class, id);
	}

	public List<Task> getAll() {
		Session session = SessionHandler.getInstance().getSession();
		return session.createQuery("from Task").list();
	}

	public void deleteAll() {
		Session session = SessionHandler.getInstance().getSession();
		session.createQuery("delete from Task").executeUpdate();
	}

	public void updateTask(Task task) {
		Session session = SessionHandler.getInstance().getSession();
		session.update(task);
	}

	public Task getTaskById(long id) {
		Session session = SessionHandler.getInstance().getSession();
		
		return (Task) session.get(Task.class, id);
	}
}
