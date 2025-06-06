package com.ahamo.option.repository;

import com.ahamo.option.model.OptionDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionDependencyRepository extends JpaRepository<OptionDependency, Long> {
    
    List<OptionDependency> findByOptionId(String optionId);
    
    @Query("SELECT od.requiredOptionId FROM OptionDependency od WHERE od.optionId = :optionId")
    List<String> findRequiredOptionIdsByOptionId(@Param("optionId") String optionId);
    
    void deleteByOptionId(String optionId);
}
