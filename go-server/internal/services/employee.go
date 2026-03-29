package services

import (
	"context"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
)

type EmployeeService interface {
	GetEmployeeIDList(
		ctx context.Context,
	) ([]string, error)
}

type employeeService struct {
	employeeRepository repository.EmployeeRepository
}

func NewEmployeeService(employeeRepository repository.EmployeeRepository) EmployeeService {
	return &employeeService{
		employeeRepository: employeeRepository,
	}
}

func (s employeeService) GetEmployeeIDList(ctx context.Context) ([]string, error) {
	return s.employeeRepository.GetEmployeeIDList(ctx)
}
