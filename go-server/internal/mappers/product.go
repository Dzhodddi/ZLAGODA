package mappers

import (
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

func ProductModelToResponse(product *generated.GetCheckProductsByNameRow) *views.ProductResponse {
	return &views.ProductResponse{
		Name:         product.ProductName,
		Quantity:     int(product.ProductNumber),
		SellingPrice: product.SellingPrice,
	}
}
