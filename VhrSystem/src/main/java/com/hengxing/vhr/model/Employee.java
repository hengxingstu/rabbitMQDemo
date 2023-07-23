package com.hengxing.vhr.model;

import java.util.UUID;

/**
 * @author hengxing
 * @version 1.0
 * @project RabbitMQ_demo
 * @date 7/17/2023 17:31:25
 */

public class Employee {
    @
    private UUID id;
    private String name;
    private Integer gender;
    private String email;
    private Double salary;

    public Employee(UUID id, String name, Integer gender, String email, Double salary) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.salary = salary;
    }

    public Employee() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", salary=" + salary +
                '}';
    }
}
