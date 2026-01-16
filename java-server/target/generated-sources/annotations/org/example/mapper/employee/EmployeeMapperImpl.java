package org.example.mapper.employee;

import javax.annotation.processing.Generated;
import org.example.dto.employee.registration.EmployeeRegistrationRequestDto;
import org.example.dto.employee.registration.EmployeeResponseDto;
import org.example.model.employee.Employee;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-16T15:20:05+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public EmployeeResponseDto toEmployeeResponseDto(Employee employee) {
        if ( employee == null ) {
            return null;
        }

        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();

        if ( employee.getEmplSurname() != null ) {
            employeeResponseDto.setEmplSurname( employee.getEmplSurname() );
        }
        if ( employee.getEmpl_name() != null ) {
            employeeResponseDto.setEmpl_name( employee.getEmpl_name() );
        }
        if ( employee.getDate_of_birth() != null ) {
            employeeResponseDto.setDate_of_birth( employee.getDate_of_birth() );
        }
        if ( employee.getDate_of_start() != null ) {
            employeeResponseDto.setDate_of_start( employee.getDate_of_start() );
        }
        if ( employee.getPhone_number() != null ) {
            employeeResponseDto.setPhone_number( employee.getPhone_number() );
        }
        if ( employee.getCity() != null ) {
            employeeResponseDto.setCity( employee.getCity() );
        }
        if ( employee.getStreet() != null ) {
            employeeResponseDto.setStreet( employee.getStreet() );
        }
        if ( employee.getZip_code() != null ) {
            employeeResponseDto.setZip_code( employee.getZip_code() );
        }

        return employeeResponseDto;
    }

    @Override
    public Employee toEmployeeEntity(EmployeeRegistrationRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        Employee employee = new Employee();

        if ( requestDto.getEmplSurname() != null ) {
            employee.setEmplSurname( requestDto.getEmplSurname() );
        }
        if ( requestDto.getEmpl_name() != null ) {
            employee.setEmpl_name( requestDto.getEmpl_name() );
        }
        if ( requestDto.getEmpl_patronymic() != null ) {
            employee.setEmpl_patronymic( requestDto.getEmpl_patronymic() );
        }
        if ( requestDto.getDate_of_birth() != null ) {
            employee.setDate_of_birth( requestDto.getDate_of_birth() );
        }
        if ( requestDto.getDate_of_start() != null ) {
            employee.setDate_of_start( requestDto.getDate_of_start() );
        }
        if ( requestDto.getPhone_number() != null ) {
            employee.setPhone_number( requestDto.getPhone_number() );
        }
        if ( requestDto.getCity() != null ) {
            employee.setCity( requestDto.getCity() );
        }
        if ( requestDto.getStreet() != null ) {
            employee.setStreet( requestDto.getStreet() );
        }
        if ( requestDto.getZip_code() != null ) {
            employee.setZip_code( requestDto.getZip_code() );
        }
        if ( requestDto.getPassword() != null ) {
            employee.setPassword( requestDto.getPassword() );
        }

        return employee;
    }
}
