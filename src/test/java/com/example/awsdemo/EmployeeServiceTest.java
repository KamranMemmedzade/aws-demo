package com.example.awsdemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.awsdemo.dao.entity.EmployeeEntity;
import com.example.awsdemo.dao.repository.EmployeeRepository;
import com.example.awsdemo.exception.EmployeeNotFoundException;
import com.example.awsdemo.mapper.EmployeeMapper;
import com.example.awsdemo.model.EmployeeDetailsDto;
import com.example.awsdemo.model.EmployeeDetailsView;
import com.example.awsdemo.model.EmployeeRegistrationRequest;
import com.example.awsdemo.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private EmployeeMapper employeeMapper;

  @InjectMocks
  private EmployeeService employeeService;

  private EmployeeEntity employee1;
  private EmployeeEntity employee2;
  private EmployeeDetailsDto employeeDetailsDto1;
  private EmployeeDetailsDto employeeDetailsDto2;
  private EmployeeRegistrationRequest registrationRequest;

  @BeforeEach
  void setUp() {
    // Setup test data
    employee1 = new EmployeeEntity();
    employee1.setId(1L);
    employee1.setName("John Doe");

    employee2 = new EmployeeEntity();
    employee2.setId(2L);
    employee2.setName("Jane Smith");

    employeeDetailsDto1 = EmployeeDetailsDto.builder()
        .id(1L)
        .name("John Doe")
        .build();

    employeeDetailsDto2 = EmployeeDetailsDto.builder()
        .id(2L)
        .name("Jane Smith")
        .build();

    registrationRequest = EmployeeRegistrationRequest.builder()
        .name("New Employee")
        .build();
  }

  @Test
  void getAllEmployees_ShouldReturnEmployeeDetailsView_WhenEmployeesExist() {
    // Given
    List<EmployeeEntity> employees = Arrays.asList(employee1, employee2);
    List<EmployeeDetailsDto> employeeDtos =
        Arrays.asList(employeeDetailsDto1, employeeDetailsDto2);

    when(employeeRepository.findAll()).thenReturn(employees);
    when(employeeMapper.mapEmployeeEntityListToDetailsViewList(employees)).thenReturn(
        employeeDtos);

    // When
    EmployeeDetailsView result = employeeService.getAllEmployees();

    // Then
    assertNotNull(result);
    assertEquals(2, result.getEmployeeCount());
    assertEquals(employeeDtos, result.getEmployees());

    verify(employeeRepository, times(1)).findAll();
    verify(employeeMapper, times(1)).mapEmployeeEntityListToDetailsViewList(employees);
  }

  @Test
  void getAllEmployees_ShouldReturnEmptyView_WhenNoEmployeesExist() {
    // Given
    List<EmployeeEntity> emptyEmployees = Arrays.asList();
    List<EmployeeDetailsDto> emptyDtos = Arrays.asList();

    when(employeeRepository.findAll()).thenReturn(emptyEmployees);
    when(employeeMapper.mapEmployeeEntityListToDetailsViewList(emptyEmployees)).thenReturn(
        emptyDtos);

    // When
    EmployeeDetailsView result = employeeService.getAllEmployees();

    // Then
    assertNotNull(result);
    assertEquals(0, result.getEmployeeCount());
    assertTrue(result.getEmployees().isEmpty());

    verify(employeeRepository, times(1)).findAll();
    verify(employeeMapper, times(1)).mapEmployeeEntityListToDetailsViewList(emptyEmployees);
  }

  @Test
  void getEmployeeById_ShouldReturnEmployeeDetailsDto_WhenEmployeeExists() {
    // Given
    Long employeeId = 1L;
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee1));
    when(employeeMapper.mapEmployeeEntityToDetailsView(employee1)).thenReturn(
        employeeDetailsDto1);

    // When
    EmployeeDetailsDto result = employeeService.getEmployeeById(employeeId);

    // Then
    assertNotNull(result);
    assertEquals(employeeDetailsDto1, result);

    verify(employeeRepository, times(1)).findById(employeeId);
    verify(employeeMapper, times(1)).mapEmployeeEntityToDetailsView(employee1);
  }

  @Test
  void getEmployeeById_ShouldThrowEmployeeNotFoundException_WhenEmployeeDoesNotExist() {
    // Given
    Long employeeId = 999L;
    when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

    // When & Then
    EmployeeNotFoundException exception = assertThrows(
        EmployeeNotFoundException.class,
        () -> employeeService.getEmployeeById(employeeId)
    );

    assertEquals("Employee not found for given id 999", exception.getMessage());
    verify(employeeRepository, times(1)).findById(employeeId);
    verify(employeeMapper, never()).mapEmployeeEntityToDetailsView(any());
  }

  @Test
  void registerEmployee_ShouldReturnEmployeeDetailsDto_WhenValidRequest() {
    // Given
    EmployeeEntity newEmployee = new EmployeeEntity();
    newEmployee.setName("New Employee");

    EmployeeEntity savedEmployee = new EmployeeEntity();
    savedEmployee.setId(3L);
    savedEmployee.setName("New Employee");

    EmployeeDetailsDto expectedDto = EmployeeDetailsDto.builder()
        .id(3L)
        .name("New Employee")
        .build();

    when(employeeMapper.mapRequestToEntity(registrationRequest)).thenReturn(newEmployee);
    when(employeeRepository.save(newEmployee)).thenReturn(savedEmployee);
    when(employeeMapper.mapEmployeeEntityToDetailsView(savedEmployee)).thenReturn(expectedDto);

    // When
    EmployeeDetailsDto result = employeeService.registerEmployee(registrationRequest);

    // Then
    assertNotNull(result);
    assertEquals(expectedDto, result);

    verify(employeeMapper, times(1)).mapRequestToEntity(registrationRequest);
    verify(employeeRepository, times(1)).save(newEmployee);
    verify(employeeMapper, times(1)).mapEmployeeEntityToDetailsView(savedEmployee);
  }

  @Test
  void registerEmployee_ShouldPropagateException_WhenRepositorySaveFails() {
    // Given
    EmployeeEntity newEmployee = new EmployeeEntity();
    when(employeeMapper.mapRequestToEntity(registrationRequest)).thenReturn(newEmployee);
    when(employeeRepository.save(newEmployee)).thenThrow(new RuntimeException("Database error"));

    // When & Then
    RuntimeException exception = assertThrows(
        RuntimeException.class,
        () -> employeeService.registerEmployee(registrationRequest)
    );

    assertEquals("Database error", exception.getMessage());
    verify(employeeMapper, times(1)).mapRequestToEntity(registrationRequest);
    verify(employeeRepository, times(1)).save(newEmployee);
    verify(employeeMapper, never()).mapEmployeeEntityToDetailsView(any());
  }

  @Test
  void registerEmployee_ShouldPropagateException_WhenMapperFails() {
    // Given
    when(employeeMapper.mapRequestToEntity(registrationRequest))
        .thenThrow(new RuntimeException("Mapping error"));

    // When & Then
    RuntimeException exception = assertThrows(
        RuntimeException.class,
        () -> employeeService.registerEmployee(registrationRequest)
    );

    assertEquals("Mapping error", exception.getMessage());
    verify(employeeMapper, times(1)).mapRequestToEntity(registrationRequest);
    verify(employeeRepository, never()).save(any());
    verify(employeeMapper, never()).mapEmployeeEntityToDetailsView(any());
  }
}