package cn.superhuang.data.scalpel.actuator.canvas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static cn.superhuang.data.scalpel.model.enumeration.CanvasNodeCategory.*;


/**
 * @Author: SuperHuang
 * @Description:
 * @Date: 2021/6/8
 * @Version: 1.0
 */
@Data
public class Canvas implements Serializable {
    @Serial
    private static final long serialVersionUID = 6651009075808717307L;
    private List<CanvasNode> nodes;
    private List<CanvasLine> lines;

    @JsonIgnore
    private Map<String, CanvasNode> getNodeMap() {
        return nodes.stream().collect(Collectors.toMap(CanvasNode::getId, n -> n));
    }

    @JsonIgnore
    public List<CanvasNode> getInputNodes() {
        return getNodes().stream().filter(node -> node.getCategory() == INPUT).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<CanvasNode> getOutputNodes() {
        return getNodes().stream().filter(node -> node.getCategory() == OUTPUT).collect(Collectors.toList());
    }

    @JsonIgnore

    public List<CanvasNode> getPreNodes(CanvasNode node) {
        return getLines().stream().filter(line -> line.getTo().equals(node.getId())).map(line -> getNodeMap().get(line.getFrom())).collect(Collectors.toList());
    }

    @JsonIgnore

    public List<CanvasNode> getNextNodes(CanvasNode node) {

        return getLines().stream().filter(line -> line.getFrom().equals(node.getId())).map(line -> getNodeMap().get(line.getTo())).collect(Collectors.toList());
    }

    @JsonIgnore
    public Set<CanvasNode> getNodesWithoutLines() {
        Map<String, CanvasNode> nodeMap = nodes.stream().collect(Collectors.toMap(CanvasNode::getId, node -> node));
        Set<CanvasNode> connectedNodes = new HashSet<>();
        for (CanvasLine line : lines) {
            connectedNodes.add(nodeMap.get(line.getFrom()));
            connectedNodes.add(nodeMap.get(line.getTo()));
        }
        Set<CanvasNode> nodesWithoutLines = new HashSet<>();
        for (CanvasNode node : nodes) {
            if (!connectedNodes.contains(node)) {
                nodesWithoutLines.add(node);
            }
        }
        return nodesWithoutLines;

    }

    @JsonIgnore
    public Set<CanvasNode> getInputNodeWithInput() {
        Set<CanvasNode> errorNodes = nodes.stream().filter(node -> node.getCategory() == INPUT).filter(node -> lines.stream().filter(l -> l.getTo().equals(node.getId())).count() > 0).collect(Collectors.toSet());
        return errorNodes;
    }

    @JsonIgnore
    public Set<CanvasNode> getOutputNodeWithOutput() {
        Set<CanvasNode> errorNodes = nodes.stream().filter(node -> node.getCategory() == OUTPUT).filter(node -> lines.stream().filter(l -> l.getFrom().equals(node.getId())).count() > 0).collect(Collectors.toSet());
        return errorNodes;
    }

    @JsonIgnore
    public Set<CanvasNode> getErrorLineProcess() {
        Map<String, CanvasNode> nodeMap = nodes.stream().collect(Collectors.toMap(CanvasNode::getId, node -> node));
        Set<CanvasNode> errorNodes = nodes.stream().filter(node -> node.getCategory() == PROCESSOR).filter(node -> {
            Long fromLineCount = lines.stream().filter(l -> l.getFrom().equals(node.getId())).count();
            Long toLineCount = lines.stream().filter(l -> l.getFrom().equals(node.getId())).count();
            if (fromLineCount == 0 || toLineCount == 0) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toSet());
        return errorNodes;
    }

    @JsonIgnore
    public Boolean isDAG() {
        Map<String, CanvasNode> nodeMap = nodes.stream().collect(Collectors.toMap(CanvasNode::getId, node -> node));

        // 构建节点的入度映射
        Map<CanvasNode, Integer> inDegreeMap = new HashMap<>();
        for (CanvasNode node : nodes) {
            inDegreeMap.put(node, 0);
        }
        for (CanvasLine line : lines) {
            CanvasNode toNode = nodeMap.get(line.getTo());
            inDegreeMap.put(toNode, inDegreeMap.getOrDefault(toNode, 0) + 1);
        }

        // 使用拓扑排序检测是否存在环
        Queue<CanvasNode> queue = new LinkedList<>();
        for (CanvasNode node : nodes) {
            if (inDegreeMap.get(node) == 0) {
                queue.offer(node);
            }
        }

        int visitedCount = 0;
        while (!queue.isEmpty()) {
            CanvasNode node = queue.poll();
            visitedCount++;

            for (CanvasLine line : lines) {
                if (line.getFrom().equals(node.getId())) {
                    CanvasNode toNode = nodeMap.get(line.getTo());
                    inDegreeMap.put(toNode, inDegreeMap.get(toNode) - 1);
                    if (inDegreeMap.get(toNode) == 0) {
                        queue.offer(toNode);
                    }
                }
            }
        }

        return visitedCount == nodes.size();
    }


//    public CanvasValidateResult validateCanvas() {
//        Boolean isDag = isDAG();
//        Set<CanvasNode> nodeWithoutLineSet = getNodesWithoutLines();
//        Set<CanvasNode> inputNodeWithInputSet = getInputNodeWithInput();
//        Set<CanvasNode> outputNodeWithOutputSet = getOutputNodeWithOutput();
//        Set<CanvasNode> errorLineProcessNodeSet = getErrorLineProcess();
//        Map<String, ValidateResult> nodeValidateResultMap = nodes.stream().map(node -> node.validate(null)).collect(Collectors.toMap(ValidateResult::getNodeId, r -> r));
//        nodeWithoutLineSet.forEach(node -> {
//            nodeValidateResultMap.get(node.getId()).addError("line", "节点不能游离");
//        });
//        inputNodeWithInputSet.forEach(node -> {
//            nodeValidateResultMap.get(node.getId()).addError("line", "输入节点不能有上游");
//        });
//        outputNodeWithOutputSet.forEach(node -> {
//            nodeValidateResultMap.get(node.getId()).addError("line", "输出节点不能有下游");
//        });
//        errorLineProcessNodeSet.forEach(node -> {
//            nodeValidateResultMap.get(node.getId()).addError("line", "处理器节点必须配置上游和下游");
//        });
//
//        CanvasValidateResult canvasValidateResult = new CanvasValidateResult();
//        canvasValidateResult.setIsDag(isDag);
//        canvasValidateResult.setErrorNodes(new HashSet<>(nodeValidateResultMap.values()));
//
//        Long errorCount = canvasValidateResult.getErrorNodes().stream().filter(result -> result.getValid()==false).count();
//        if (errorCount > 0 || !isDag) {
//            canvasValidateResult.setValid(false);
//        } else {
//            canvasValidateResult.setValid(true);
//        }
//        return canvasValidateResult;
//    }
}