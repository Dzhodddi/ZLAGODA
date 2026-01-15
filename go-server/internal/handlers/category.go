package handlers

import (
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/services"
	validation "github.com/Dzhodddi/ZLAGODA/internal/validator"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/labstack/echo/v4"
	"net/http"
)

type CategoryHandler struct {
	categoryService services.CategoryService
}

func NewCategoryHandler(categoryService services.CategoryService) *CategoryHandler {
	return &CategoryHandler{
		categoryService: categoryService,
	}
}

func (h *CategoryHandler) RegisterRouts(e *echo.Group) {
	category := e.Group("/categories")
	category.POST("", h.createCategory)
}

// createCategory godoc
//
// @Summary      Create a new category
// @Description  Creates a new product category
// @Tags         Category
// @Accept       json
// @Produce      json
// @Param        payload  body      views.CreateNewCategory  true  "Category data"
// @Success      201  {object}  views.CategoryResponse
// @Failure      400  {object}  map[string]any  "Invalid request payload"
// @Failure      422  {object}  map[string]any  "Validation error"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /categories [post]
func (h *CategoryHandler) createCategory(c echo.Context) error {
	var payload views.CreateNewCategory
	if err := c.Bind(&payload); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	if err := validation.ValidateStruct(payload); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	category, err := h.categoryService.CreateCategory(c.Request().Context(), payload)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusCreated, category)
}
