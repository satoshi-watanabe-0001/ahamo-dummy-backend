package com.ahamo.contract.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "contract_template_variables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractTemplateVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private ContractTemplate template;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private VariableType type;

    @Column(name = "required")
    private Boolean required;

    @Column(name = "description")
    private String description;

    public enum VariableType {
        STRING, NUMBER, DATE, BOOLEAN
    }
}
