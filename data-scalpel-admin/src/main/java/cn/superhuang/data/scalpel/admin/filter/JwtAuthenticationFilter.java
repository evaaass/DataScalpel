package cn.superhuang.data.scalpel.admin.filter;

import cn.superhuang.data.scalpel.admin.app.sys.service.JwtService;
import cn.superhuang.data.scalpel.admin.app.sys.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtService jwtService;
    @Resource
    private UserService userDetailService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull                HttpServletResponse response,
            @NonNull        FilterChain filterChain) throws ServletException, IOException {

        String authHeader=request.getHeader("Authorization");
        // Authorization başlığı yoksa veya "Bearer" ile başlamıyorsa filtreyi geçiyoruz
        if (authHeader==null || !authHeader.startsWith("Bearer"))
        {
            filterChain.doFilter(request,response);
            return;
        }
        String token=authHeader.substring(7);
        String username=jwtService.extractUsername(token);
        //Kullanıcı adı mevcutsa ve daha kimlik doğrulama yapılmamışssa
        if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
        {
            //Kullanıcı bilgilerini al
            UserDetails userDetails= userDetailService.loadUserByUsername(username);
            //Token geçerli mi kontrol et
            if (jwtService.isValid(token,userDetails))
            {
                UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities()
                );
                authenticationToken.setDetails(
                        //Kullanıcı detayları ayarlanır IP adresi oturum bilgileri gibi
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                //SecurityContext'e kimlik bilgileri eklenir
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}