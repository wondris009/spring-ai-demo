package cz.sg.springaidemo.advisor

import mu.KotlinLogging
import org.springframework.ai.chat.client.ChatClientRequest
import org.springframework.ai.chat.client.ChatClientResponse
import org.springframework.ai.chat.client.advisor.api.CallAdvisor
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain

class LoggingAdvisor : CallAdvisor {

    override fun adviseCall(
        chatClientRequest: ChatClientRequest,
        callAdvisorChain: CallAdvisorChain
    ): ChatClientResponse {
        logger.info { "Before calling LLM" }
        val response = callAdvisorChain.nextCall(chatClientRequest)
        logger.info { "After calling LLM" }
        return response
    }

    override fun getName(): String = this.javaClass.simpleName

    //lowest number => highest prio
    override fun getOrder() = 0

    private val logger = KotlinLogging.logger {}
}