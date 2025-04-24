package org.springframework.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.entity.ActorFilms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wrp
 * @since 2025年04月22日 11:30
 **/
@RestController
@RequestMapping(value = "chat", produces = "application/json; charset=utf-8")
public class MyController {

	@Autowired
	ChatClient chatClient;

	// 返回String
	@GetMapping("ai")
	String chat(@RequestParam("user") String userInput) {
		return this.chatClient.prompt()
				.user(userInput)
				.call()
				.content();
	}

	// 返回ChatResponse
	@GetMapping("joke")
	String tellJoke() {
		ChatResponse chatResponse = chatClient.prompt()
				.user("Tell me a joke，中文回答")
				.call()
				.chatResponse();
		return chatResponse.toString();
	}

	// 返回实体类
	@GetMapping("film")
	String film() {
		ActorFilms actorFilms = chatClient.prompt()
				.user("Generate the filmography for a random actor，中文回答")
				.call()
				.entity(ActorFilms.class);
		return actorFilms.toString();
	}

	// 返回实体类集合
	@GetMapping("film/list")
	String filmList() {
		List<ActorFilms> actorFilms = chatClient.prompt()
				.user("Generate the filmography of 5 movies for Tom Hanks and Bill Murray.")
				.call()
				.entity(new ParameterizedTypeReference<List<ActorFilms>>() {});
		return actorFilms.toString();
	}

	// stream流
	@GetMapping("stream")
	Flux<String> stream() {
		return this.chatClient.prompt()
				.user("给我讲个故事，300字。")
				.stream()
				.content();
	}

	// 结构化输出
	@GetMapping("output")
	String output() {
		var converter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<ActorFilms>>() {});

		Flux<String> flux = this.chatClient.prompt()
				.user(u -> u.text("""
                        Generate the filmography for a random actor.
                        {format}
                      """)
						.param("format", converter.getFormat()))
				.stream()
				.content();

		String content = flux.collectList().block().stream().collect(Collectors.joining());

		List<ActorFilms> actorFilms = converter.convert(content);
		return actorFilms.toString();
	}
}
