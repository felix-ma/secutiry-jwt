package com.felix.security.jwt;

import com.felix.security.jwt.mapper.UserInfoMapper;
import com.felix.security.jwt.entity.UserInfo;
import com.felix.security.jwt.security.JwtTokenResolve;
import com.felix.security.jwt.security.JwtTokenUtil;
import com.felix.security.jwt.security.JwtUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author grez
 * @since 19-1-13
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityJwtApp.class)
public class UserTest {

    @Autowired
    private PasswordEncoder passwordEncoderBean;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtTokenResolve jwtTokenResolve;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @Test
    public void addUserTest() {
        String password = passwordEncoderBean.encode("password");
        UserInfo user = new UserInfo();
        user.setUsername("disabled");
        user.setPassword(password);
        user.setEnabled(true);
        user.setUpdateTime(new Date());

        userInfoMapper.insert(user);
    }

    @Test
    public void encodeJWT() {
        UserDetails admin = userDetailsService.loadUserByUsername("admin");
        System.out.println(admin);
        String token = jwtTokenUtil.generateToken((JwtUser) userDetailsService.loadUserByUsername("admin"));
        String audienceFromToken = jwtTokenResolve.getAuthorityFromToken(token);
        System.out.println(audienceFromToken);
        Boolean canlogin = jwtTokenResolve.validateToken(token, admin);
        System.out.println(canlogin);
    }

    @Test
    public void decodeJWT() {
        String s = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU0ODIyNTIwMiwiaWF0IjoxNTQ3NjIwNDAyfQ.X7yYFqnpqQpl8hhQqfz84JiUxga05kKDC0LsmDIM9vZBYfcMBIdSINPGnTYNNIxQouKIqppUOE9EY9H2pNVrxss6dOuDmQfhh27cyuS1OBL8qiMVWqqGzpBuQxc3-MCDHAFzyJbKY594XouYLccC9AjU1tR8Ww1hSV0sz9n20y1VvFYAbTIQHIuwlbpBqIa3ZfM8CvcLOtLX-L7sYJjk0lZO7rcn94fO50n0pT0aKx4NSm0X0m7oUDkUcfRW4WAcP1R7hmu_QOCcm42o9I5xjvnCGOGDLsXvJi7i32oGx-N6_-FVQvp0tlEV8ELTJ9AO3ZyPTd3zgCUQ3PL-1nEc-w";
        String usernameFromToken = jwtTokenResolve.getUsernameFromToken(s);
        Date issuedAtDateFromToken = jwtTokenResolve.getIssuedAtDateFromToken(s);
        Date expirationDateFromToken = jwtTokenResolve.getExpirationDateFromToken(s);
        String audienceFromToken = jwtTokenResolve.getAuthorityFromToken(s);
        System.out.println(audienceFromToken);
        System.out.println(usernameFromToken);
        ZoneId zone = ZoneId.systemDefault();
        System.out.println(LocalDateTime.ofInstant(issuedAtDateFromToken.toInstant(), zone));
        System.out.println(LocalDateTime.ofInstant(expirationDateFromToken.toInstant(), zone));

        Boolean aBoolean = jwtTokenResolve.canTokenBeRefreshed(s, new Date());
        System.out.println(aBoolean);
    }
}
