package cn.superhuang.data.scalpel.admin.app.task.web.resource.request;

import cn.superhuang.data.scalpel.admin.app.task.model.CanvasPreRunSummary;
import lombok.Data;

import java.util.List;

@Data
public class CanvasPreRunRequest {
    private String canvas;
    private List<CanvasPreRunSummary> inputSummaries;
}
