package com.meshhdawi.productlib.fileupload

import com.meshhdawi.productlib.AppProperties
import net.coobird.thumbnailator.Thumbnails
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

@Service
class FileStorageService(appProperties: AppProperties) {

    private val uploadDir = appProperties.fileStorage.uploadDir

    init {
        val path = Paths.get(uploadDir)
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
    }

    fun storeImageVariations(file: MultipartFile): String {
        val fileName = "${Instant.now().toEpochMilli()}_${file.originalFilename ?: throw IllegalArgumentException("Invalid file name")}"
        val filePath = Paths.get(uploadDir, fileName)
        val resizedFilePath = Paths.get(uploadDir, "resized_".plus(fileName))
        val thumbnailFilePath = Paths.get(uploadDir, "thumbnail_".plus(fileName))

        // Save the original file
        file.transferTo(filePath)

        // Resize the image to a width of 500 pixels while maintaining the aspect ratio
        Thumbnails.of(filePath.toFile())
            .width(500)
            .toFile(resizedFilePath.toFile())

        // Create a thumbnail with size 80x80 pixels
        Thumbnails.of(filePath.toFile())
            .size(200, 200)
            .toFile(thumbnailFilePath.toFile())

        return fileName
    }
}