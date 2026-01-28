package repository

import (
	"errors"

	"github.com/lib/pq"

	"context"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
)

type SaleRepository interface {
	CreateNewSale(ctx context.Context, sale views.CreateNewSale) (*generated.Sale, error)
}

type saleRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewSaleRepository(db *sqlx.DB) SaleRepository {
	return &saleRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

func (r *saleRepository) CreateNewSale(ctx context.Context, sale views.CreateNewSale) (*generated.Sale, error) {
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
		var pgErr *pq.Error
		if errors.As(err, &pgErr) {
			switch pgErr.Code {
			case "23503": // Foreign key constraint violation
				return nil, ErrForeignKey
			case "23505": // Unique constraint violation
				return nil, ErrConflict
			}
		}
		return nil, err
	}
	return &newSale, nil
}
