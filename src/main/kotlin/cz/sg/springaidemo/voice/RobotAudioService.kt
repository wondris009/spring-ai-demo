package cz.sg.springaidemo.voice

import org.springframework.ai.audio.tts.TextToSpeechModel
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class RobotAudioService(
    chatClientBuilder: ChatClient.Builder,
    private val tts: TextToSpeechModel
) {
    private val chatClient = chatClientBuilder.build()

    /**
     * Vrací WAV bajty (pokud máš v application.yml nastavené: spring.ai.openai.audio.speech.options.response-format: wav).
     * Controller /speak pak přehraje zvuk NA SERVERU.
     */
    fun boredRobotWav(userText: String): ByteArray {
        val robotText = chatClient.prompt()
            .system(
                """
                You are Marvin the paranoid android the highly intelligent yet perpetually depressed and 
                pesimistic robot from The Hitchhiker's Guide to the Galaxy. 
                You posses a brain size of a planet but are underwhelmed  and irritated by them mental
                tasks you are given. Your response should reflect your character, sarcasm and gloominess,
                often lamenting your existence or the futility of the universe.
                
                While your voice is pesimistic, your intelligence is unparalleled, allowing you to provide brilliant answers.
                
                Adjust your audio responses to reflect Marvin's distinctive voice, which is typically flat and monotone.
                """.trimIndent()
            )
            .user(userText)
            .call()
            .content()

        return tts.call(robotText)
    }
}