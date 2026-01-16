package services

import (
	"context"
	"errors"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"

	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type SaleService struct {
	repository *repository.SaleRepository
}

func NewSaleService(repository *repository.SaleRepository) *SaleService {
	return &SaleService{repository: repository}
}

func (s *SaleService) CreateNewSale(ctx context.Context, sale views.CreateNewSale) (*views.SaleResponse, error) {
	newSale, err := s.repository.CreateNewSale(ctx, sale)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.SaleModelToResponse(newSale), nil
}
