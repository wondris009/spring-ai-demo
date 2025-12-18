package cz.sg.springaidemo.basic

import org.springframework.ai.image.ImageModel
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageModel: ImageModel
) {

//    data class ImageRequest(
//        val prompt: String,
//        val size: String? = "1024x1024" // e.g. "256x256", "512x512", "1024x1024"
//    )
//
//    data class ImageResponse(
//        val prompt: String,
//        val urls: List<String>
//    )
//
//    @PostMapping
//    fun generate(@RequestBody req: ImageRequest): ResponseEntity<ImageResponse> {
//        val options = ImageOptionsBuilder.builder()
//            .withN(1)
//            .withSize(req.size ?: "1024x1024")
//            .build()
//
//        val response = imageModel.call(req.prompt, options)
//
//        // Spring AI returns Image objects with a URL (commonly) or B64 depending on provider/options.
//        val urls = response.results
//            .mapNotNull { it.output.url }   // if URL is provided
//            .filter { it.isNotBlank() }
//
//        return ResponseEntity.ok(ImageResponse(req.prompt, urls))
//    }
}