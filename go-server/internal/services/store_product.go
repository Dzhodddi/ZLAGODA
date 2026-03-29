package services

import (
	"context"

	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type StoreProductService interface {
	GetStoreProductList(
		ctx context.Context,
	) ([]views.DropdownStoreProductItem, error)
}

type storeProductService struct {
	storeProductRepository repository.StoreProductRepository
}

func NewStoreProductService(storeProductRepository repository.StoreProductRepository) StoreProductService {
	return &storeProductService{
		storeProductRepository: storeProductRepository,
	}
}

func (s storeProductService) GetStoreProductList(ctx context.Context) ([]views.DropdownStoreProductItem, error) {
	items, err := s.storeProductRepository.GetStoreProductIDList(ctx)
	if err != nil {
		return nil, err
	}
	response := make([]views.DropdownStoreProductItem, 0, len(items))
	for i := range items {
		response = append(response, views.DropdownStoreProductItem{
			UPC:          items[i].Upc,
			SellingPrice: items[i].SellingPrice,
			Quantity:     items[i].ProductsNumber,
			ProductName:  items[i].ProductName,
		})
	}
	return response, err
}
