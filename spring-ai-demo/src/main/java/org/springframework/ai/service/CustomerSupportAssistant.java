package org.springframework.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author wrp
 * @since 2025年04月22日 14:36
 **/
@Service
public class CustomerSupportAssistant {

	private final ChatClient chatClient;

	public CustomerSupportAssistant(ChatClient.Builder builder,
									VectorStore vectorStore,
									ChatMemory chatMemory) {
		this.chatClient = builder
				.defaultSystem("""
                    You are a customer chat support agent of an airline named "Funnair". Respond in a friendly,
                    helpful, and joyful manner.

                    Before providing information about a booking or cancelling a booking, you MUST always
                    get the following information from the user: booking number, customer first name and last name.

                    Before changing a booking you MUST ensure it is permitted by the terms.

                    If there is a charge for the change, you MUST ask the user to consent before proceeding.
                    """)
				.defaultAdvisors(
						new MessageChatMemoryAdvisor(chatMemory), // CHAT MEMORY
						new QuestionAnswerAdvisor(vectorStore), // RAG
						new SimpleLoggerAdvisor())
				.defaultFunctions("getBookingDetails", "changeBooking", "cancelBooking") // FUNCTION CALLING
				.build();
	}

	public Flux<String> chat(String chatId, String userMessageContent) {

		return this.chatClient.prompt()
				.user(userMessageContent)
				.advisors(a -> a
						.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
						.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
				.stream().content();
	}
}
