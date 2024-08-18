package cn.superhuang.data.scalpel.admin.app.spark.web.resource;

import cn.superhuang.data.scalpel.admin.app.spark.model.CanvasPreRunSummary;
import cn.superhuang.data.scalpel.admin.app.spark.web.resource.request.CanvasPreRunRequest;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Tag(name = "画布模块")
@RequestMapping("/api/v1")
public interface ICanvasResource {
    @Operation(summary = "画布任务预运行")
    @PostMapping("/canvas/actions/pre-run")
    GenericResponse<Collection<CanvasPreRunSummary>> preRun(@RequestBody CanvasPreRunRequest preRunRequest) throws Exception;
}
