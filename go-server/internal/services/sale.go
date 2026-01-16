// create sale services bsae on check.go
package services

import (
	"context"

	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/repository"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type SaleService struct {
	repository *repository.SaleRepository
}

func NewSaleService(repository *repository.SaleRepository) *SaleService {
	return &SaleService{repository: repository}
}

func (s *SaleService) CreateNewSale(ctx context.Context, sale views.CreateNewSale) (*generated.Sale, error) {
	newSale, err := s.repository.CreateNewSale(ctx, sale)
	if err != nil {
		return nil, err
	}
	return newSale, nil
}

