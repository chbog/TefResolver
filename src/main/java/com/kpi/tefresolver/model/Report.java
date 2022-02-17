package com.kpi.tefresolver.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Report {
    @Column
    @Id
    @GeneratedValue
    private Long id;

    @Column
//    @NotEmpty
    private String fileName;

    @Column
//    @NotNull
    private Boolean correct;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "report")
    @ToString.Exclude
    private Set<ObservationData> rows;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Report report = (Report) o;
        return id != null && Objects.equals(id, report.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
