package cn.superhuang.data.scalpel.actuator.canvas.node.processor;

import cn.superhuang.data.scalpel.actuator.ActuatorException;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasData;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasTable;
import cn.superhuang.data.scalpel.actuator.canvas.node.CanvasNode;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.JoinConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.JoinAction;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.JoinCondition;
import lombok.Data;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class JoinProcessor extends CanvasNode {
    private JoinConfiguration configuration;

    @Override
    public CanvasData execute(CanvasData canvasData) {
        if (configuration == null || configuration.getActions() == null) {
            return canvasData;
        }
        for (JoinAction action : configuration.getActions()) {

            if (!canvasData.getTableMap().containsKey(action.getLeftTable())) {
                throw new ActuatorException("Join失败：左表不存在");
            }
            if (!canvasData.getTableMap().containsKey(action.getRightTable())) {
                throw new ActuatorException("Join失败：右表不存在");
            }

            CanvasTable leftTable = canvasData.getTableMap().get(action.getLeftTable());
            CanvasTable rightTable = canvasData.getTableMap().get(action.getRightTable());
            Dataset<Row> leftDs = leftTable.getDataset();
            Dataset<Row> rightDs = rightTable.getDataset();


            List<String> leftColumnNameList = Arrays.asList(leftDs.columns());
            List<String> rightColumnNameList = Arrays.asList(rightDs.columns());

            List<String> sameColumnNameList = new ArrayList<>();
            for (String leftColumnName : leftColumnNameList) {
                if (rightColumnNameList.contains(leftColumnName)) {
                    sameColumnNameList.add(leftColumnName);
                }
            }

            Dataset<Row> newLeftDs = leftDs.select(getNewDsColumns(leftTable.getName(), leftDs, leftColumnNameList, sameColumnNameList));
            Dataset<Row> newRightDs = rightDs.select(getNewDsColumns(rightTable.getName(), rightDs, rightColumnNameList, sameColumnNameList));


            Column joinColExpr = null;
            for (JoinCondition condition : action.getConditions()) {
                Column joinConditionColumn = newLeftDs.col(getRenameColName(leftTable.getName(), condition.getLeftTableField(), sameColumnNameList))
                        .equalTo(newRightDs.col(getRenameColName(rightTable.getName(), condition.getRightTableField(), sameColumnNameList)));
                if (joinColExpr == null) {
                    joinColExpr = joinConditionColumn;
                } else {
                    joinColExpr = joinColExpr.and(joinConditionColumn);
                }
            }

            Dataset<Row> newDs = newLeftDs.join(newRightDs, joinColExpr, action.getJoinType().name());

            CanvasTable joinedTable = new CanvasTable();
            joinedTable.setDataset(newDs);
            joinedTable.setName(action.getNewTable());
            joinedTable.setMetadata(leftTable.getMetadata());

            canvasData.getTableMap().put(joinedTable.getName(), joinedTable);
        }
        return canvasData;
    }

    public Column[] getNewDsColumns(String tableName, Dataset<Row> dataset, List<String> columnNameList,
                                    List<String> sameColumnNameList) {
        List<Column> columns = new ArrayList<>();
        for (String columnName : columnNameList) {
            Column column = dataset.col(columnName);
            if (sameColumnNameList.contains(columnName)) {
                column = column.as(tableName + "_" + columnName);
            }
            columns.add(column);
        }
        return columns.toArray(new Column[0]);
    }

    public static String getRenameColName(String tableName, String colName, List<String> sameColumnNameList) {
        if (sameColumnNameList.contains(colName)) {
            return tableName + "_" + colName;
        }
        return colName;
    }

//    @Override
//    public ConfigurationValidateResult validate(List<CanvasTable> inputTables) {
//        ConfigurationValidateResult validateResult = super.validate(inputTables);
//
////        if (configuration == null || configuration.getActions() == null || configuration.getActions().size() == 0) {
////            validateResult.addError("$.configuration", "配置不能为空");
////            return validateResult;
////        }
//
//        for (int i = 0; i < configuration.getActions().size(); i++) {
//            TableJoinAction action = configuration.getActions().get(i);
//            if (StrUtil.isBlank(action.getTableLeft())) {
//                validateResult.addError("$.configuration.actions[" + i + "].tableLeft", "左表不能为空");
//            }
//            if (StrUtil.isBlank(action.getTableRight())) {
//                validateResult.addError("$.configuration.actions[" + i + "].tableRight", "右表不能为空");
//            }
//            if (action.getJoinType() == null) {
//                validateResult.addError("$.configuration.actions[" + i + "].joinType", "join类型不能为空");
//            }
//            if (CollectionUtil.isEmpty(action.getConditions())) {
//                validateResult.addError("$.configuration.actions[" + i + "].conditions", "join条件不能为空");
//            }
//            if (inputTables != null && StrUtil.isNotBlank(action.getTableLeft())) {
//                int finalI = i;
//                inputTables.stream().filter(inputTable -> inputTable.getId().equals(action.getTableLeft())).findAny().ifPresentOrElse(t -> {
//                    t.getColumns().stream().filter(c -> c.getName().equals(action.getTableLeft())).findAny().ifPresentOrElse(c -> {
//                    }, () -> {
//                        if (CollectionUtil.isNotEmpty(action.getConditions())) {
//                            Set<String> fieldSet = t.getColumns().stream().map(c -> c.getName()).collect(Collectors.toSet());
//                            action.getConditions().stream().map(c -> c.getLeftField()).forEach(leftField -> {
//                                if (!fieldSet.contains(leftField)) {
//                                    validateResult.addError("$.configuration.actions[" + finalI + "].conditions[*].leftField", "字段" + leftField + "在表" + action.getTableLeft() + "不存在");
//                                }
//                            });
//                        }
//                    });
//                }, () -> validateResult.addError("$.configuration.actions[" + finalI + "].table", "指定的表" + action.getTableLeft() + "不存在"));
//
//                inputTables.stream().filter(inputTable -> inputTable.getId().equals(action.getTableRight())).findAny().ifPresentOrElse(t -> {
//                    t.getColumns().stream().filter(c -> c.getName().equals(action.getTableRight())).findAny().ifPresentOrElse(c -> {
//                    }, () -> {
//                        if (CollectionUtil.isNotEmpty(action.getConditions())) {
//                            Set<String> fieldSet = t.getColumns().stream().map(c -> c.getName()).collect(Collectors.toSet());
//                            action.getConditions().stream().map(c -> c.getRightField()).forEach(rightField -> {
//                                if (!fieldSet.contains(rightField)) {
//                                    validateResult.addError("$.configuration.actions[" + finalI + "].conditions[*].rightField", "字段" + rightField + "在表" + action.getTableRight() + "不存在");
//                                }
//                            });
//                        }
//                    });
//                }, () -> validateResult.addError("$.configuration.actions[" + finalI + "].table", "指定的表" + action.getTableRight() + "不存在"));
//            }
//        }
//
//
//        if (validateResult.getErrors().size() > 0) {
//            validateResult.setValid(false);
//        }
//        return validateResult;
//    }
}
