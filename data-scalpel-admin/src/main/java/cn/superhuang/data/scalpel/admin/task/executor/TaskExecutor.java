package cn.superhuang.data.scalpel.admin.task.executor;

public interface TaskExecutor {

    public String submitTask();

    public void getTaskStatus(String taskId);
}
