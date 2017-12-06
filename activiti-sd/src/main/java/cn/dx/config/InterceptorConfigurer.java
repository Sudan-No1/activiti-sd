package cn.dx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cn.dx.interceptor.SessionInterceptor;

@Configuration
public class InterceptorConfigurer extends WebMvcConfigurerAdapter{
	
	//test
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/cmdbController/*","/billController/*","/userController/*","/workflow/*");
		 super.addInterceptors(registry);
	}
}
