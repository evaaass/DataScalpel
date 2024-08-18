package cn.superhuang.data.scalpel.admin.app.spark.web.resource.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.admin.app.spark.model.CanvasPreRunSummary;
import cn.superhuang.data.scalpel.admin.app.spark.service.CanvasPreRunService;
import cn.superhuang.data.scalpel.admin.app.spark.web.resource.ICanvasResource;
import cn.superhuang.data.scalpel.admin.app.spark.web.resource.request.CanvasPreRunRequest;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CanvasResource implements ICanvasResource {
    @Resource
    private CanvasPreRunService preRunService;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public GenericResponse<Collection<CanvasPreRunSummary>> preRun(CanvasPreRunRequest preRunRequest) throws Exception {
        Collection<CanvasPreRunSummary> result = preRunService.preRun(objectMapper.readValue(preRunRequest.getCanvas(), Canvas.class), preRunRequest.getInputSummaries());
        return GenericResponse.ok(result);
    }




}
