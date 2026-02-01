package mappers

import (
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

func CheckModelToResponse(check *generated.Check) *views.CheckResponse {
	return &views.CheckResponse{
		CheckNumber: check.CheckNumber,
		IDEmployee:  check.IDEmployee,
		CardNumber:  check.CardNumber,
		PrintDate:   check.PrintDate.Format("2006-01-02"),
		SumTotal:    check.SumTotal,
		VAT:         check.Vat,
	}
}

func CheckModelWithProductsToResponse(check *repository.Check) *views.CheckResponseWithProducts {
	products := make([]views.ProductResponse, 0, len(check.ProductList))
	for _, product := range check.ProductList {
		products = append(products, *ProductModelToResponse(&product))
	}
	return &views.CheckResponseWithProducts{
		CheckResponse: *CheckModelToResponse(check.Check),
		Products:      products,
	}
}

func CheckListViewToResponse(check *generated.CheckListView) *views.CheckListResponse {
	return &views.CheckListResponse{
		CheckResponse: views.CheckResponse{
			CheckNumber: check.CheckNumber,
			IDEmployee:  check.IDEmployee,
			CardNumber:  check.CardNumber,
			PrintDate:   check.PrintDate.Format(constants.DateLayout),
			SumTotal:    check.SumTotal,
			VAT:         check.Vat,
		},
		ProductResponse: views.ProductResponse{
			Name:         check.ProductName,
			Quantity:     int(check.ProductNumber),
			SellingPrice: check.SellingPrice,
		},
	}
}
