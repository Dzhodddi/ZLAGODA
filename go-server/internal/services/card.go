package services

import (
	"context"
	"fmt"

	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type CardService interface {
	CreateNewCustomerCard(ctx context.Context, card views.CreateNewCustomerCard) (*views.CustomerCardResponse, error)
	GetCustomerCard(ctx context.Context, cardNumber string) (*views.CustomerCardResponse, error)
	UpdateCustomerCard(ctx context.Context, card views.UpdateCustomerCard, cardNumber string) (*views.CustomerCardResponse, error)
	DeleteCustomerCard(ctx context.Context, cardNumber string) error
	ListCustomerCards(ctx context.Context, q views.ListCustomerCardsQueryParams) ([]views.CustomerCardResponse, error)
}

type cardService struct {
	cardRepository repository.CardRepository
}

func NewCardService(cardRepository repository.CardRepository) CardService {
	return &cardService{cardRepository: cardRepository}
}

func (s *cardService) CreateNewCustomerCard(ctx context.Context, card views.CreateNewCustomerCard) (*views.CustomerCardResponse, error) {
	newCard, err := s.cardRepository.CreateNewCard(ctx, card)
	if err != nil {
		return nil, fmt.Errorf("failed to create card: %w", err)
	}
	return mappers.CardModelToResponse(newCard), nil
}

func (s *cardService) GetCustomerCard(ctx context.Context, cardNumber string) (*views.CustomerCardResponse, error) {
	card, err := s.cardRepository.GetCustomerCard(ctx, cardNumber)
	if err != nil {
		return nil, fmt.Errorf("failed to fetch card: %w", err)
	}
	return mappers.CardModelToResponse(card), nil
}

func (s *cardService) UpdateCustomerCard(ctx context.Context, card views.UpdateCustomerCard, cardNumber string) (*views.CustomerCardResponse, error) {
	updatedCard, err := s.cardRepository.UpdateCustomerCard(ctx, card, cardNumber)
	if err != nil {
		return nil, fmt.Errorf("failed to update card: %w", err)
	}
	return mappers.CardModelToResponse(updatedCard), nil
}

func (s *cardService) DeleteCustomerCard(ctx context.Context, cardNumber string) error {
	err := s.cardRepository.DeleteCustomerCard(ctx, cardNumber)
	if err != nil {
		return fmt.Errorf("failed to delete card: %w", err)
	}
	return nil
}

func (s *cardService) ListCustomerCards(ctx context.Context, q views.ListCustomerCardsQueryParams) ([]views.CustomerCardResponse, error) {
	var cards []generated.CustomerCard
	var err error
	switch {
	case q.Percent != nil:
		cards, err = s.cardRepository.ListCustomerCardsSortedByPercent(ctx, *q.Percent)
	case q.Sorted != nil && *q.Sorted:
		cards, err = s.cardRepository.ListCustomerCardsSortedBySurname(ctx)
	default:
		cards, err = s.cardRepository.ListCustomerCards(ctx)
	}

	if err != nil {
		return nil, fmt.Errorf("failed to fetch all cards: %w", err)
	}
	var cardList []views.CustomerCardResponse
	for _, card := range cards {
		cardList = append(cardList, *mappers.CardModelToResponse(&card))
	}
	return cardList, nil
}
