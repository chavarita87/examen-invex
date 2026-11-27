package com.invex.examen.repository;

import com.invex.examen.model.entity.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Data access for {@link Employee}.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Case-insensitive partial search on first name, middle name and both last names.
     *
     * @param name text to look for
     * @return employees whose name contains the text, ordered by last name and first name
     */
    @Query("SELECT e FROM Employee e "
            + "WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "OR LOWER(e.middleName) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "OR LOWER(e.paternalLastName) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "OR LOWER(e.maternalLastName) LIKE LOWER(CONCAT('%', :name, '%')) "
            + "ORDER BY e.paternalLastName, e.firstName")
    List<Employee> searchByName(@Param("name") String name);
}
