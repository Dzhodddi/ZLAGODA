package repository

import (
	"context"
	"database/sql"
	"errors"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
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

func (r *CategoryRepository) UpdateCategory(ctx context.Context, category views.UpdateCategory, categoryId int64) (*generated.Category, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	updatedCategory, err := r.queries.UpdateCategory(
		ctx,
		generated.UpdateCategoryParams{
			CategoryNumber: categoryId,
			CategoryName:   category.CategoryName,
		},
	)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errorResponse.EntityNotFound()
		}
		return nil, err
	}
	return &updatedCategory, nil
}

func (r *CategoryRepository) DeleteCategory(ctx context.Context, id int64) error {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	_, err := r.queries.DeleteCategoryByID(ctx, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return errorResponse.EntityNotFound()
		}
		return err
	}
	return nil
}

func (r *CategoryRepository) GetCategoryByID(ctx context.Context, id int64) (*generated.Category, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	category, err := r.queries.GetCategoryByID(ctx, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errorResponse.EntityNotFound()
		}
		return nil, err
	}
	return &category, nil
}

func (r *CategoryRepository) GetAllCategories(ctx context.Context) ([]generated.Category, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	categories, err := r.queries.GetAllCategories(ctx)
	if err != nil {
		return nil, err
	}
	return categories, nil
}
