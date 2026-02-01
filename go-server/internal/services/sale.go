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
	GetAllSalesWithinDate(ctx context.Context, startDate, endDate time.Time) ([]views.SaleResponse, error)
}

type saleService struct {
	repository repository.SaleRepository
}

func NewSaleService(repository repository.SaleRepository) SaleService {
	return &saleService{
		repository: repository,
	}
}

func (s *saleService) GetAllSalesWithinDate(ctx context.Context, startDate, endDate time.Time) ([]views.SaleResponse, error) {
	sales, err := s.repository.GetAllSalesWithinDate(ctx, startDate, endDate)
	if err != nil {
		return nil, fmt.Errorf("failed to get sales: %w", err)
	}
	salesResponseList := make([]views.SaleResponse, 0, len(sales))
	for i := range sales {
		salesResponseList = append(salesResponseList, *mappers.SaleModelToResponse(&sales[i]))
	}
	return salesResponseList, nil
}
