package services

import (
	"context"

	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type EmployeeService interface {
	GetEmployeeIDList(
		ctx context.Context,
	) ([]views.DropdownEmployeeItem, error)
}

type employeeService struct {
	employeeRepository repository.EmployeeRepository
}

func NewEmployeeService(employeeRepository repository.EmployeeRepository) EmployeeService {
	return &employeeService{
		employeeRepository: employeeRepository,
	}
}

func (s employeeService) GetEmployeeIDList(ctx context.Context) ([]views.DropdownEmployeeItem, error) {
	items, err := s.employeeRepository.GetEmployeeIDList(ctx)
	if err != nil {
		return nil, err
	}
	response := make([]views.DropdownEmployeeItem, 0, len(items))
	for i := range items {
		response = append(response, views.DropdownEmployeeItem{
			IDEmployee: items[i].IDEmployee,
			FullName:   items[i].FullName,
		})
	}
	return response, err
}
