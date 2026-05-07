package com.javaweb.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        /* * Cấu hình MatchingStrategy là STANDARD để ModelMapper có thể tự động ánh xạ 
         * giữa các biến có quy tắc đặt tên khác nhau (ví dụ: user_id trong Entity 
         * và userId trong DTO).
         */
        mapper.getConfiguration()
              .setMatchingStrategy(MatchingStrategies.STANDARD)
              .setFieldMatchingEnabled(true)
              .setSkipNullEnabled(true);
              
        return mapper;
    }
}