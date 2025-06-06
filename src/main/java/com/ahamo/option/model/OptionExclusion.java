package com.ahamo.option.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "option_exclusions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionExclusion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "option_id", nullable = false)
    private String optionId;
    
    @Column(name = "excluded_option_id", nullable = false)
    private String excludedOptionId;
}
