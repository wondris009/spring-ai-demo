package cz.sg.springaidemo.voice

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.jvm.java

@RestController
class RobotController(
    private val robotAudioService: RobotAudioService
) {

    @PostMapping(
        "/audio",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = ["audio/wav"]
    )
    fun robotAudio(@RequestBody body: RobotRequest): ResponseEntity<ByteArray> {
        val wav = robotAudioService.boredRobotWav(body.text)
        return ResponseEntity.ok()
            .contentType(MediaType("audio", "wav"))
            .body(wav)
    }

    @PostMapping(
        "/speak",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun speak(@RequestBody body: RobotRequest): ResponseEntity<Void> {
        val wav = robotAudioService.boredRobotWav(body.text)
        playWavOnServer(wav)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    private fun playWavOnServer(wavBytes: ByteArray) {
        require(wavBytes.size >= 12) { "Audio payload too small: ${wavBytes.size} bytes" }

        val riff = String(wavBytes, 0, 4, Charsets.US_ASCII)
        val wave = String(wavBytes, 8, 4, Charsets.US_ASCII)
        require(riff == "RIFF" && wave == "WAVE") {
            "TTS nevrátil WAV (header: $riff/$wave, size=${wavBytes.size}). " +
                    "Zkontroluj spring.ai.openai.audio.speech.options.response-format=wav"
        }

        ByteArrayInputStream(wavBytes).use { bais ->
            AudioSystem.getAudioInputStream(bais).use { ais ->
                val format = ais.format
                val info = DataLine.Info(SourceDataLine::class.java, format)
                val line = AudioSystem.getLine(info) as SourceDataLine

                line.open(format)
                line.start()

                val buffer = ByteArray(8192)
                while (true) {
                    val read = ais.read(buffer)
                    if (read <= 0) break
                    line.write(buffer, 0, read)
                }

                line.drain()
                line.stop()
                line.close()
            }
        }
    }
}

data class RobotRequest(val text: String)