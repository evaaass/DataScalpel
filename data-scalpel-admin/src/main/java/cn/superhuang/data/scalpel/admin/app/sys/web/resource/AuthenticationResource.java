package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.model.LoginToken;
import cn.superhuang.data.scalpel.admin.app.sys.service.AuthenticationService;
import cn.superhuang.data.scalpel.admin.app.sys.service.UserService;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.LoginRequest;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@ApiSupport(order = 0)
@Tag(name = "00.鉴权模块")
@RequestMapping("/api/v1")
@RestController
public class AuthenticationResource {

    @Resource
    private AuthenticationService authenticationService;

    @Operation(summary = "创建token")
    @PostMapping("/auth/generateToken")
    public GenericResponse<LoginToken> login(@RequestBody LoginRequest loginRequest) {
        LoginToken token = authenticationService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        return GenericResponse.ok(token);
    }

}
