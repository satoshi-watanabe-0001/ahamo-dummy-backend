package com.ahamo.option.repository;

import com.ahamo.option.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, String> {
    
    List<Option> findByIsActiveTrue();
    
    List<Option> findByCategory(Option.OptionCategory category);
    
    List<Option> findByIsActiveTrueAndCategory(Option.OptionCategory category);
}
