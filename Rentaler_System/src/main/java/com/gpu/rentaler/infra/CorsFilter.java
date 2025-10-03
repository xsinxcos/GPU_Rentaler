//package com.gpu.rentaler.infra;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class CorsFilter implements Filter {
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        HttpServletResponse res = (HttpServletResponse) response;
//        HttpServletRequest req = (HttpServletRequest) request;
//
//        res.setHeader("Access-Control-Allow-Origin", "*"); // 或指定域名
//        res.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
//        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        res.setHeader("Access-Control-Allow-Credentials", "true");
//        res.setHeader("Access-Control-Max-Age", "3600");
//
//        // 对OPTIONS请求直接返回200
//        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
//            res.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//}
