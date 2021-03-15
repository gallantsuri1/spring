package com.example.spring;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.Objects;

@RestController
public class EmployeeController {
    @Autowired
    private EmployeeRepository repository;

    @GetMapping("/employees")
    List<Employee> all() {
        return repository.findAll();
    }

    @GetMapping("/employees/{id}")
        //Optional<Employee> get(@PathVariable Long id) {
    Employee get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        //return repository.findById(id);
    }

    @PostMapping("/employees")
    Employee add(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    @PutMapping("/employees/{id}")
    Employee update(@RequestBody Employee employee, @PathVariable Long id) {
        return repository.findById(id).map(
                e -> {
                    e.setName(employee.getName());
                    e.setRole(employee.getRole());
                    return repository.save(e);
                }
        ).orElseGet(() -> {
            employee.setId(id);
            return repository.save(employee);
        });
    }

    @DeleteMapping("/employees/{id}")
    void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }

}

@ControllerAdvice
class EmployeeNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(EmployeeNotFoundException e) {
        return e.getMessage();
    }
}

class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long id) {
        super("Could not find employee " + id);
    }
}

interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

@Configuration
class LoadEmployeeDb {
    private static final Logger log = LoggerFactory.getLogger(LoadEmployeeDb.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Employee("Test User1", "Role 1")));
            log.info("Preloading " + repository.save(new Employee("Test User2", "Role 2")));
        };
    }
}

@Entity
class Employee {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String role;

    Employee() {
    }

    Employee(String name, String role) {
        this.name = name;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) &&
                Objects.equals(name, employee.name) &&
                Objects.equals(role, employee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

