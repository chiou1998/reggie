package com.itheima.reggie.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;



@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {


    @Override //设置静态资源映射
    protected void addResourceHandlers(ResourceHandlerRegistry Registry){
        log.info("开始静态资源映射...");
        Registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        Registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
