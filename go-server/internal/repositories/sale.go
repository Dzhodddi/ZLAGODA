package repository

import (
	"context"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/jmoiron/sqlx"
)

type SaleRepository interface {
	GetAllSalesWithinDate(ctx context.Context, startDate, endDate time.Time, lastCheckNumber string) ([]generated.Sale, error)
}

type saleRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewSaleRepository(db *sqlx.DB) SaleRepository {
	return &saleRepository{
		db:      db,
		queries: generated.New(db),
	}
}

func (s *saleRepository) GetAllSalesWithinDate(
	ctx context.Context,
	startDate, endDate time.Time,
	lastCheckNumber string,
) ([]generated.Sale, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	return s.queries.GetSalesWithinDate(ctx, generated.GetSalesWithinDateParams{
		PrintDate:   startDate,
		PrintDate_2: endDate,
		CheckNumber: lastCheckNumber,
		Limit:       constants.PaginationStep,
	})
}
