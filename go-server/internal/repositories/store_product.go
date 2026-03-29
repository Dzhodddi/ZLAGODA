package repository

import (
	"context"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/jmoiron/sqlx"
)

type StoreProductRepository interface {
	GetStoreProductIDList(ctx context.Context) ([]generated.GetStoreProductIDListRow, error)
}

type storeProductRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewStoreProductRepository(db *sqlx.DB) StoreProductRepository {
	return &storeProductRepository{
		db:      db,
		queries: generated.New(db),
	}
}

func (r *storeProductRepository) GetStoreProductIDList(ctx context.Context) ([]generated.GetStoreProductIDListRow, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	return r.queries.GetStoreProductIDList(ctx)
}
