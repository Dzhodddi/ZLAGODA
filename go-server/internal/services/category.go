package services

import (
	"context"
	"errors"

	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/mappers"
	"github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type CategoryService struct {
	categoryRepository *repository.CategoryRepository
}

func NewCategoryService(categoryRepository *repository.CategoryRepository) *CategoryService {
	return &CategoryService{
		categoryRepository: categoryRepository,
	}
}

func (s *CategoryService) CreateCategory(ctx context.Context, category views.CreateNewCategory) (*views.CategoryResponse, error) {
	newCategory, err := s.categoryRepository.CreateNewCategory(ctx, category)
	if err != nil {
		return nil, errorResponse.Internal(err)
	}
	return mappers.CategoryModelToResponse(newCategory), nil
}

func (s *CategoryService) UpdateCategory(ctx context.Context, updateCategory views.UpdateCategory, categoryId int64) (*views.CategoryResponse, error) {
	updatedCategory, err := s.categoryRepository.UpdateCategory(ctx, updateCategory, categoryId)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.CategoryModelToResponse(updatedCategory), nil
}

func (s *CategoryService) DeleteCategory(ctx context.Context, id int64) error {
	err := s.categoryRepository.DeleteCategory(ctx, id)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return err
		}
		return errorResponse.Internal(err)
	}
	return nil
}

func (s *CategoryService) GetCategoryByID(ctx context.Context, id int64) (*views.CategoryResponse, error) {
	category, err := s.categoryRepository.GetCategoryByID(ctx, id)
	if err != nil {
		var httpErr *errorResponse.HTTPErrorResponse
		if errors.As(err, &httpErr) {
			return nil, err
		}
		return nil, errorResponse.Internal(err)
	}
	return mappers.CategoryModelToResponse(category), nil
}

func (s *CategoryService) GetAllCategories(ctx context.Context) ([]*views.CategoryResponse, error) {
	categories, err := s.categoryRepository.GetAllCategories(ctx)
	if err != nil {
		return nil, errorResponse.Internal(err)
	}
	var categoryResponses []*views.CategoryResponse
	for _, category := range categories {
		categoryResponses = append(categoryResponses, mappers.CategoryModelToResponse(&category))
	}
	return categoryResponses, nil
}
