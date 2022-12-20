package com.itheima.reggie.config;



import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {


    @Override //设置静态资源映射
    protected void addResourceHandlers(ResourceHandlerRegistry Registry){
        log.info("开始静态资源映射...");
        Registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        Registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    @Override //拓展mvc框架的消息转换器
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,messageConverter);
    }
}
