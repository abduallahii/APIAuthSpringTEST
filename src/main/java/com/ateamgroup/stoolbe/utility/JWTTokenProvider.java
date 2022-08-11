package com.ateamgroup.stoolbe.utility;


import com.ateamgroup.stoolbe.models.UserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static com.ateamgroup.stoolbe.constant.SecurityConstant.*;
import static java.util.Arrays.stream;

@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret ;

    //Generate JWT Token LOGIC
    public String generateJwtToken(UserPrincipal userPrincipal) {

        String[] claims = getClaimsFromUser(userPrincipal);

        return JWT.create()
                .withIssuer(ETISALAT_CO_LLC)
                .withAudience(ETISALAT_CO_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    // to get permissions from exist Token
    // to get permissions from exist Token
    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // we have
    public Authentication getAuthentication(String UserName
                                            , List<GrantedAuthority> authorities
                                            , HttpServletRequest request ){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(UserName ,null,authorities);
    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken ;
    }

    public boolean isTokenValid(String userName , String Token){
        return StringUtils.isNotEmpty(userName) && !isTokenExpired(getJWTVerifier(),Token);
    }


    public String getSubject(String token){
        return getJWTVerifier().verify(token).getSubject();
    }


    // check if expired or not
    private boolean isTokenExpired(JWTVerifier jwtVerifier, String token) {
        Date expiration = getJWTVerifier().verify(token).getExpiresAt();
        return expiration.before(new Date()) ;
    }


    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        //AUTHORITIES from security statics with it`s claims above
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class) ;
    }


    //Verifier token
    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier ;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm)
                    .withIssuer(ETISALAT_CO_LLC)
                    .build();
        } catch (JWTVerificationException exception) {
   // we do not want to use our exception to protect our date use generic one
            throw  new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED) ;
        }
        return verifier ;
    }


    //Create user Permissions
    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : userPrincipal.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
            return authorities.toArray(new String[0]);
//        return userPrincipal.getAuthorities().toArray(new String[0]);
//        return authorities.toArray(new String[0]);
    } }