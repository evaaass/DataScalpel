package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.model.LoginToken;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.LoginRequest;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationResource {

    public GenericResponse<LoginToken> login(@RequestBody LoginRequest loginRequest) {
        return null;
    }

}
