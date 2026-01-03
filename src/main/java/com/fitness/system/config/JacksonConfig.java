package com.fitness.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Jackson配置类，确保正确处理UTF-8编码和日期类型
 */
@Configuration
public class JacksonConfig {

    /**
     * 自定义ObjectMapper，确保正确处理日期类型和UTF-8编码
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册JavaTimeModule以支持Java 8日期时间类型
        objectMapper.registerModule(new JavaTimeModule());
        // 配置日期序列化格式
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 允许序列化空值
        objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /**
     * 自定义MappingJackson2HttpMessageConverter，确保使用UTF-8编码
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        // 创建转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 设置ObjectMapper
        converter.setObjectMapper(objectMapper);
        // 明确设置UTF-8编码
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        
        return converter;
    }
}