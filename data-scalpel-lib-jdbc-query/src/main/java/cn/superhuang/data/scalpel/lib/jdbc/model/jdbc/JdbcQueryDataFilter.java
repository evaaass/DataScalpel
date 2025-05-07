package cn.superhuang.data.scalpel.lib.jdbc.model.jdbc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdbcQueryDataFilter {
    private String name;
    /**
     * = | != | > | >= | < | <= | in | not in | between | like | not like | is null | is not null | is empty | is not empty
     */
    private String operator;
    private Object value;

    public List<Object> toListValue() {
        if (value instanceof List) {
            return (List) value;
        } else {
            return Collections.singletonList(value);
        }
    }

    public Object getFirstValue() {
        if (value instanceof List) {
			List value = (List) this.value;
			return value.size() > 0 ? value.get(0) : null;
        } else {
            return value;
        }
    }

    public Boolean isValueEmpty() {
        if (value instanceof List) {
            return ((List<?>) value).isEmpty();
        } else {
            return value == null;
        }
    }
}
