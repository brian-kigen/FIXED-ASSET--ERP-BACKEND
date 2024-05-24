package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Asset GL is required")
    private String assetGl;

    private Integer accumulatedDepreciation;

    private Integer depreciation;

    private String description;

    @Positive(message = "Useful life must be positive")
    private int usefulLife;

    @NotNull(message = "Salvage value cannot be null")
    @PositiveOrZero(message = "Salvage value must be positive or zero")
    private Integer salvageValue;

    private Integer netBookValue;

    private boolean disposed;

    @NotNull(message = "Original value cannot be null")
    @Positive(message = "Original value must be positive")
    private Integer originalValue;

    @ManyToOne
    @JoinColumn(name = "allocated_to")
    private User allocatedTo;

    private boolean received;

    // Calculate annual depreciation
    public Integer calculateAnnualDepreciation() {
        return (originalValue - salvageValue) / usefulLife;
    }

    // Calculate net book value by subtracting accumulated depreciation from original value
    public void calculateNetBookValue() {
        this.netBookValue = this.originalValue - this.accumulatedDepreciation;
    }

    // Lifecycle callback to initialize calculated fields before persisting
    @PrePersist
    @PreUpdate
    private void initializeCalculatedFields() {
        // Check if originalValue is not null before initializing calculated fields
        if (this.originalValue != null) {
            this.depreciation = calculateAnnualDepreciation();
            if (this.accumulatedDepreciation == null) {
                this.accumulatedDepreciation = 0;
            }
            calculateNetBookValue();
        }
    }

    // Override setters to automatically recalculate net book value
    public void setOriginalValue(Integer originalValue) {
        this.originalValue = originalValue;
        initializeCalculatedFields();
    }

    public void setUsefulLife(int usefulLife) {
        this.usefulLife = usefulLife;
        initializeCalculatedFields();
    }

    public void setSalvageValue(Integer salvageValue) {
        this.salvageValue = salvageValue;
        initializeCalculatedFields();
    }

    // Method to dispose the asset at salvage value
    public void disposeAtSalvageValue() {
        this.accumulatedDepreciation = this.originalValue - this.salvageValue;
        this.netBookValue = this.salvageValue;
        this.disposed = true;
    }

    // Method to calculate profit or loss upon disposal
    public Integer calculateProfitOrLoss() {
        return this.salvageValue - this.netBookValue;
    }
}
