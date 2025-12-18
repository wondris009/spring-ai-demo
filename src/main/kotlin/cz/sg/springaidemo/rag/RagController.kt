package cz.sg.springaidemo.rag

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClient

@RestController
class RagController(
    private val vectorStore: VectorStore,
    private val ragChatClient: ChatClient
) {
    data class IngestRequest(val texts: List<String>)
    data class ChatRequest(val conversationId: String, val message: String)

    // --- 1) Ingest documents into the vector DB ---
    @PostMapping("/ingest")
    fun ingest(@RequestBody req: IngestRequest): Map<String, Any> {
        val docs = req.texts.mapIndexed { idx, text ->
            Document(
                text,
                mapOf(
                    "source" to "manual",
                    "docId" to idx.toString()
                )
            )
        }
        vectorStore.add(docs)
        return mapOf("added" to docs.size)
    }

    // --- 2) Chat with RAG + memory ---
    @PostMapping("/chat")
    fun chat(@RequestBody req: ChatRequest): String? {
        return ragChatClient
            .prompt()
            .user(req.message)
            // important: this is how memory is isolated per conversation
            .advisors { it.param(ChatMemory.CONVERSATION_ID, req.conversationId) }
            .call()
            .content()
    }

    // --- 3) Debug: browse stored points via Qdrant REST API ---
    // Qdrant REST port is 6333; this lets you inspect payloads during debugging.
    @GetMapping("/debug/qdrant/points", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun debugQdrantPoints(
        @RequestParam(defaultValue = "vector_store") collection: String,
        @RequestParam(defaultValue = "10") limit: Int
    ): String {
        val client = RestClient.builder()
            .baseUrl("http://localhost:6333")
            .build()

        val body = mapOf(
            "limit" to limit,
            "with_payload" to true,
            "with_vector" to false
        )

        return client.post()
            .uri("/collections/{collection}/points/scroll", collection)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(String::class.java)!!
    }
}