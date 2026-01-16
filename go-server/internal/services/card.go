package services

import (
	"context"
	"errors"

	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type CardService struct {
	cardRepository *repository.CardRepository
}

func NewCardService(cardRepository *repository.CardRepository) *CardService {
	return &CardService{cardRepository: cardRepository}
}

func (s *CardService) CreateNewCustomerCard(ctx context.Context, card views.CreateNewCustomerCard) (*views.CustomerCardResponse, error) {
	newCard, err := s.cardRepository.CreateNewCard(ctx, card)
	if err != nil {
		if errors.Is(err, errorResponse.Conflict()) {
			return nil, err
		}
	}
	return mappers.CardModelToResponse(newCard), nil
}
