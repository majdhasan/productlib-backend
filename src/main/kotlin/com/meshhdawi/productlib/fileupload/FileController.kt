package com.meshhdawi.productlib.fileupload

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Path
import java.nio.file.Paths

@RestController
@RequestMapping("/api/files")
class FileController {

    @GetMapping("/{filename}")
    fun getImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val filePath: Path = Paths.get("src/main/resources/static/uploads").resolve(filename)
        val resource: Resource = UrlResource(filePath.toUri())
        return if (resource.exists() || resource.isReadable) {
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
                .body(resource)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}