package repository

import (
	"context"
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
)

type CategoryRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewCategoryRepository(db *sqlx.DB) *CategoryRepository {
	return &CategoryRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

func (r *CategoryRepository) CreateNewCategory(ctx context.Context, category views.CreateNewCategory) (*generated.Category, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	newCategory, err := r.queries.CreateNewCategory(ctx, category.CategoryName)
	if err != nil {
		return nil, err
	}
	return &newCategory, nil
}
