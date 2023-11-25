package org.example;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/thymeleafCookie")
public class ThymeleafCookieController extends HttpServlet {
    private transient TemplateEngine engine;
    @Override
    public void init() {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:/Users/777/IdeaProjects/devHomework12/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        Map<String, Object> params = new LinkedHashMap<>();
        String timezone = req.getParameter("timezone");
        ZoneOffset zoneOffset = null;
        if(req.getParameter("timezone") != null){
            //setting timezone from parameters
            zoneOffset = ZoneOffset.of(timezone);
        }else if(hasCookieTimezone(req)){
            Cookie[] cookies = req.getCookies();
            for (Cookie cookie : cookies) {
                if(String.valueOf(cookie.getName()).equals("timezone")){
                    zoneOffset = ZoneOffset.of(String.valueOf(cookie.getValue()));
                }
            }
        }else{
            //setting UTC timezone
            zoneOffset = ZoneOffset.of("Z");
        }
        //formatting time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        String formattedTime = OffsetDateTime.now(zoneOffset).format(formatter);

        params.put("time",formattedTime);

        Context simpleContext = new Context(
                req.getLocale(),
                params
        );
        engine.process("cookieTime", simpleContext, resp.getWriter());
        //saving last used timezone to cookies
        resp.addCookie(new Cookie("lastTimezone", timezone));
        resp.getWriter().close();
    }

    private boolean hasCookieTimezone(HttpServletRequest req){
        Cookie[] cookies = req.getCookies();
        for(Cookie cookie : cookies){
            if(String.valueOf(cookie.getName()).equals("timezone")) return true;
        }
        return false;
    }
}