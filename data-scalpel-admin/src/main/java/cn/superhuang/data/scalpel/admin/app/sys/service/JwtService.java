package cn.superhuang.data.scalpel.admin.app.sys.service;

import cn.superhuang.data.scalpel.admin.app.sys.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY="6c7cbcbdc2f9212e649e675cddf07b91444001da9aa959714506c8f6f5ba8306";

    //Token içerisindeki username'i çıkar
    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }
    //Token içerisindeki belirli bir değeri(claims) alan fonksiyon
    public<T> T extractClaim(String token, Function<Claims,T> resolver)
    {
        Claims claims=extractAllClaims(token);
        //resolver fonksiyonunu kullanarak istenilen claims'i döner
        return resolver.apply(claims);
    }
    //Token'in geçerli olup olmadığını kontrol eden fonksiyon
    public boolean isValid(String token, UserDetails user)
    {
        String username=extractUsername(token);
        return (username.equals(user.getUsername())&& !isTokenExpired(token));
    }
    //Token'in son kullanma tarihine bakılan fonksiyon
    private boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }
    //Token'in son kullanma tarihini çıkaran fonksiyon
    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }
    //Token içerisinden bütün claimleri çıkaran fonksiyon
    public Claims extractAllClaims(String token)
    {
        return Jwts
                .parser()// parser oluşturuldu
                .verifyWith(getSigninKey())//İmza anahtarının alınması
                .build()
                .parseSignedClaims(token)//Token parse edilir
                .getPayload(); //claimler alınır
    }
    //Token oluşturma
    public String generateToken(User user)
    {
        String token=Jwts.builder()
                .subject(user.getName())
                .issuedAt(new Date(System.currentTimeMillis()))//Token oluşturma tarihi
                .expiration(new Date(System.currentTimeMillis()+24*60*60*1000))//Son kullanma tarihi 1 gün
                .signWith(getSigninKey())//Token imzalama
                .compact();// Token oluşturma
        return token;
    }
    //İmza anahtarını al
    private SecretKey getSigninKey()
    {
        byte[] keyBytes= Decoders.BASE64URL.decode(SECRET_KEY); //Anahtarı BASE64 formatında çıkar
        return Keys.hmacShaKeyFor(keyBytes); //anahtarı algoritma için uygun hale getir.
    }


}