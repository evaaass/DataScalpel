package cn.superhuang.data.scalpel.app.task.model;

import cn.superhuang.data.scalpel.model.GenericResult;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

public abstract class  TaskResultSummary extends GenericResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 4923995032112039654L;
}
