package cn.superhuang.data.scalpel.admin.web.resource;


import cn.superhuang.data.scalpel.actuator.canvas.node.input.JdbcInput;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "画布例子接口")
@RequestMapping("/api/v1")
public class ExampleResource {
    @Operation(summary = "INPUT-JdbcInput")
    @GetMapping("/canvas/input/JdbcInput")
    GenericResponse<JdbcInput> JdbcInput() {
        return GenericResponse.ok(new JdbcInput());
    }


    @Operation(summary = "test")
    @GetMapping("/canvas/input/test")
    GenericResponse<Map<String,Object>> test() {
        Map<String,Object> test=new HashMap<>();
        test.put("create_time",new Date());
        return GenericResponse.ok(test);
    }
}
