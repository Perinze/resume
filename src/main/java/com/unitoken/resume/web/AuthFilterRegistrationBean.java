package com.unitoken.resume.web;

import com.unitoken.resume.context.UserContext;
import com.unitoken.resume.exception.PermissionDenied;
import com.unitoken.resume.model.User;
import com.unitoken.resume.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Component
public class AuthFilterRegistrationBean extends FilterRegistrationBean<Filter> {
    @Autowired
    UserService userService;

    @Autowired
    UserContext userContext;

    @Override
    public Filter getFilter() {
        setUrlPatterns(List.of("/cvs", "/cv/*"));
        return new AuthFilter();
    }

    class AuthFilter implements Filter {
        final Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            String token = req.getHeader("authorization");
            logger.info("authorization header -> token " + token);
            if (token == null) {
                logger.error("token null");
                throw new PermissionDenied();
            }
            User user;
            try {
                user = userService.getUser(userService.authorize(token));
            } catch (Exception e) {
                throw new PermissionDenied();
            }
            logger.info("token -> open id " + user.getOpenId());
            if (user.getOpenId() == null) {
                logger.error("open id not mapped by token");
                throw new PermissionDenied();
            }
            userContext.setUser(user);
            logger.info("set user in context");
            chain.doFilter(request, response);
        }
    }
}
