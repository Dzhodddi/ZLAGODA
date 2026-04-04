package services

import (
	"context"
	"fmt"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type SaleService interface {
	GetAllSalesWithinDate(ctx context.Context, startDate, endDate time.Time, params views.SaleListQueryParams) ([]views.SaleResponse, error)
}

type saleService struct {
	repository repository.SaleRepository
}

func NewSaleService(repository repository.SaleRepository) SaleService {
	return &saleService{
		repository: repository,
	}
}

func (s *saleService) GetAllSalesWithinDate(
	ctx context.Context,
	startDate, endDate time.Time,
	params views.SaleListQueryParams,
) ([]views.SaleResponse, error) {
	if params.LastCheckNumber == nil {
		params.LastCheckNumber = new(string)
	}
	if params.LastUPC == nil {
		params.LastUPC = new(string)
	}
	sales, err := s.repository.GetAllSalesWithinDate(ctx, startDate, endDate, *params.LastCheckNumber, *params.LastUPC)
	if err != nil {
		return nil, fmt.Errorf("failed to get sales: %w", err)
	}
	salesResponseList := make([]views.SaleResponse, 0, len(sales))
	for i := range sales {
		salesResponseList = append(salesResponseList, *mappers.SaleModelToResponse(&sales[i]))
	}
	return salesResponseList, nil
}
