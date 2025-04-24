package org.springframework.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wrp
 * @since 2025年04月22日 15:00
 **/
@RestController
@RequestMapping("advisor")
public class AdvisorController {

	@Autowired
	ChatClient chatClient;

	@GetMapping("logger")
	String logger() {
		return chatClient.prompt()
//				.advisors(new SimpleLoggerAdvisor())
				// 自定义日志
				.advisors(new SimpleLoggerAdvisor(
						request -> "Custom request: " + request.userText(),
						response -> "Custom response: " + response.getResult(),
						0
				))
				.user("讲个笑话")
				.call()
				.content();
	}
}
