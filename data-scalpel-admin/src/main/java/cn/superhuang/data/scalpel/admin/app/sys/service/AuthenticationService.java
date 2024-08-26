package cn.superhuang.data.scalpel.admin.app.sys.service;

import cn.superhuang.data.scalpel.admin.app.sys.domain.User;
import cn.superhuang.data.scalpel.admin.app.sys.model.LoginToken;
import cn.superhuang.data.scalpel.admin.app.sys.repository.UserRepository;
import cn.superhuang.data.scalpel.admin.service.dto.AuthenticationResultDTO;
import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtService jwtService;
    @Resource
    private AuthenticationManager authenticationManager;

    public LoginToken authenticate(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        User user = userRepository.findByName(username).orElseThrow();
        String token=jwtService.generateToken(user);
        LoginToken loginToken=new LoginToken();
        loginToken.setAccessToken(token);
        return loginToken;
    }

}
