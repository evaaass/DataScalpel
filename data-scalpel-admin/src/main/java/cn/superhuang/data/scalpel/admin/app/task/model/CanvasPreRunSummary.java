package cn.superhuang.data.scalpel.admin.app.task.model;

import cn.superhuang.data.scalpel.admin.app.task.model.enumeration.CanvasNodeState;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.ErrorDetail;
import cn.superhuang.data.scalpel.model.LogRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class CanvasPreRunSummary {
    private String nodeId;

    private CanvasNodeState state;

    @Schema(title = "画布节点的输入表信息")
    private List<DataTable> inputTables = new ArrayList<>();

    @Schema(title = "画布节点的输出表信息")
    private List<DataTable> outputTables = new ArrayList<>();

    @Schema(title = "state为ERROR时的错误信息")
    private List<ErrorDetail> errors = new ArrayList();

    @Schema(title = "执行日志")
    private List<LogRecord> logs = new ArrayList<>();

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;
}
