package com.example.awsdemo.mapper;

import com.example.awsdemo.dao.entity.EmployeeEntity;
import com.example.awsdemo.model.EmployeeDetailsDto;
import com.example.awsdemo.model.EmployeeRegistrationRequest;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmployeeMapper {

  EmployeeDetailsDto mapEmployeeEntityToDetailsView(EmployeeEntity employeeEntity);

  List<EmployeeDetailsDto> mapEmployeeEntityListToDetailsViewList(
      List<EmployeeEntity> employeeEntities);

  EmployeeEntity mapRequestToEntity(EmployeeRegistrationRequest employeeRegistrationRequest);
}
