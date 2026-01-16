package repository

import (
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/lib/pq"

	"context"
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
)

type SaleRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewSaleRepository(db *sqlx.DB) *SaleRepository {
	return &SaleRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

func (r *SaleRepository) CreateNewSale(ctx context.Context, sale views.CreateNewSale) (*generated.Sale, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	newSale, err := r.queries.CreateNewSale(
		ctx,
		generated.CreateNewSaleParams{
			ProductNumber: sale.ProductNumber,
			Upc:           sale.Upc,
			CheckNumber:   sale.CheckNumber,
			SellingPrice:  sale.SellingPrice,
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
	return &newSale, nil
}
