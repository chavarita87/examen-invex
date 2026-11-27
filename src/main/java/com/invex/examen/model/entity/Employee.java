package com.invex.examen.model.entity;

import com.invex.examen.constant.EmployeeConstants;
import com.invex.examen.model.enums.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Employee registered in the system.
 */
@Entity
@Table(name = EmployeeConstants.TABLE_NAME,
        indexes = {
            @Index(name = "idx_employees_first_name", columnList = "first_name"),
            @Index(name = "idx_employees_paternal_last_name", columnList = "paternal_last_name")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = EmployeeConstants.NAME_MAX_LENGTH)
    private String firstName;

    @Column(name = "middle_name", length = EmployeeConstants.NAME_MAX_LENGTH)
    private String middleName;

    @Column(name = "paternal_last_name", nullable = false, length = EmployeeConstants.NAME_MAX_LENGTH)
    private String paternalLastName;

    @Column(name = "maternal_last_name", length = EmployeeConstants.NAME_MAX_LENGTH)
    private String maternalLastName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = EmployeeConstants.GENDER_MAX_LENGTH)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "position", nullable = false, length = EmployeeConstants.POSITION_MAX_LENGTH)
    private String position;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Employee)) {
            return false;
        }
        Employee employee = (Employee) other;
        return id != null && Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
