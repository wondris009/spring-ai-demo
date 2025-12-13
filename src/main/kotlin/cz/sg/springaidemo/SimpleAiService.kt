package cz.sg.springaidemo

import mu.KotlinLogging
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Service
class SimpleAiService(chatClientBuilder: ChatClient.Builder) {

    private val chatClient = chatClientBuilder.build()

    fun getResponseAsText(prompt: String) = chatClient.prompt().user(prompt).call().content()

    fun getResponseAsChatResponse(prompt: String): ChatResponse? {
        val chatResponse = chatClient.prompt().user(prompt).call().chatResponse()
        return chatResponse
    }

    fun getResponseWithPrompt(systemText: String, userText: String): String? {
        val systemPrompt = SystemMessage(systemText)
        val userPrompt = UserMessage(userText)

        val prompt = Prompt(listOf(systemPrompt, userPrompt))
        val response = chatClient.prompt(prompt).call().content()
        return response
    }
}

@RestController
class DummyController(private val simpleAiService: SimpleAiService) {

    @GetMapping("/ask-hello")
    fun asText() = simpleAiService.getResponseAsText("Say hello world for me")

    @GetMapping("/ask-chat-response")
    fun asChatResponse(@RequestParam text: String) = simpleAiService.getResponseAsChatResponse(text)

    @GetMapping("/ask-with-prompt")
    fun withPrompt(@RequestParam systemText: String, @RequestParam userText: String) =
        simpleAiService.getResponseWithPrompt(systemText, userText)
}

private val logger = KotlinLogging.logger {}