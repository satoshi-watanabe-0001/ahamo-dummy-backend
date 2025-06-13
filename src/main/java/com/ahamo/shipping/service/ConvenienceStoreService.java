package com.ahamo.shipping.service;

import com.ahamo.shipping.model.ConvenienceStore;
import com.ahamo.shipping.repository.ConvenienceStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConvenienceStoreService {

    @Autowired
    private ConvenienceStoreRepository convenienceStoreRepository;

    public List<ConvenienceStore> searchStores(String prefecture, String city, String postalCode, 
                                             Double latitude, Double longitude, Double radiusKm) {
        if (latitude != null && longitude != null && radiusKm != null) {
            return convenienceStoreRepository.findByLocationWithinRadius(latitude, longitude, radiusKm);
        } else if (postalCode != null && !postalCode.trim().isEmpty()) {
            return convenienceStoreRepository.findByPostalCodeAndIsActiveTrue(postalCode);
        } else {
            return convenienceStoreRepository.findByLocationCriteria(prefecture, city, postalCode);
        }
    }

    public ConvenienceStore findByStoreCode(String storeCode) {
        return convenienceStoreRepository.findByStoreCode(storeCode).orElse(null);
    }

    public List<ConvenienceStore> findAll() {
        return convenienceStoreRepository.findAll();
    }

    public List<ConvenienceStore> findByPrefectureAndCity(String prefecture, String city) {
        return convenienceStoreRepository.findByPrefectureAndCityAndIsActiveTrue(prefecture, city);
    }

    public List<ConvenienceStore> findByPostalCode(String postalCode) {
        return convenienceStoreRepository.findByPostalCodeAndIsActiveTrue(postalCode);
    }
}
