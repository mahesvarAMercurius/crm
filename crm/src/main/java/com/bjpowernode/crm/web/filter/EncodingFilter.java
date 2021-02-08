package com.bjpowernode.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        //对post请求的参数进行过滤
        req.setCharacterEncoding("UTF-8");
        //对响应流数据的过滤
        resp.setContentType("text/html;charset=utf-8");
        //放行
        chain.doFilter(req,resp);
    }
}
