package cz.sg.springaidemo.rag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.api.Advisor
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Schedulers

@Configuration
class RagConfig {

    @Bean
    fun chatMemory(): ChatMemory {
        // Spring AI also auto-configures InMemoryChatMemoryRepository by default,
        // but we create it explicitly so it's obvious and easy to tweak.
        val repo = InMemoryChatMemoryRepository()

        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(repo)
            .maxMessages(20)
            .build()
    }

    @Bean
    fun messageChatMemoryAdvisor(chatMemory: ChatMemory): MessageChatMemoryAdvisor {
        return MessageChatMemoryAdvisor.builder(chatMemory)
            .conversationId("default") // used if caller doesn't provide conversationId
            .order(Advisor.DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER)
            .scheduler(Schedulers.boundedElastic())
            .build()
    }

    @Bean
    fun questionAnswerAdvisor(vectorStore: VectorStore): QuestionAnswerAdvisor {
        val ragTemplate = PromptTemplate(
            """
                {query}
        
                Context information is below.
                ---------------------
                {question_answer_context}
                ---------------------
        
                Given the context information and no prior knowledge, answer the query.
                If the answer is not in the context, say you don't know.
            """.trimIndent()
        )

        return QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder().topK(3).similarityThreshold(0.7).build())
            .promptTemplate(ragTemplate)
            .scheduler(QuestionAnswerAdvisor.DEFAULT_SCHEDULER) // if available in your IDE; otherwise use Schedulers.boundedElastic()
            .order(0)
            .build()
    }

    @Bean
    fun ragChatClient(
        builder: ChatClient.Builder,
        messageChatMemoryAdvisor: MessageChatMemoryAdvisor,
        questionAnswerAdvisor: QuestionAnswerAdvisor
    ): ChatClient {
        return builder
            .defaultSystem(
                """
                You are a helpful assistant.
                Use the retrieved CONTEXT when it is relevant.
                If the answer is not in context and you are unsure, say you don't know.
                """.trimIndent()
            )
            .defaultAdvisors(messageChatMemoryAdvisor, questionAnswerAdvisor)
            .build()
    }
}