package mappers

import (
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

func SaleModelToResponse(sale *generated.Sale) *views.SaleResponse {
	return &views.SaleResponse{
		ProductNumber: sale.ProductNumber,
		Upc:           sale.Upc,
		CheckNumber:   sale.CheckNumber,
		SellingPrice:  sale.SellingPrice,
	}
}
