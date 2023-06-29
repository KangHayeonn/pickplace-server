package com.server.pickplace.host.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HostConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
