package cn.superhuang.data.scalpel.actuator.util;

import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.actuator.canvas.node.IDatasourceConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.IModelConfiguration;
import cn.superhuang.data.scalpel.model.enumeration.CanvasNodeCategory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class CanvasUtil {
    public static void main(String[] args) {
        String dwxCanvasContent = "{\"nodes\":[{\"position\":{\"x\":0,\"y\":100},\"size\":{\"width\":212,\"height\":48},\"view\":\"react-shape-view\",\"shape\":\"data-processing-dag-node\",\"ports\":{\"groups\":{\"in\":{\"position\":\"left\",\"attrs\":{\"circle\":{\"r\":4,\"magnet\":true,\"stroke\":\"transparent\",\"strokeWidth\":1,\"fill\":\"transparent\"}}},\"out\":{\"position\":{\"name\":\"right\",\"args\":{\"dx\":-32}},\"attrs\":{\"circle\":{\"r\":4,\"magnet\":true,\"stroke\":\"transparent\",\"strokeWidth\":1,\"fill\":\"transparent\"}}}},\"items\":[{\"id\":\"92437058-5ce1-466e-8a18-985c93c05b8f-out\",\"group\":\"out\",\"attrs\":{\"circle\":{\"fill\":\"transparent\",\"stroke\":\"transparent\"}}}]},\"id\":\"92437058-5ce1-466e-8a18-985c93c05b8f\",\"data\":{\"name\":\"开始\",\"type\":\".Starter\",\"status\":\"success\"},\"zIndex\":1},{\"position\":{\"x\":250,\"y\":100},\"size\":{\"width\":212,\"height\":48},\"view\":\"react-shape-view\",\"shape\":\"data-processing-dag-node\",\"ports\":{\"groups\":{\"in\":{\"position\":\"left\",\"attrs\":{\"circle\":{\"r\":4,\"magnet\":true,\"stroke\":\"transparent\",\"strokeWidth\":1,\"fill\":\"transparent\"}}},\"out\":{\"position\":{\"name\":\"right\",\"args\":{\"dx\":-32}},\"attrs\":{\"circle\":{\"r\":4,\"magnet\":true,\"stroke\":\"transparent\",\"strokeWidth\":1,\"fill\":\"transparent\"}}}},\"items\":[{\"id\":\"e911e001-b040-4311-a8e3-df8bd5a74973-in\",\"group\":\"in\",\"attrs\":{\"circle\":{\"fill\":\"transparent\",\"stroke\":\"transparent\"}}},{\"id\":\"e911e001-b040-4311-a8e3-df8bd5a74973-out\",\"group\":\"out\",\"attrs\":{\"circle\":{\"fill\":\"transparent\",\"stroke\":\"transparent\"}}}]},\"id\":\"e911e001-b040-4311-a8e3-df8bd5a74973\",\"data\":{\"type\":\".input.JdbcInput\",\"name\":\"关系数据库抽取\",\"configuration\":{\"datasourceId\":\"5b3eea76-b22c-4580-ab3b-40018ab248d7\",\"strategy\":{\"type\":\"ALL\"},\"items\":[{\"item\":\"ceshi_jzw_jyjz\",\"type\":\"JDBC_TABLE\",\"name\":\"ceshi_jzw_jyjz\",\"description\":\"教育建筑_已编码上图\",\"alias\":\"教育建筑_已编码上图\",\"columns\":[{\"originType\":\"varchar\",\"name\":\"entity_id\",\"alias\":\"教育建筑业务ID\",\"remark\":\"教育建筑业务ID\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"entity_name\",\"alias\":\"教育建筑名称\",\"remark\":\"教育建筑名称\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"region_code\",\"alias\":\"行政区划码\",\"remark\":\"行政区划码\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"type_code\",\"alias\":\"分类代码\",\"remark\":\"分类代码\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"code\",\"alias\":\"统一识别代码\",\"remark\":\"统一识别代码\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"department_name\",\"alias\":\"数据来源部门名称\",\"remark\":\"数据来源部门名称\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"beidou_code\",\"alias\":\"北斗网格位置码\",\"remark\":\"北斗网格位置码\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"constructi\",\"alias\":\"管理单位\",\"remark\":\"管理单位\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"spatial_coordinates\",\"alias\":\"空间坐标(x\",\"remark\":\"空间坐标(x,y)\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"project_name\",\"alias\":\"建设项目名称\",\"remark\":\"建设项目名称\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"project_location\",\"alias\":\"建设项目位置\",\"remark\":\"建设项目位置\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"construction_unit\",\"alias\":\"建设单位\",\"remark\":\"建设单位\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"land_use\",\"alias\":\"土地用途\",\"remark\":\"土地用途\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"land_acquisition\",\"alias\":\"土地取得方式\",\"remark\":\"土地取得方式\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"temporary_project\",\"alias\":\"是否为临时项目\",\"remark\":\"是否为临时项目\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"numeric\",\"name\":\"area\",\"alias\":\"总建筑面积\",\"remark\":\"总建筑面积\",\"type\":\"DECIMAL\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"address\",\"alias\":\"统一标准地址\",\"remark\":\"统一标准地址\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"date\",\"name\":\"create_time\",\"alias\":\"创建时间\",\"remark\":\"创建时间\",\"type\":\"DATE\",\"precision\":null,\"scale\":0},{\"originType\":\"date\",\"name\":\"update_time\",\"alias\":\"更新时间\",\"remark\":\"更新时间\",\"type\":\"DATE\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"engineering_code\",\"alias\":\"工建万物标识码\",\"remark\":\"工建万物标识码\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"geometry\",\"name\":\"geometry\",\"alias\":\"几何字段\",\"remark\":\"几何字段\",\"type\":\"GEOMETRY\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"model_id\",\"alias\":\"白膜id\",\"remark\":\"白膜id\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"fangpu_bianhao\",\"alias\":\"房普房屋编号\",\"remark\":\"房普房屋编号\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"fangpu_bh\",\"alias\":\"房普bh\",\"remark\":\"房普bh\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"ldg\",\"alias\":\"楼栋高\",\"remark\":\"楼栋高\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"dmg\",\"alias\":\"地面高\",\"remark\":\"地面高\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"lg\",\"alias\":\"楼高\",\"remark\":\"楼高\",\"type\":\"STRING\",\"precision\":null,\"scale\":0},{\"originType\":\"varchar\",\"name\":\"zpdj\",\"alias\":\"灾普等级\",\"remark\":\"灾普等级\",\"type\":\"STRING\",\"precision\":null,\"scale\":0}],\"metadata\":{}}]},\"status\":\"success\"},\"zIndex\":2}],\"lines\":[{\"shape\":\"data-processing-curve\",\"connector\":{\"name\":\"curveConnector\"},\"id\":\"6d1999df-5010-41b2-aaec-1d259e38188f\",\"zIndex\":-1,\"data\":{\"source\":\"92437058-5ce1-466e-8a18-985c93c05b8f\",\"target\":\"e911e001-b040-4311-a8e3-df8bd5a74973\"},\"source\":{\"cell\":\"92437058-5ce1-466e-8a18-985c93c05b8f\",\"port\":\"92437058-5ce1-466e-8a18-985c93c05b8f-out\"},\"target\":{\"cell\":\"e911e001-b040-4311-a8e3-df8bd5a74973\",\"port\":\"e911e001-b040-4311-a8e3-df8bd5a74973-in\"},\"from\":\"92437058-5ce1-466e-8a18-985c93c05b8f\",\"to\":\"e911e001-b040-4311-a8e3-df8bd5a74973\"}]}";

        Canvas canvas = fromDwxCanvasContent(dwxCanvasContent);
        System.out.println(canvas);
    }

    public static Canvas fromDwxCanvasContent(String dwxCanvasContent) {
        try {
            ObjectNode rootNode = JsonUtil.objectMapper.createObjectNode();
            rootNode.putArray("nodes");
            rootNode.putArray("lines");


            JsonNode jsonNode = JsonUtil.objectMapper.readTree(dwxCanvasContent);
            Iterator<JsonNode> dwxNodes = jsonNode.get("nodes").elements();
            String startNodeId = null;
            while (dwxNodes.hasNext()) {
                JsonNode dwxNode = dwxNodes.next();
                if (dwxNode.get("data").get("type").asText().equals(".Starter") || dwxNode.get("data").get("type").asText().equals(".start.Start")) {
                    startNodeId = dwxNode.get("id").asText();
                    continue;
                }

                ObjectNode node = JsonUtil.objectMapper.createObjectNode();
                node.setAll((ObjectNode) dwxNode.get("data"));
                node.put("id", dwxNode.get("id").asText());
                ((ArrayNode) rootNode.get("nodes")).add(node);
            }
            Iterator<JsonNode> dwxLines = jsonNode.get("lines").elements();
            while (dwxLines.hasNext()) {
                JsonNode dwxLine = dwxLines.next();
                String fromNodeId = dwxLine.get("from").asText();
                String toNodeId = dwxLine.get("to").asText();
                if (fromNodeId.equals(startNodeId) || toNodeId.equals(startNodeId)) {
                    continue;
                }
                ObjectNode line = JsonUtil.objectMapper.createObjectNode();
                line.put("from", fromNodeId);
                line.put("to", toNodeId);
                ((ArrayNode) rootNode.get("lines")).add(line);
            }

            System.out.println(rootNode.toPrettyString());
            return JsonUtil.objectMapper.treeToValue(rootNode, Canvas.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("转换丁文星画布失败:" + e.getMessage(), e);
        }

    }

    public static Set<String> getOutputModelIds(Canvas canvas) {
        return canvas.getNodes().stream().filter(node -> (node.getConfiguration() instanceof IModelConfiguration) && node.getCategory() == CanvasNodeCategory.INPUT).map(node -> ((IModelConfiguration) node.getConfiguration()).getModelIds()).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public static Set<String> getInputModelIds(Canvas canvas) {
        return canvas.getNodes().stream().filter(node -> (node.getConfiguration() instanceof IModelConfiguration) && node.getCategory() == CanvasNodeCategory.INPUT).map(node -> ((IModelConfiguration) node.getConfiguration()).getModelIds()).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public static Set<String> getModelIds(Canvas canvas) {
        return canvas.getNodes().stream().filter(node -> (node.getConfiguration() instanceof IModelConfiguration)).map(node -> ((IModelConfiguration) node.getConfiguration()).getModelIds()).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public static Set<String> getDatasourceIds(Canvas canvas) {
        return canvas.getNodes().stream().filter(node -> (node.getConfiguration() instanceof IDatasourceConfiguration)).flatMap(node -> ((IDatasourceConfiguration) node.getConfiguration()).getDatasourceIds().stream()).collect(Collectors.toSet());
    }
}
