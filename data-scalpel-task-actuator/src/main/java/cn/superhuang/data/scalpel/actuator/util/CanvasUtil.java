package cn.superhuang.data.scalpel.actuator.util;

import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.actuator.canvas.node.IDatasourceConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.IModelConfiguration;
import cn.superhuang.data.scalpel.model.enumeration.CanvasNodeCategory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CanvasUtil {

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
