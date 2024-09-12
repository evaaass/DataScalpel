package cn.superhuang.data.scalpel.admin.app.task.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.superhuang.data.scalpel.actuator.ActuatorContext;
import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.admin.app.task.model.CanvasPreRunSummary;
import cn.superhuang.data.scalpel.admin.app.task.model.enumeration.CanvasNodeState;
import cn.superhuang.data.scalpel.admin.util.SchemaUtil;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import cn.superhuang.data.scalpel.model.ErrorDetail;
import cn.superhuang.data.scalpel.model.enumeration.CanvasNodeCategory;
import cn.superhuang.data.scalpel.model.enumeration.LogLevel;
import cn.superhuang.data.scalpel.model.task.SparkConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.CanvasTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.MetadataBuilder;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CanvasPreRunService implements InitializingBean {
    private ActuatorContext actuatorContext;


    public Collection<CanvasPreRunSummary> preRun(Canvas canvas, List<CanvasPreRunSummary> inputSummaries) {
        inputSummaries.forEach(s -> s.setState(CanvasNodeState.OK));
        Map<String, CanvasPreRunSummary> summaryMap = inputSummaries.stream().collect(Collectors.toMap(CanvasPreRunSummary::getNodeId, summary -> summary));

        canvas.getNodes().forEach(node -> {
            node.setContext(actuatorContext);
            if (node.getCategory() == CanvasNodeCategory.INPUT) {
                node.setExecuted(true);
                Map<String, CanvasTable> tableMap = summaryMap.get(node.getId()).getOutputTables().stream().map(dataTable -> {
                    StructType structType = new StructType();
                    for (DataTableColumn column : dataTable.getColumns()) {
                        MetadataBuilder metadataBuilder = new MetadataBuilder();
                        if (column.getPrecision() != null) {
                            metadataBuilder = metadataBuilder.putLong("precision", column.getPrecision());
                        }
                        if (column.getScale() != null) {
                            metadataBuilder = metadataBuilder.putLong("scale", column.getScale());
                        }

                        structType = structType.add(column.getName(), column.getType().getSparkType(), true, metadataBuilder.build());
                    }
                    Dataset<Row> dataset = actuatorContext.getSparkSession().createDataFrame(actuatorContext.getJavaSparkContext().emptyRDD(), structType);
                    CanvasTable canvasData = BeanUtil.copyProperties(dataTable, CanvasTable.class);
                    canvasData.setDataset(dataset);
                    return canvasData;
                }).collect(Collectors.toMap(CanvasTable::getName, CanvasTable -> CanvasTable));
                node.setCanvasData(new CanvasData(tableMap));
            }
        });


        List<CanvasNode> inputNodes = canvas.getInputNodes();
        for (CanvasNode inputNode : inputNodes) {
            List<CanvasNode> nextNodes = canvas.getNextNodes(inputNode);
            for (CanvasNode nextNode : nextNodes) {
                executeNode(nextNode, canvas, summaryMap);
            }
        }
        return summaryMap.values();
    }

    public void executeNode(CanvasNode node, Canvas canvas, Map<String, CanvasPreRunSummary> summaryMap) {
        if (node.getExecuted() == true) {
            return;
        }
        List<CanvasNode> preNodes = canvas.getPreNodes(node);
        for (CanvasNode preNode : preNodes) {
            executeNode(preNode, canvas, summaryMap);
        }
        if (node.getExecuted() == false) {
            CanvasPreRunSummary canvasPreRunSummary = new CanvasPreRunSummary();
            canvasPreRunSummary.setNodeId(node.getId());
            summaryMap.put(node.getId(), canvasPreRunSummary);

            //START 判断上游是否有异常的节点
            Long preNodeErrorCount = preNodes.stream().map(preNode -> summaryMap.get(preNode.getId()).getState()).filter(state -> state == CanvasNodeState.ERROR).count();
            if (preNodeErrorCount > 0) {
                canvasPreRunSummary.setState(CanvasNodeState.ERROR);
                canvasPreRunSummary.getErrors().add(ErrorDetail.builder().target("@line").content("前置节点异常").build());
                return;
            }
            //END 判断上游是否有异常的节点


            //START 判断上游汇集后是否有重名表
            Set<String> duplicatesTableNameSet = getDuplicatesTableNames(preNodes);
            if (duplicatesTableNameSet.size() > 0) {
                canvasPreRunSummary.setState(CanvasNodeState.ERROR);
                canvasPreRunSummary.getErrors().add(ErrorDetail.builder().target("@line").content("输入存在同名表[%s]".formatted(CollectionUtil.join(duplicatesTableNameSet, ","))).build());
                return;
            }
            //END 判断上游汇集后是否有重名表


            //OUTPUT不执行
            if (node.getCategory() != CanvasNodeCategory.OUTPUT) {
                Map<String, CanvasTable> preTableMap = preNodes.stream().map(preNode -> preNode.getCanvasData().getTableMap().values()).flatMap(Collection::stream).map(t -> t.clone()).collect(Collectors.toMap(CanvasTable::getName, t -> t));
                try {
                    canvasPreRunSummary.setState(CanvasNodeState.OK);
                    CanvasData canvasData = node.execute(new CanvasData(preTableMap));
                    node.setCanvasData(canvasData);
                    canvasPreRunSummary.setOutputTables(canvasData.getTableMap().values().stream().map(t -> {
                        DataTable dataTable = BeanUtil.copyProperties(t, DataTable.class);
                        List<DataTableColumn> columns = SchemaUtil.convertToDataTableColumn(t.getDataset().schema());
                        dataTable.setColumns(columns);
                        return dataTable;
                    }).collect(Collectors.toList()));
                } catch (Exception e) {
                    canvasPreRunSummary.setState(CanvasNodeState.ERROR);
                    canvasPreRunSummary.getErrors().add(new ErrorDetail("$", e.getMessage()));
                }
            }

            canvasPreRunSummary.setInputTables(preNodes.stream().map(preNode -> preNode.getCanvasData().getTableMap().values()).flatMap(Collection::stream).map(t ->
                    {
                        DataTable dataTable = BeanUtil.copyProperties(t, DataTable.class);
                        List<DataTableColumn> columns = SchemaUtil.convertToDataTableColumn(t.getDataset().schema());
                        dataTable.setColumns(columns);
                        return dataTable;
                    }
            ).collect(Collectors.toList()));
            node.setExecuted(true);
        }
        List<CanvasNode> nextNodes = canvas.getNextNodes(node);
        for (CanvasNode nextNode : nextNodes) {
            executeNode(nextNode, canvas, summaryMap);
        }
    }

    private Set<String> getDuplicatesTableNames(List<CanvasNode> preNodes) {
        Map<String, Integer> tableNameCountMap = new HashMap<>();
        preNodes.forEach(canvasNode -> {
//            Set<String> nodeTableNameSet = summaryMap.get(canvasNode.getId()).getOutputTables().stream().map(t -> t.getName()).collect(Collectors.toSet());
            Set<String> nodeTableNameSet = canvasNode.getCanvasData().getTableMap().keySet();
            nodeTableNameSet.forEach(tableName -> {
                if (tableNameCountMap.containsKey(tableName)) {
                    tableNameCountMap.put(tableName, tableNameCountMap.get(tableName) + 1);
                } else {
                    tableNameCountMap.put(tableName, 1);
                }
            });
        });
        Set<String> duplicatesTableNames = new HashSet<>();
        tableNameCountMap.forEach((k, v) -> {
            if (v > 1) {
                duplicatesTableNames.add(k);
            }
        });
        return duplicatesTableNames;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        SparkSession sparkSession = SparkSession.builder().master("local")
//                .config("spark.sql.codegen.wholeStage", "false")
//                .config("spark.sql.crossJoin.enabled", "true")
//                .config("spark.driver.maxResultSize", "4g")
//                .config("spark.ui.enabled", "false")
//                .getOrCreate();
//        System.out.println(sparkSession);


        SparkConfiguration sparkConfiguration = new SparkConfiguration();
        sparkConfiguration.setMaster("local");
        sparkConfiguration.setLogLevel(LogLevel.INFO);
        sparkConfiguration.setConfigs(new HashMap<>());

        TaskConfiguration taskConfiguration = new CanvasTaskConfiguration();
        taskConfiguration.setSparkConfiguration(sparkConfiguration);

        this.actuatorContext = ActuatorContext.getOrCreate(taskConfiguration);
    }
}
