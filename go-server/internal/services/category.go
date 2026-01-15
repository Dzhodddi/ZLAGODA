package services

import (
	"context"
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
