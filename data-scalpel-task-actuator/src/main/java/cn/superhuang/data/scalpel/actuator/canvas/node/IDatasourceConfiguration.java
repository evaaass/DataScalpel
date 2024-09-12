package cn.superhuang.data.scalpel.actuator.canvas.node;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

public interface IDatasourceConfiguration {
    @JsonIgnore
    public Set<String> getDatasourceIds();
}
