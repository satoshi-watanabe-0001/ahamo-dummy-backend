package com.ahamo.contract.service;

public interface DocumentStorageService {

    String storeDocument(String documentId, byte[] content, String format);

    byte[] retrieveDocument(String documentId);

    String generateAccessUrl(String documentId, int expirationHours);

    void deleteDocument(String documentId);

    boolean documentExists(String documentId);
}
