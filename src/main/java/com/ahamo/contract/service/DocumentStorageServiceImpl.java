package com.ahamo.contract.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentStorageServiceImpl implements DocumentStorageService {

    @Value("${contract.document.storage.path:/tmp/contract-documents}")
    private String storagePath;

    @Value("${contract.document.base-url:http://localhost:3000/documents}")
    private String baseUrl;

    @Override
    public String storeDocument(String documentId, byte[] content, String format) {
        log.info("Storing document with ID: {}", documentId);
        
        try {
            Path storageDir = Paths.get(storagePath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            String fileName = documentId + "." + format.toLowerCase();
            Path filePath = storageDir.resolve(fileName);
            
            Files.write(filePath, content);
            
            String accessUrl = baseUrl + "/" + fileName;
            log.info("Document stored successfully: {}", accessUrl);
            
            return accessUrl;
        } catch (IOException e) {
            log.error("Failed to store document: {}", documentId, e);
            throw new RuntimeException("Document storage failed", e);
        }
    }

    @Override
    public byte[] retrieveDocument(String documentId) {
        log.info("Retrieving document with ID: {}", documentId);
        
        try {
            Path filePath = findDocumentPath(documentId);
            if (filePath == null) {
                throw new IllegalArgumentException("Document not found: " + documentId);
            }
            
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to retrieve document: {}", documentId, e);
            throw new RuntimeException("Document retrieval failed", e);
        }
    }

    @Override
    public String generateAccessUrl(String documentId, int expirationHours) {
        log.info("Generating access URL for document: {}", documentId);
        
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(expirationHours);
        
        return baseUrl + "/" + documentId + "?token=" + token + "&expires=" + 
               expiresAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public void deleteDocument(String documentId) {
        log.info("Deleting document with ID: {}", documentId);
        
        try {
            Path filePath = findDocumentPath(documentId);
            if (filePath != null && Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Document deleted successfully: {}", documentId);
            }
        } catch (IOException e) {
            log.error("Failed to delete document: {}", documentId, e);
            throw new RuntimeException("Document deletion failed", e);
        }
    }

    @Override
    public boolean documentExists(String documentId) {
        Path filePath = findDocumentPath(documentId);
        return filePath != null && Files.exists(filePath);
    }

    private Path findDocumentPath(String documentId) {
        try {
            Path storageDir = Paths.get(storagePath);
            if (!Files.exists(storageDir)) {
                return null;
            }

            return Files.list(storageDir)
                .filter(path -> path.getFileName().toString().startsWith(documentId + "."))
                .findFirst()
                .orElse(null);
        } catch (IOException e) {
            log.error("Failed to search for document: {}", documentId, e);
            return null;
        }
    }
}
