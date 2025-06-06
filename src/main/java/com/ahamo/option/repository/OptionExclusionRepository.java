package com.ahamo.option.repository;

import com.ahamo.option.model.OptionExclusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionExclusionRepository extends JpaRepository<OptionExclusion, Long> {
    
    List<OptionExclusion> findByOptionId(String optionId);
    
    @Query("SELECT oe.excludedOptionId FROM OptionExclusion oe WHERE oe.optionId = :optionId")
    List<String> findExcludedOptionIdsByOptionId(@Param("optionId") String optionId);
    
    void deleteByOptionId(String optionId);
}
