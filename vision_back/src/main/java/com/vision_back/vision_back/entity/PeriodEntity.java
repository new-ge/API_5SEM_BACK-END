package com.vision_back.vision_back.entity;

import jakarta.persistence.*;

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
    private Integer periodId;

    @NotNull
    @Column(name = "period_code")
    private Integer periodCode;

    @NotNull
    @Column(name = "period_date")
    private Integer periodDate;  

    @NotNull
    @Column(name = "period_month")
    private Integer periodMonth;

    @NotNull
    @Column(name = "period_year")
    private Integer periodYear;

    @NotNull
    @Column(name = "period_hour")
    private Integer periodHour;  
}