package cn.superhuang.data.scalpel.model.enumeration;

/**
 * The ExecutionStatus enumeration.
 */
public enum TaskInstanceExecutionStatus {
    QUEUING,
    RUNNING,
    SUCCESS,
    FAILURE;

    public static Boolean isRunningStatus(TaskInstanceExecutionStatus status) {
        return status == TaskInstanceExecutionStatus.RUNNING || status == TaskInstanceExecutionStatus.QUEUING;
    }
}
