package cz.sg.springaidemo.basic

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BasicController(private val aiService: AiService) {

    @GetMapping("/ask")
    fun asText(@RequestParam text: String = "Say hello world for me") = aiService.getResponseAsText(text)

    @GetMapping("/ask-chat-response")
    fun asChatResponse(@RequestParam text: String) = aiService.getResponseAsChatResponse(text)

    @GetMapping("/ask-with-prompt")
    fun withPrompt(@RequestParam systemText: String, @RequestParam userText: String) =
        aiService.getResponseWithPrompt(systemText, userText)
}