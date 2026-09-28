package br.com.trcon.site.shared.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ChatAiProperties.class)
public class ChatAiConfig {}
