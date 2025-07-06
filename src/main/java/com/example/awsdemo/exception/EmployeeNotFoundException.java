package com.example.awsdemo.exception;

public class EmployeeNotFoundException extends NotFoundException {

  public EmployeeNotFoundException(Long id) {
    super(String.format("Employee not found for given id %d", id));
  }
}
