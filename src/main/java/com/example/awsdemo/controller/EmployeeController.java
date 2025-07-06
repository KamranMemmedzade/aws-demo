package com.example.awsdemo.controller;

import com.example.awsdemo.model.EmployeeDetailsDto;
import com.example.awsdemo.model.EmployeeDetailsView;
import com.example.awsdemo.model.EmployeeRegistrationRequest;
import com.example.awsdemo.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/employee", produces = "application/json")
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<EmployeeDetailsView> getAllEmployees() {
    return ResponseEntity.ok(employeeService.getAllEmployees());
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDetailsDto> getEmployeeById(@PathVariable Long id) {
    return ResponseEntity.ok(employeeService.getEmployeeById(id));
  }

  @PostMapping
  public ResponseEntity<EmployeeDetailsDto> registerEmployee(
      @RequestBody EmployeeRegistrationRequest employeeRegistrationRequest) {
    return ResponseEntity.ok(employeeService.registerEmployee(employeeRegistrationRequest));
  }

}
