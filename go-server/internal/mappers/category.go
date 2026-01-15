package mappers

import (
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

func CategoryModelToResponse(category *generated.Category) *views.CategoryResponse {
	return &views.CategoryResponse{
		CategoryName:   category.CategoryName,
		CategoryNumber: category.CategoryNumber,
	}
}
