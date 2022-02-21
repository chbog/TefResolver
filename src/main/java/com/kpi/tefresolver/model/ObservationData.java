package com.kpi.tefresolver.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ObservationData {
    @Column
    @Id
    @GeneratedValue
    private Long id;

    @Column
//    @Size(min = 1, max = 31)
    private Short dayNumber;

    @Column
//    @Temporal(TemporalType.TIME)
    private LocalTime observationTime;

    @Column
//    @Min(-273)
    private Short temperature;

    @Column
//    @Enumerated(EnumType.STRING)
//    private WindDirection windDirection;
    private String windDirection;

    @Column
    private Integer windSpeed;

    @ManyToOne
    @JoinColumn(name = "report_id")
    @JsonIgnore
    private Report report;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ObservationData that = (ObservationData) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
