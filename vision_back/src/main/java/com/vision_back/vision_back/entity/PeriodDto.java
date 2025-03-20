package com.vision_back.vision_back.entity;

import java.sql.Time;
import java.time.Month;
import java.time.Year;
import java.util.Date;

import javax.persistence.*;

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

public class PeriodDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_id")
    private Long periodId;

    @Column(name = "period_code")
    private Long periodCode;

    @Column(name = "period_date")
    private Date periodDate;  

    @Column(name = "period_month")
    private Month periodMonth;

    @Column(name = "period_year")
    private Year periodYear;

    @Column(name = "period_time")
    private Time periodTime;  
}