package services

import (
	"context"
	"fmt"

	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type CategoryService interface {
	CreateCategory(ctx context.Context, category views.CreateNewCategory) (*views.CategoryResponse, error)
	UpdateCategory(ctx context.Context, updateCategory views.UpdateCategory, categoryId int64) (*views.CategoryResponse, error)
	DeleteCategory(ctx context.Context, id int64) error
	GetCategoryByID(ctx context.Context, id int64) (*views.CategoryResponse, error)
	GetAllCategories(ctx context.Context, q views.ListCategoryQueryParams) ([]views.CategoryResponse, error)
}

type categoryService struct {
	categoryRepository repository.CategoryRepository
}

func NewCategoryService(categoryRepository repository.CategoryRepository) CategoryService {
	return &categoryService{
		categoryRepository: categoryRepository,
	}
}

func (s *categoryService) CreateCategory(ctx context.Context, category views.CreateNewCategory) (*views.CategoryResponse, error) {
	newCategory, err := s.categoryRepository.CreateNewCategory(ctx, category)
	if err != nil {
		return nil, fmt.Errorf("failed to create category: %w", err)
	}
	return mappers.CategoryModelToResponse(newCategory), nil
}

func (s *categoryService) UpdateCategory(ctx context.Context, updateCategory views.UpdateCategory, categoryId int64) (*views.CategoryResponse, error) {
	updatedCategory, err := s.categoryRepository.UpdateCategory(ctx, updateCategory, categoryId)
	if err != nil {
		return nil, fmt.Errorf("failed to update category: %w", err)
	}
	return mappers.CategoryModelToResponse(updatedCategory), nil
}

func (s *categoryService) DeleteCategory(ctx context.Context, id int64) error {
	err := s.categoryRepository.DeleteCategory(ctx, id)
	if err != nil {
		return fmt.Errorf("failed to delete category: %w", err)
	}
	return nil
}

func (s *categoryService) GetCategoryByID(ctx context.Context, id int64) (*views.CategoryResponse, error) {
	category, err := s.categoryRepository.GetCategoryByID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("failed to fetch category: %w", err)
	}
	return mappers.CategoryModelToResponse(category), nil
}

func (s *categoryService) GetAllCategories(ctx context.Context, q views.ListCategoryQueryParams) ([]views.CategoryResponse, error) {
	var categories []generated.Category
	var err error
	switch {
	case q.Sorted != nil && *q.Sorted:
		categories, err = s.categoryRepository.GetAllCategoriesSortedByName(ctx)
	default:
		categories, err = s.categoryRepository.GetAllCategories(ctx)
	}
	if err != nil {
		return nil, fmt.Errorf("failed to fetch categories: %w", err)
	}
	var categoryResponses []views.CategoryResponse
	for _, category := range categories {
		categoryResponses = append(categoryResponses, *mappers.CategoryModelToResponse(&category))
	}
	return categoryResponses, nil
}
