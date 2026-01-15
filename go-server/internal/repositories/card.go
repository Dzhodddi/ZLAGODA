package repository

import (
	"context"
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/utils"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var ErrCustomerCardExists = errorResponse.Conflict(constants.EntityAlreadyExists)

type CardRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewCardRepository(db *sqlx.DB) *CardRepository {
	return &CardRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

func (r *CardRepository) CreateNewCard(ctx context.Context, card views.CreateNewCustomerCard) (*generated.CustomerCard, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	newCard, err := r.queries.CreateNewCustomerCard(ctx, generated.CreateNewCustomerCardParams{
		CardNumber:         card.CardNumber,
		CustomerSurname:    card.CustomerSurname,
		CustomerName:       card.CustomerName,
		CustomerPatronymic: utils.ToNullString(card.CustomerPatronymic),
		PhoneNumber:        card.PhoneNumber,
		City:               utils.ToNullString(card.City),
		Street:             utils.ToNullString(card.Street),
		ZipCode:            utils.ToNullString(card.Zipcode),
		CustomerPercent:    card.CustomerPercent,
	})
	if err != nil {
		if pgErr, ok := err.(*pq.Error); ok && pgErr.Code == "23505" {
			return nil, ErrCustomerCardExists
		}
		return nil, err
	}
	return &newCard, nil
}
