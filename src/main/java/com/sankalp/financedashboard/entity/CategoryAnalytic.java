package com.sankalp.financedashboard.entity;

import com.sankalp.financedashboard.dto.category.CategoryAnalyticDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryAnalytic {
    private Category category;

    private Double amount;

    private Long numberOfRecords;


    /**
     * Create data transfer object.
     * @return category analytic dto
     */
    public CategoryAnalyticDto dto() {
        return CategoryAnalyticDto.builder()
                .category(category.dto())
                .amount(amount)
                .numberOfRecords(numberOfRecords)
                .build();
    }
}
