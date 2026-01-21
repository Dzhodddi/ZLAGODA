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
	totalPrice := 0.0
	rawPayloadSQL := ""
	values := make([]interface{}, 0, 2*len(check.Products))
	keys := make([]string, 0, len(check.Products))
	for _, product := range check.Products {
		keys = append(keys, product.UPC)
		values = append(values, product.UPC, product.Quantity)
		rawPayloadSQL += "(?, ?::int),"
	}
	rawPayloadSQL = strings.TrimSuffix(rawPayloadSQL, ",")
	newCheck, err := s.checkRepository.CreateNewCheck(ctx, check, printTime, rawPayloadSQL, totalPrice, values, keys)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.CheckModelToResponse(newCheck), nil
}
