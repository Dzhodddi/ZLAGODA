package repository

import (
	"context"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

type CheckRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewCheckRepository(db *sqlx.DB) *CheckRepository {
	return &CheckRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

func (r *CheckRepository) CreateNewCheck(ctx context.Context, check views.CreateNewCheck, printTime time.Time) (*generated.Check, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	newCheck, err := r.queries.CreateNewCheck(
		ctx,
		generated.CreateNewCheckParams{
			CheckNumber: check.CheckNumber,
			IDEmployee:  check.IDEmployee,
			CardNumber:  check.CardNumber,
			PrintDate:   printTime,
			SumTotal:    check.SumTotal,
			Vat:         check.VAT,
		},
	)
	if err != nil {
		if pgErr, ok := err.(*pq.Error); ok {
			switch pgErr.Code {
			case "23503": // Foreign key constraint violation
				return nil, errorResponse.BadForeignKey()
			case "23505": // Unique constraint violation
				return nil, errorResponse.Conflict()
			}
		}
		return nil, err
	}
	return &newCheck, nil
}
