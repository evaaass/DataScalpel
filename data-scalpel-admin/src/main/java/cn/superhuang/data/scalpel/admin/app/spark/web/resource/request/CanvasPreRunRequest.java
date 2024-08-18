package cn.superhuang.data.scalpel.admin.app.spark.web.resource.request;

import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.admin.app.spark.model.CanvasPreRunSummary;
import lombok.Data;

import java.util.List;

@Data
public class CanvasPreRunRequest {
    private String canvas;
    private List<CanvasPreRunSummary> inputSummaries;
}
