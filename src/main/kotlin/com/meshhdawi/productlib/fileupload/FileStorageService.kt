package com.meshhdawi.productlib.fileupload

import com.meshhdawi.productlib.AppProperties
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

    fun storeFile(file: MultipartFile): String {
        val fileName = "${Instant.now().toEpochMilli()}_${file.originalFilename ?: throw IllegalArgumentException("Invalid file name")}"
        val filePath = Paths.get(uploadDir, fileName)
        file.transferTo(filePath)
        return fileName
    }
}