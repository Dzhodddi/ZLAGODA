package services

import (
	"context"
	"fmt"

	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"

	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type SaleService interface {
	CreateNewSale(ctx context.Context, sale views.CreateNewSale) (*views.SaleResponse, error)
}

type saleService struct {
	saleRepository repository.SaleRepository
}

func NewSaleService(saleRepository repository.SaleRepository) SaleService {
	return &saleService{saleRepository: saleRepository}
}

func (s *saleService) CreateNewSale(ctx context.Context, sale views.CreateNewSale) (*views.SaleResponse, error) {
	newSale, err := s.saleRepository.CreateNewSale(ctx, sale)
	if err != nil {
		return nil, fmt.Errorf("failed to create sale: %w", err)
	}
	return mappers.SaleModelToResponse(newSale), nil
}
