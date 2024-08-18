package cn.superhuang.data.scalpel.actuator.util;

import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.actuator.canvas.node.IDatasourceConfiguration;
import cn.superhuang.data.scalpel.actuator.canvas.node.IModelConfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CanvasUtil {
    public static Set<String> getCanvasModelIds(Canvas canvas) {
        return canvas.getNodes().stream().filter(node -> (node.getConfiguration() instanceof IModelConfiguration)).map(node -> ((IModelConfiguration) node.getConfiguration()).getModelIds()).flatMap(Collection::stream).collect(Collectors.toSet());

    }

    public static Set<String> getCanvasDatasourceIds(Canvas canvas) {
        return canvas.getNodes().stream().filter(node -> (node.getConfiguration() instanceof IDatasourceConfiguration)).map(node -> ((IDatasourceConfiguration) node.getConfiguration()).getDatasourceId()).collect(Collectors.toSet());
    }
}
