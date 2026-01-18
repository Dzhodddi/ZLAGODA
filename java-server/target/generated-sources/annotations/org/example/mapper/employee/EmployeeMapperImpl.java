package org.example.mapper.employee;

import javax.annotation.processing.Generated;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.model.employee.Employee;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T01:25:29+0200",
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

        employee.setIdEmployee( dto.getIdEmployee() );
        employee.setEmplSurname( dto.getEmplSurname() );
        employee.setEmplName( dto.getEmplName() );
        employee.setEmplPatronymic( dto.getEmplPatronymic() );
        employee.setRole( map( dto.getRole() ) );
        employee.setSalary( dto.getSalary() );
        employee.setDateOfBirth( dto.getDateOfBirth() );
        employee.setDateOfStart( dto.getDateOfStart() );
        employee.setPhoneNumber( dto.getPhoneNumber() );
        employee.setCity( dto.getCity() );
        employee.setStreet( dto.getStreet() );
        employee.setZipCode( dto.getZipCode() );
        employee.setPassword( dto.getPassword() );

        return employee;
    }

    @Override
    public EmployeeResponseDto toEmployeeResponseDto(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();

        employeeResponseDto.setIdEmployee( employee.getIdEmployee() );
        employeeResponseDto.setEmplSurname( employee.getEmplSurname() );
        employeeResponseDto.setEmplName( employee.getEmplName() );
        employeeResponseDto.setEmplPatronymic( employee.getEmplPatronymic() );
        employeeResponseDto.setRole( map( employee.getRole() ) );
        employeeResponseDto.setSalary( employee.getSalary() );
        employeeResponseDto.setDateOfBirth( employee.getDateOfBirth() );
        employeeResponseDto.setDateOfStart( employee.getDateOfStart() );
        employeeResponseDto.setPhoneNumber( employee.getPhoneNumber() );
        employeeResponseDto.setCity( employee.getCity() );
        employeeResponseDto.setStreet( employee.getStreet() );
        employeeResponseDto.setZipCode( employee.getZipCode() );

        return employeeResponseDto;
    }
}
