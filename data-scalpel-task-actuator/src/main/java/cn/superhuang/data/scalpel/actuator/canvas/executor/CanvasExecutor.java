package cn.superhuang.data.scalpel.actuator.canvas.executor;

import cn.superhuang.data.scalpel.actuator.ActuatorContext;
import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.app.task.model.TaskResultSummary;
import cn.superhuang.data.scalpel.model.task.configuration.CanvasTaskConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class CanvasExecutor extends TaskExecutor {

    public CanvasExecutor(ActuatorContext context) {
        super(context);
    }

    public TaskResultSummary execute() throws Exception {
        CanvasTaskConfiguration canvasTaskConfiguration = (CanvasTaskConfiguration) getContext().getTaskConfiguration();
        Canvas canvas = getContext().getObjectMapper().readValue(canvasTaskConfiguration.getCanvas(), Canvas.class);
        canvas.getNodes().forEach(node -> node.setContext(getContext()));

        List<CanvasNode> inputNodes = canvas.getInputNodes();

        for (CanvasNode inputNode : inputNodes) {
            executeNode(inputNode, canvas);
//            List<CanvasNode> nextNodes = canvas.getNextNodes(inputNode);
//            for (CanvasNode nextNode : nextNodes) {
//                executeNode(nextNode, canvas);
//            }
        }

        return getContext().getTaskResultSummary();
    }

    public void executeNode(CanvasNode node, Canvas canvas) {
        if (node.getExecuted() == true) {
            return;
        }
        List<CanvasNode> preNodes = canvas.getPreNodes(node);
        for (CanvasNode preNode : preNodes) {
            executeNode(preNode, canvas);
        }
        Map<String, CanvasTable> preTableMap = preNodes.stream().map(preNode -> preNode.getCanvasData().getTableMap().values()).flatMap(Collection::stream).map(t -> t.clone()).collect(Collectors.toMap(CanvasTable::getName, t -> t));
        if (node.getExecuted() == false) {
            CanvasData canvasData = node.execute(new CanvasData(preTableMap));
            node.setExecuted(true);
            node.setCanvasData(canvasData);
        }
        List<CanvasNode> nextNodes = canvas.getNextNodes(node);
        for (CanvasNode nextNode : nextNodes) {
            executeNode(nextNode, canvas);
        }
    }
}
