package cn.superhuang.data.scalpel.actuator.canvas;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CanvasData implements Serializable {
    @Serial
    private static final long serialVersionUID = -6517711363815510751L;
    public Map<String, CanvasTable> tableMap;

    public CanvasData(Map<String, CanvasTable> tableMap) {
        this.tableMap = tableMap;
    }
}
