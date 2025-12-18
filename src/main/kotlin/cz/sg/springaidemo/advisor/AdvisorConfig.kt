package cz.sg.springaidemo.advisor

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdvisorConfig {

    @Bean
    fun advisorChatClient(chatModel: OpenAiChatModel): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultAdvisors(
                LoggingAdvisor()
            )
            .build()
    }
}