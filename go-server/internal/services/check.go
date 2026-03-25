package services

import (
	"context"
	"fmt"
	"strings"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type CheckService interface {
	CreateCheck(ctx context.Context, check views.CreateNewCheck, printTime time.Time) (*views.CheckResponse, error)
	DeleteCheck(ctx context.Context, checkNumber string) error
	GetCheck(ctx context.Context, checkNumber string) (*views.CheckResponseWithProducts, error)
	GetCheckList(
		ctx context.Context,
		employeeID *string,
		startDate, endDate time.Time,
	) (*[]views.CheckResponseWithProducts, error)
	GetTotalCheckPrice(
		ctx context.Context,
		q views.CheckListQueryParams,
		startDate, endDate time.Time,
	) (float64, error)
}

type checkService struct {
	checkRepository repository.CheckRepository
}

func NewCheckService(checkRepository repository.CheckRepository) CheckService {
	return &checkService{
		checkRepository: checkRepository,
	}
}

func (s *checkService) CreateCheck(ctx context.Context, check views.CreateNewCheck, printTime time.Time) (*views.CheckResponse, error) {
	upcToQuantity := make(map[string]int)
	for _, product := range check.Products {
		_, ok := upcToQuantity[product.UPC]
		if !ok {
			upcToQuantity[product.UPC] = product.Quantity
		} else {
			upcToQuantity[product.UPC] += product.Quantity
		}
	}
	rawPayloadSQL := ""
	payload := make([]interface{}, 0, len(upcToQuantity))
	keys := make([]string, 0, len(upcToQuantity))
	for key, value := range upcToQuantity {
		keys = append(keys, key)
		payload = append(payload, key, value)
		rawPayloadSQL += "(?, ?::int),"
	}
	rawPayloadSQL = strings.TrimSuffix(rawPayloadSQL, ",")
	newCheck, err := s.checkRepository.CreateNewCheck(ctx, check, printTime, rawPayloadSQL, payload, keys)
	if err != nil {
		return nil, fmt.Errorf("failed to create check: %w", err)
	}
	return mappers.CheckModelToResponse(newCheck), nil
}

func (s *checkService) DeleteCheck(ctx context.Context, checkNumber string) error {
	err := s.checkRepository.DeleteCheck(ctx, checkNumber)
	if err != nil {
		return fmt.Errorf("failed to delete category: %w", err)
	}
	return nil
}

func (s *checkService) GetCheck(ctx context.Context, checkNumber string) (*views.CheckResponseWithProducts, error) {
	check, err := s.checkRepository.GetCheckByNumber(ctx, checkNumber)
	if err != nil {
		return nil, fmt.Errorf("failed to get check: %w", err)
	}
	return mappers.CheckModelWithProductsToResponse(check), nil
}

func (s *checkService) GetCheckList(
	ctx context.Context,
	employeeID *string,
	startDate, endDate time.Time,
) (*[]views.CheckResponseWithProducts, error) {
	var checkList []generated.CheckListView
	var err error

	switch {
	case employeeID != nil:
		checkList, err = s.checkRepository.GetChecksWithProductsByCashierWithinDate(
			ctx,
			*employeeID,
			startDate,
			endDate,
		)
	default:
		checkList, err = s.checkRepository.GetAllChecksWithProductsWithinDate(
			ctx,
			startDate,
			endDate,
		)
	}
	if err != nil {
		return nil, fmt.Errorf("failed to get checks: %w", err)
	}
	mappedChecks := make(map[string]*views.CheckResponseWithProducts)
	for _, row := range checkList {
		if _, exists := mappedChecks[row.CheckNumber]; !exists {
			mappedChecks[row.CheckNumber] = &views.CheckResponseWithProducts{
				CheckResponse: views.CheckResponse{
					CheckNumber: row.CheckNumber,
					IDEmployee:  row.IDEmployee,
					CardNumber:  row.CardNumber,
					PrintDate:   row.PrintDate.Format(constants.DateLayout),
					SumTotal:    row.SumTotal,
					VAT:         row.Vat,
				},
				Products: make([]views.ProductResponse, 0),
			}
		}

		mappedChecks[row.CheckNumber].Products = append(mappedChecks[row.CheckNumber].Products, views.ProductResponse{
			Name:         row.ProductName,
			SellingPrice: row.SellingPrice,
			Quantity:     int(row.ProductNumber),
		})
	}
	result := make([]views.CheckResponseWithProducts, 0, len(mappedChecks))
	for _, check := range mappedChecks {
		result = append(result, *check)
	}
	return &result, nil
}

func (s *checkService) GetTotalCheckPrice(
	ctx context.Context,
	q views.CheckListQueryParams,
	startDate, endDate time.Time,
) (float64, error) {
	var totalPrice float64
	var err error
	switch {
	case q.EmployeeID != nil:
		totalPrice, err = s.checkRepository.GetTotalPriceByCashierWithinDate(
			ctx,
			*q.EmployeeID,
			startDate, endDate)
	default:
		totalPrice, err = s.checkRepository.GetTotalPriceByAllCashiersWithinDate(
			ctx,
			startDate, endDate)
	}
	if err != nil {
		return 0, fmt.Errorf("failed to get total price: %w", err)
	}
	return totalPrice, nil
}
