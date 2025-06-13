package com.ahamo.shipping.repository;

import com.ahamo.shipping.model.ConvenienceStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConvenienceStoreRepository extends JpaRepository<ConvenienceStore, Long> {

    Optional<ConvenienceStore> findByStoreCode(String storeCode);

    List<ConvenienceStore> findByPrefectureAndCityAndIsActiveTrue(String prefecture, String city);

    List<ConvenienceStore> findByPostalCodeAndIsActiveTrue(String postalCode);

    @Query("SELECT c FROM ConvenienceStore c WHERE c.isActive = true " +
           "AND (:prefecture IS NULL OR c.prefecture = :prefecture) " +
           "AND (:city IS NULL OR c.city = :city) " +
           "AND (:postalCode IS NULL OR c.postalCode = :postalCode)")
    List<ConvenienceStore> findByLocationCriteria(
        @Param("prefecture") String prefecture,
        @Param("city") String city,
        @Param("postalCode") String postalCode
    );

    @Query(value = "SELECT * FROM convenience_stores c WHERE c.is_active = true " +
                   "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(c.latitude)) * " +
                   "cos(radians(c.longitude) - radians(:longitude)) + " +
                   "sin(radians(:latitude)) * sin(radians(c.latitude)))) <= :radiusKm " +
                   "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(c.latitude)) * " +
                   "cos(radians(c.longitude) - radians(:longitude)) + " +
                   "sin(radians(:latitude)) * sin(radians(c.latitude))))",
           nativeQuery = true)
    List<ConvenienceStore> findByLocationWithinRadius(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radiusKm") Double radiusKm
    );
}
