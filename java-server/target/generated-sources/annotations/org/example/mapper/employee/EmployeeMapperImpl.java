package org.example.mapper.employee;

import javax.annotation.processing.Generated;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.model.employee.Employee;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-28T05:09:42+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Employee toEmployeeEntity(EmployeeRegistrationRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Employee employee = new Employee();

        employee.setId_employee( dto.getId_employee() );
        employee.setEmpl_surname( dto.getEmpl_surname() );
        employee.setEmpl_name( dto.getEmpl_name() );
        employee.setEmpl_patronymic( dto.getEmpl_patronymic() );
        employee.setRole( map( dto.getRole() ) );
        employee.setSalary( dto.getSalary() );
        employee.setDate_of_birth( dto.getDate_of_birth() );
        employee.setDate_of_start( dto.getDate_of_start() );
        employee.setPhone_number( dto.getPhone_number() );
        employee.setCity( dto.getCity() );
        employee.setStreet( dto.getStreet() );
        employee.setZip_code( dto.getZip_code() );
        employee.setPassword( dto.getPassword() );

        return employee;
    }

    @Override
    public EmployeeResponseDto toEmployeeResponseDto(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();

        employeeResponseDto.setId_employee( employee.getId_employee() );
        employeeResponseDto.setEmpl_surname( employee.getEmpl_surname() );
        employeeResponseDto.setEmpl_name( employee.getEmpl_name() );
        employeeResponseDto.setEmpl_patronymic( employee.getEmpl_patronymic() );
        employeeResponseDto.setRole( map( employee.getRole() ) );
        employeeResponseDto.setSalary( employee.getSalary() );
        employeeResponseDto.setDate_of_birth( employee.getDate_of_birth() );
        employeeResponseDto.setDate_of_start( employee.getDate_of_start() );
        employeeResponseDto.setPhone_number( employee.getPhone_number() );
        employeeResponseDto.setCity( employee.getCity() );
        employeeResponseDto.setStreet( employee.getStreet() );
        employeeResponseDto.setZip_code( employee.getZip_code() );

        return employeeResponseDto;
    }
}
