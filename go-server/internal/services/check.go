package services

import (
	"context"
	"errors"
	"strings"
	"time"

	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	"github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type CheckService struct {
	checkRepository *repository.CheckRepository
}

func NewCheckService(checkRepository *repository.CheckRepository) *CheckService {
	return &CheckService{
		checkRepository: checkRepository,
	}
}

func (s *CheckService) CreateCheck(ctx context.Context, check views.CreateNewCheck, printTime time.Time) (*views.CheckResponse, error) {
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
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.CheckModelToResponse(newCheck), nil
}
