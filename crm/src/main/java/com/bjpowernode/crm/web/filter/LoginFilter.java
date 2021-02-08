package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if("/login.jsp".equals(request.getServletPath()) || "/settings/user/login.do".equals(request.getServletPath())){
            chain.doFilter(req,resp);
        }else{
            if(null != user){
                chain.doFilter(req,resp);
            }else{
                response.sendRedirect(request.getContextPath()+"/index.html");
            }
        }
    }
}
