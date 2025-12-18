package cz.sg.springaidemo.advisor

import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AdvisorController(private val advisorChatClient: ChatClient) {

    @GetMapping("/advisor")
    fun doWithAdvisor() = advisorChatClient.prompt().user("Say hello world for me").call().content()
}