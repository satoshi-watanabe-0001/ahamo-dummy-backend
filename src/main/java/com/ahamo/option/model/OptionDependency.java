package com.ahamo.option.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "option_dependencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDependency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "option_id", nullable = false)
    private String optionId;
    
    @Column(name = "required_option_id", nullable = false)
    private String requiredOptionId;
}
