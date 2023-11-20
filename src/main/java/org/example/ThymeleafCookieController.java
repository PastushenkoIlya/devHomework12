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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        /*String timezone;
        if(req.getParameter("timezone") != null){
            timezone = req.getParameter("timezone");
        } else {
            timezone = TimeZone.getDefault().toZoneId().toString();
        }
        ZonedDateTime time = ZonedDateTime.now(ZoneId.of(timezone));
        String formattedTime = time.format(formatter);*/
        String timezone = req.getParameter("timezone");
        ZoneOffset zoneOffset;
        if(req.getParameter("timezone") != null){
            zoneOffset = ZoneOffset.of(timezone);
        }else{
            zoneOffset = ZoneOffset.of(req.getParameter("UTC"));
        }
        String formattedTime = OffsetDateTime.now(zoneOffset).format(formatter);

        params.put("time",formattedTime);

        Context simpleContext = new Context(
                req.getLocale(),
                params
        );
        engine.process("time", simpleContext, resp.getWriter());
        //saving last used timezone to cookies
        resp.addCookie(new Cookie("lastTimezone", timezone));
        resp.getWriter().close();
    }
}