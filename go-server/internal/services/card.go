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
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.CardModelToResponse(newCard), nil
}

func (s *CardService) GetCustomerCard(ctx context.Context, cardNumber string) (*views.CustomerCardResponse, error) {
	card, err := s.cardRepository.GetCustomerCard(ctx, cardNumber)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.CardModelToResponse(card), nil
}

func (s *CardService) UpdateCustomerCard(ctx context.Context, card views.UpdateCustomerCard, cardNumber string) (*views.CustomerCardResponse, error) {
	updatedCard, err := s.cardRepository.UpdateCustomerCard(ctx, card, cardNumber)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.CardModelToResponse(updatedCard), nil
}

func (s *CardService) DeleteCustomerCard(ctx context.Context, cardNumber string) error {
	err := s.cardRepository.DeleteCustomerCard(ctx, cardNumber)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return err
		}
		return errorResponse.Internal(err)
	}
	return nil
}

func (s *CardService) ListCustomerCards(ctx context.Context) ([]*views.CustomerCardResponse, error) {
	cards, err := s.cardRepository.ListCustomerCards(ctx)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	var cardList []*views.CustomerCardResponse
	for _, card := range cards {
		cardList = append(cardList, mappers.CardModelToResponse(&card))
	}
	return cardList, nil
}
