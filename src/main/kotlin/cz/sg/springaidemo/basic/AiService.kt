package cz.sg.springaidemo.basic

import mu.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service

@Service
class AiService(chatClientBuilder: ChatClient.Builder) {

    private val chatClient = chatClientBuilder.build()

    fun getResponseAsText(prompt: String) = chatClient.prompt().user(prompt).call().content()

    fun getResponseAsChatResponse(prompt: String): ChatResponse? {
        val chatResponse = chatClient.prompt().user(prompt).call().chatResponse()
        return chatResponse
    }

    fun getResponseWithPrompt(systemText: String, userText: String): String? {
        val systemPrompt = SystemMessage(systemText)
        val userPrompt = UserMessage(userText)
//        val toolResponseMessage = ToolResponseMessage()
        val assistantMessage = AssistantMessage("")

        val prompt = Prompt(listOf(systemPrompt, userPrompt))
        val response = chatClient.prompt(prompt).call().content()
        return response
    }
}

private val logger = KotlinLogging.logger {}