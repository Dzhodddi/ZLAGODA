package repository

import (
	"context"
	"database/sql"
	"errors"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/utils"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

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
			return nil, errorResponse.Conflict()
		}
		return nil, err
	}
	return &newCard, nil
}

func (r *CardRepository) GetCustomerCard(ctx context.Context, cardNumber string) (*generated.CustomerCard, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	card, err := r.queries.GetCustomerCardByID(ctx, cardNumber)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errorResponse.EntityNotFound()
		}
		return nil, err
	}
	return &card, nil
}

func (r *CardRepository) UpdateCustomerCard(ctx context.Context, card views.UpdateCustomerCard, cardNumber string) (*generated.CustomerCard, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	result, err := r.queries.UpdateCustomerCard(
		ctx,
		generated.UpdateCustomerCardParams{
			CardNumber:         cardNumber,
			CustomerSurname:    card.CustomerSurname,
			CustomerName:       card.CustomerName,
			CustomerPatronymic: utils.ToNullString(card.CustomerPatronymic),
			PhoneNumber:        card.PhoneNumber,
			City:               utils.ToNullString(card.City),
			Street:             utils.ToNullString(card.Street),
			ZipCode:            utils.ToNullString(card.Zipcode),
			CustomerPercent:    card.CustomerPercent,
		},
	)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errorResponse.EntityNotFound()
		}
		if pgErr, ok := err.(*pq.Error); ok {
			switch pgErr.Code {
			case "23503": // Foreign key constraint violation
				return nil, errorResponse.BadForeignKey()
			}
		}
		return nil, err
	}
	return &result, nil
}

func (r *CardRepository) DeleteCustomerCard(ctx context.Context, cardNumber string) error {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	_, err := r.queries.DeleteCustomerCardByID(ctx, cardNumber)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return errorResponse.EntityNotFound()
		}
		return err
	}
	return nil
}

func (r *CardRepository) ListCustomerCards(ctx context.Context) ([]generated.CustomerCard, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	rows, err := r.queries.GetAllCustomerCards(ctx)
	if err != nil {
		return nil, err
	}
	return rows, nil
}
