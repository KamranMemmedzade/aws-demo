package com.example.awsdemo.service;

import com.example.awsdemo.dao.repository.EmployeeRepository;
import com.example.awsdemo.exception.EmployeeNotFoundException;
import com.example.awsdemo.mapper.EmployeeMapper;
import com.example.awsdemo.model.EmployeeDetailsDto;
import com.example.awsdemo.model.EmployeeDetailsView;
import com.example.awsdemo.model.EmployeeRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;


  public EmployeeDetailsView getAllEmployees() {
    var employees =
        employeeMapper.mapEmployeeEntityListToDetailsViewList(employeeRepository.findAll());

    return EmployeeDetailsView.builder()
        .employeeCount(employees.size())
        .employees(employees)
        .build();
  }

  public EmployeeDetailsDto getEmployeeById(Long id) {
    var employee = employeeRepository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));
    return employeeMapper.mapEmployeeEntityToDetailsView(employee);
  }


  public EmployeeDetailsDto registerEmployee(
      EmployeeRegistrationRequest employeeRegistrationRequest) {
    var newEmployee= employeeMapper.mapRequestToEntity(employeeRegistrationRequest);
    var savedEmployee = employeeRepository.save(newEmployee);
    return employeeMapper.mapEmployeeEntityToDetailsView(savedEmployee);
  }
}
