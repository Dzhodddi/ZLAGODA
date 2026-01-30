package repository

import (
	"context"
	"database/sql"
	"errors"

	"github.com/lib/pq"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
)

type CategoryRepository interface {
	CreateNewCategory(ctx context.Context, category views.CreateNewCategory) (*generated.Category, error)
	UpdateCategory(ctx context.Context, category views.UpdateCategory, categoryId int64) (*generated.Category, error)
	DeleteCategory(ctx context.Context, id int64) error
	GetCategoryByID(ctx context.Context, id int64) (*generated.Category, error)
	GetAllCategories(ctx context.Context, lastCategoryNumber int64) ([]generated.Category, error)
	GetAllCategoriesSortedByName(ctx context.Context) ([]generated.Category, error)
}

type categoryRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewCategoryRepository(db *sqlx.DB) CategoryRepository {
	return &categoryRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

func (r *categoryRepository) CreateNewCategory(ctx context.Context, category views.CreateNewCategory) (*generated.Category, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	newCategory, err := r.queries.CreateNewCategory(ctx, category.CategoryName)
	if err != nil {
		return nil, err
	}
	return &newCategory, nil
}

func (r *categoryRepository) UpdateCategory(ctx context.Context, category views.UpdateCategory, categoryId int64) (*generated.Category, error) {
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
			return nil, ErrNotFound
		}
		return nil, err
	}
	return &updatedCategory, nil
}

func (r *categoryRepository) DeleteCategory(ctx context.Context, id int64) error {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	_, err := r.queries.DeleteCategoryByID(ctx, id)
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

func (r *categoryRepository) GetCategoryByID(ctx context.Context, id int64) (*generated.Category, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	category, err := r.queries.GetCategoryByID(ctx, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, ErrNotFound
		}
		return nil, err
	}
	return &category, nil
}

func (r *categoryRepository) GetAllCategories(ctx context.Context, lastCategoryNumber int64) ([]generated.Category, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	categories, err := r.queries.GetAllCategories(ctx, generated.GetAllCategoriesParams{
		CategoryNumber: lastCategoryNumber,
		Limit:          constants.PaginationStep,
	})
	if err != nil {
		return nil, err
	}
	return categories, nil
}

func (r *categoryRepository) GetAllCategoriesSortedByName(ctx context.Context) ([]generated.Category, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	categories, err := r.queries.GetAllCategoriesSortedByName(ctx, constants.PaginationStep)
	if err != nil {
		return nil, err
	}
	return categories, nil
}
