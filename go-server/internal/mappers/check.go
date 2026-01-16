package mappers

import (
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
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
