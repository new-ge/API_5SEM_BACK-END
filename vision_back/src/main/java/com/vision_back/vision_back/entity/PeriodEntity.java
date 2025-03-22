package com.vision_back.vision_back.entity;

import javax.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="period")

public class PeriodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_id")
    private Long periodId;

    @NotNull
    @Column(name = "period_code")
    private Long periodCode;

    @NotNull
    @Column(name = "period_date")
    private Long periodDate;  

    @NotNull
    @Column(name = "period_month")
    private Long periodMonth;

    @NotNull
    @Column(name = "period_year")
    private Long periodYear;

    @NotNull
    @Column(name = "period_hour")
    private Long periodHour;  
}