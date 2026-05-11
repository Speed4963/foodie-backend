package com.eatproject.backend.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

@Component
@Log4j2
@RequiredArgsConstructor
public class CommonUtil {

    @Value("${image.upload-dir}")
    private String uploadDir;

    private final MessageSource messageSource;

    public String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }

    public void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> log.error(error.getDefaultMessage()));
            throw new RuntimeException("입력값이 올바르지 않습니다.");
        }
    }

    public void saveFile(MultipartFile file, String uuid) throws Exception {
        Path folderPath = Paths.get(uploadDir);
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }
        Path uuidPath = folderPath.resolve(uuid);
        file.transferTo(uuidPath);
    }

    public void deleteFile(String uuid) {
        Path uuidPath = Paths.get(uploadDir).resolve(uuid);
        try {
            Files.deleteIfExists(uuidPath);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", uuid);
        }
    }

    public String generateUrl(String domainPath, String uuid) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/download/" + domainPath + "/{uuid}")
                .buildAndExpand(uuid)
                .toUriString();
    }
}
