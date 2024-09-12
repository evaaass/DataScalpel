package cn.superhuang.data.scalpel.admin;

import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.actuator.canvas.CanvasLine;
import cn.superhuang.data.scalpel.actuator.canvas.node.input.JdbcInput;
import cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration.JdbcInputConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.input.configuration.JdbcInputItem;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.UUIDProcessor;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.UUIDConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.processor.configuration.action.UUIDAction;
import cn.superhuang.data.scalpel.admin.app.task.model.CanvasPreRunSummary;
import cn.superhuang.data.scalpel.admin.app.task.web.resource.request.CanvasPreRunRequest;
import cn.superhuang.data.scalpel.app.task.model.TimeRangeStrategy;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import cn.superhuang.data.scalpel.model.enumeration.CanvasNodeCategory;
import cn.superhuang.data.scalpel.model.enumeration.ColumnType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CreatePreRunTestData {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        JdbcInputConfiguration jdbcInputConfiguration = new JdbcInputConfiguration();
        jdbcInputConfiguration.setDatasourceId("5b3eea76-b22c-4580-ab3b-40018ab248d7");
        jdbcInputConfiguration.setStrategy(TimeRangeStrategy.All());
        jdbcInputConfiguration.setItems(Collections.singletonList(new JdbcInputItem("sys_user", null, new HashMap<>())));
        JdbcInput jdbcInput = new JdbcInput();
        jdbcInput.setConfiguration(jdbcInputConfiguration);
        jdbcInput.setId("input01");
        jdbcInput.setCategory(CanvasNodeCategory.INPUT);
        jdbcInput.setType(".input.JdbcInput");

        UUIDConfiguration uuidConfiguration = new UUIDConfiguration(Collections.singletonList(new UUIDAction("sys_user", "super_uuid")));
        UUIDProcessor uuidProcessor = new UUIDProcessor();
        uuidProcessor.setConfiguration(uuidConfiguration);
        uuidProcessor.setId("uuid01");
        uuidProcessor.setCategory(CanvasNodeCategory.PROCESSOR);
        uuidProcessor.setType(".processor.UUIDProcessor");


        Canvas canvas = new Canvas(new ArrayList<>(),new ArrayList<>());
        canvas.getNodes().add(jdbcInput);
        canvas.getNodes().add(uuidProcessor);

        canvas.getLines().add(new CanvasLine("input01","uuid01"));

        DataTable dataTable=new DataTable();
        dataTable.setName("sys_user");
        dataTable.setAlias("用户表");
        dataTable.setColumns(new ArrayList<>());

        DataTableColumn column01=new DataTableColumn();
        column01.setName("guid");
        column01.setAlias("guid");
        column01.setRemark("remark");
        column01.setType(ColumnType.STRING);
        column01.setPrecision(50);

        DataTableColumn column02=new DataTableColumn();
        column02.setName("name");
        column02.setAlias("name");
        column02.setRemark("hehe");
        column02.setType(ColumnType.STRING);
        column02.setPrecision(50);

        dataTable.getColumns().add(column01);
        dataTable.getColumns().add(column02);

        CanvasPreRunSummary preRunSummary = new CanvasPreRunSummary();
        preRunSummary.setNodeId("input01");
        preRunSummary.setOutputTables(Collections.singletonList(dataTable));




        CanvasPreRunRequest request = new CanvasPreRunRequest();
        request.setCanvas(objectMapper.writeValueAsString(canvas));
        request.setInputSummaries(Collections.singletonList(preRunSummary));

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));

    }
}
