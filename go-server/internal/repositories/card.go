package repository

import (
	"context"
	"database/sql"
	"errors"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/utils"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

type CardRepository interface {
	CreateNewCard(ctx context.Context, card views.CreateNewCustomerCard) (*generated.CustomerCard, error)
	GetCustomerCard(ctx context.Context, cardNumber string) (*generated.CustomerCard, error)
	UpdateCustomerCard(ctx context.Context, card views.UpdateCustomerCard, cardNumber string) (*generated.CustomerCard, error)
	DeleteCustomerCard(ctx context.Context, cardNumber string) error
	ListCustomerCards(ctx context.Context) ([]generated.CustomerCard, error)
	ListCustomerCardsSortedBySurname(ctx context.Context) ([]generated.CustomerCard, error)
	ListCustomerCardsSortedByPercent(ctx context.Context, percent int) ([]generated.CustomerCard, error)
}

type cardRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewCardRepository(db *sqlx.DB) CardRepository {
	return &cardRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

func (r *cardRepository) CreateNewCard(ctx context.Context, card views.CreateNewCustomerCard) (*generated.CustomerCard, error) {
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
			return nil, ErrConflict
		}
		return nil, err
	}
	return &newCard, nil
}

func (r *cardRepository) GetCustomerCard(ctx context.Context, cardNumber string) (*generated.CustomerCard, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	card, err := r.queries.GetCustomerCardByID(ctx, cardNumber)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, ErrNotFound
		}
		return nil, err
	}
	return &card, nil
}

func (r *cardRepository) UpdateCustomerCard(ctx context.Context, card views.UpdateCustomerCard, cardNumber string) (*generated.CustomerCard, error) {
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
			return nil, ErrNotFound
		}
		if pgErr, ok := err.(*pq.Error); ok {
			switch pgErr.Code {
			case "23503": // Foreign key constraint violation
				return nil, ErrForeignKey
			}
		}
		return nil, err
	}
	return &result, nil
}

func (r *cardRepository) DeleteCustomerCard(ctx context.Context, cardNumber string) error {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	_, err := r.queries.DeleteCustomerCardByID(ctx, cardNumber)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return ErrNotFound
		}
		var pqErr *pq.Error
		if errors.As(err, &pqErr) {
			if pqErr.Code == "23503" {
				return ErrForeignKey
			}
		}
		return err
	}
	return nil
}

func (r *cardRepository) ListCustomerCards(ctx context.Context) ([]generated.CustomerCard, error) {
	return r.getListHelper(ctx, r.ListCustomerCards)
}

func (r *cardRepository) ListCustomerCardsSortedBySurname(ctx context.Context) ([]generated.CustomerCard, error) {
	return r.getListHelper(ctx, r.ListCustomerCardsSortedBySurname)
}

func (r *cardRepository) ListCustomerCardsSortedByPercent(ctx context.Context, percent int) ([]generated.CustomerCard, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	rows, err := r.ListCustomerCardsSortedByPercent(ctx, percent)
	if err != nil {
		return nil, err
	}
	return rows, nil
}

func (r *cardRepository) getListHelper(ctx context.Context, queryList func(ctx context.Context) ([]generated.CustomerCard, error)) ([]generated.CustomerCard, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	rows, err := queryList(ctx)
	if err != nil {
		return nil, err
	}
	return rows, nil
}
