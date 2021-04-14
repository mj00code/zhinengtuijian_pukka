package com.ubo.iptv.mybatis;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: xuning
 * @Date: 2019/07/29
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.ubo.iptv.mybatis.*.mapper"})
@ConditionalOnClass(PaginationInterceptor.class)
public class MybatisPlusAutoConfiguration {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}