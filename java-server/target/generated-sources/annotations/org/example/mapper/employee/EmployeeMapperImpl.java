package org.example.mapper.employee;

import javax.annotation.processing.Generated;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.model.employee.Employee;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-16T23:53:03+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
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
        employee.setSalary( dto.getSalary() );
        employee.setEmplName( dto.getEmpl_name() );
        employee.setEmplPatronymic( dto.getEmpl_patronymic() );
        employee.setDateOfBirth( dto.getDate_of_birth() );
        employee.setDateOfStart( dto.getDate_of_start() );
        employee.setPhoneNumber( dto.getPhone_number() );
        employee.setZipCode( dto.getZip_code() );
        employee.setPassword( dto.getPassword() );
        employee.setEmplSurname( dto.getEmplSurname() );
        employee.setCity( dto.getCity() );
        employee.setStreet( dto.getStreet() );

        return employee;
    }

    @Override
    public EmployeeResponseDto toEmployeeResponseDto(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();

        employeeResponseDto.setEmpl_name( employee.getEmplName() );
        employeeResponseDto.setDate_of_birth( employee.getDateOfBirth() );
        employeeResponseDto.setDate_of_start( employee.getDateOfStart() );
        employeeResponseDto.setPhone_number( employee.getPhoneNumber() );
        employeeResponseDto.setZip_code( employee.getZipCode() );
        employeeResponseDto.setIdEmployee( employee.getIdEmployee() );
        employeeResponseDto.setEmplSurname( employee.getEmplSurname() );
        employeeResponseDto.setSalary( employee.getSalary() );
        employeeResponseDto.setCity( employee.getCity() );
        employeeResponseDto.setStreet( employee.getStreet() );

        return employeeResponseDto;
    }

    @Override
    public void updateEmployeeFromDto(EmployeeRegistrationRequestDto requestDto, Employee employee) {
        if ( requestDto == null ) {
            return;
        }

        employee.setSalary( requestDto.getSalary() );
        employee.setPassword( requestDto.getPassword() );
        employee.setEmplName( requestDto.getEmpl_name() );
        employee.setEmplPatronymic( requestDto.getEmpl_patronymic() );
        employee.setDateOfBirth( requestDto.getDate_of_birth() );
        employee.setDateOfStart( requestDto.getDate_of_start() );
        employee.setPhoneNumber( requestDto.getPhone_number() );
        employee.setZipCode( requestDto.getZip_code() );
        employee.setEmplSurname( requestDto.getEmplSurname() );
        employee.setCity( requestDto.getCity() );
        employee.setStreet( requestDto.getStreet() );
    }
}
