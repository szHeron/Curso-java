package com.heron.TodoList.filter;

import java.io.IOException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.heron.TodoList.user.IUserRepository;
import com.heron.TodoList.user.UserModel;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
            String servletPath = request.getServletPath();
            if(!servletPath.contains("/users")){
                String authorizationEncoded = request.getHeader("Authorization");
                authorizationEncoded = authorizationEncoded.substring("basic".length()).trim();

                byte[] authorizationDecoded = Base64.decodeBase64(authorizationEncoded);
                String auth = new String(authorizationDecoded);
                String[] credentials = auth.split(":");
                String username = credentials[0];
                String password = credentials[1];

                UserModel user = this.userRepository.findByUsername(username);

                if(user == null){
                    response.sendError(401);
                }else{
                    var result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                    if(result.verified){
                        request.setAttribute("userId", user.getId());
                        filterChain.doFilter(request, response);
                    }else{
                        response.sendError(401);
                    }
                }
            }else{
                filterChain.doFilter(request, response);
            }
    }


}
