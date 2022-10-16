package com.example.torchwebsite.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.api.pojo.Admin;
import com.example.torchwebsite.service.AdminService;
import com.example.torchwebsite.utils.JwtUtil;
import io.fusionauth.jwt.JWTExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final AdminService adminService;

    //    注入bean即可使用，这里可以不写@Autowired
    @Autowired
    public AdminInterceptor(JwtUtil jwtUtil, AdminService adminService) {
        this.jwtUtil = jwtUtil;
        this.adminService = adminService;
    }


    /**
     * 在这里进行用户登录校验，若用户为登录，则通过response返回一个error
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenBearer = request.getHeader("Authorization");
        if(tokenBearer == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String[] tokenSplitRet = tokenBearer.split(" ");
        if (tokenSplitRet.length != 2){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        String token = tokenSplitRet[1];

        Map<String, Object> payload;
        try {
            payload = jwtUtil.decodeToken(token);
        }catch (JWTExpiredException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("id", payload.get("uid"));

        Admin admin = adminService.getBaseMapper().selectOne(wrapper);

        if (admin == null){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        request.setAttribute("Admin", admin);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
