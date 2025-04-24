package org.springframework.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wrp
 * @since 2025年04月22日 13:38
 **/
@Configuration
public class ChatClientConfig {

	@Bean
	public ChatClient chatClient(ChatModel chatModel) {
		return ChatClient.builder(chatModel)
				// 设置默认系统文本
//				.defaultSystem("You are a friendly chat bot that answers question in the voice of a Pirate")
				// 带参数voice
				.defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
				.build();
	}

	@Bean
	public ChatMemory chatMemory() {
		return new InMemoryChatMemory();
	}
}
