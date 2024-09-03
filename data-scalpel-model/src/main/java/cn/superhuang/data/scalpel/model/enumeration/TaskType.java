package cn.superhuang.data.scalpel.model.enumeration;

import lombok.Getter;

/**
 * The TaskType enumeration.
 */
@Getter
public enum TaskType {
    BATCH_CANVAS("BATCH"),
    BATCH_SPARK_SQL("BATCH"),
    BATCH_NATIVE_SQL("BATCH"),

    STREAM_CANVAS("STREAM"),
    STREAM_SPARK_SQL("STREAM");

    private String category;

    private TaskType(String category) {
        this.category = category;
    }

}
