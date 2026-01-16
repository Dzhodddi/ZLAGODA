package services

import (
	"context"
	"errors"
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
	newCheck, err := s.checkRepository.CreateNewCheck(ctx, check, printTime)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.CheckModelToResponse(newCheck), nil
}
