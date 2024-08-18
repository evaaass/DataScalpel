package cn.superhuang.data.scalpel.actuator.canvas.node.output.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldMapping  implements Serializable {
    @Serial
    private static final long serialVersionUID = 4529294200907005512L;
    private String sourceFieldName;
    private String targetFieldName;
}
