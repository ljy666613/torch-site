package com.example.front.config;

import com.example.front.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置与实例化
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final UserInterceptor interceptor;

    @Autowired
    public InterceptorConfig(UserInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     *    添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] path = {
                "/**"
        };
        String[] excludePath = {
                "/api/front/login",
//                "/api/front/mailVerify",
//                "/api/front/codeCheck",
                "/api/front/register"
        };
        registry.addInterceptor(interceptor).addPathPatterns(path).excludePathPatterns(excludePath);
    }

    /**
     *    这里是静态资源映射，将static对应的地址映射到file下面，因为静态文件的具体位置并不相同
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        配置图片路径映射，本地
        String path = System.getProperty("user.dir")+"";
//        String path = System.getProperty("user.dir")+"/static/";服务器可用
        registry.addResourceHandler("/static/**").addResourceLocations("file:"+path);
    }
}
