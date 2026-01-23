package services

import (
	"context"
	"fmt"
	"strings"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type CheckService interface {
	CreateCheck(ctx context.Context, check views.CreateNewCheck, printTime time.Time) (*views.CheckResponse, error)
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
