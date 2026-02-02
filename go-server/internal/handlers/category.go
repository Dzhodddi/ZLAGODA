package handlers

import (
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/auth"
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/services"
	"github.com/Dzhodddi/ZLAGODA/internal/utils"
	validation "github.com/Dzhodddi/ZLAGODA/internal/validator"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/labstack/echo/v4"
)

type CategoryHandler struct {
	categoryService services.CategoryService
	auth            auth.Authenticator
}

func NewCategoryHandler(categoryService services.CategoryService, auth auth.Authenticator) *CategoryHandler {
	return &CategoryHandler{
		categoryService: categoryService,
		auth:            auth,
	}
}

func (h *CategoryHandler) RegisterRouts(e *echo.Group) {
	category := e.Group("/categories")
	category.POST("", h.createCategory, h.auth.CheckRole(auth.Manager))
	category.GET("", h.getAllCategories, h.auth.CheckRole(auth.Manager))

	id := category.Group("/:id")
	id.PUT("", h.updateCategory, h.auth.CheckRole(auth.Manager))
	id.DELETE("", h.deleteCategory, h.auth.CheckRole(auth.Manager))
	id.GET("", h.getCategoryByID, h.auth.CheckRole(auth.Manager))
}

// createCategory godoc
//
// @Summary      Create a new category
// @Description  Creates a new product category
// @Tags         Category
// @Accept       json
// @Produce      json
// @Param        payload  body      views.CreateNewCategory  true  "Category data"
// @Success      201  {object}  views.CategoryResponse "Payload of category"
// @Failure      400  {object}  map[string]any
// @Failure      401  {object}  map[string]any  "Unauthorized"
// @Failure      403  {object}  map[string]any  "Forbidden"
// @Failure      422  {object}  map[string]any
// @Failure      500  {object}  map[string]any
//
//	@Security		ApiKeyAuth
//
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

// updateCategory godoc
//
// @Summary      Update an existing category
// @Description  Updates an existing product category by ID
// @Tags         Category
// @Accept       json
// @Produce      json
// @Param        id       path      int64     true "Category ID"
// @Param        payload  body      views.UpdateCategory  true	"Payload of category"
// @Success      200  {object}  views.CategoryResponse
// @Failure      400  {object}  map[string]any
// @Failure      401  {object}  map[string]any  "Unauthorized"
// @Failure      403  {object}  map[string]any  "Forbidden"
// @Failure      422  {object}  map[string]any
// @Failure      404  {object}  map[string]any
// @Failure      500  {object}  map[string]any
//
//	@Security		ApiKeyAuth
//
// @Router       /categories/{id} [put]
func (h *CategoryHandler) updateCategory(c echo.Context) error {
	id, err := utils.ParseStringToInt(c.Param("id"))
	if err != nil {
		return errorResponse.BadRequest(constants.EntityDoesNotExist, err)
	}
	var payload views.UpdateCategory
	if err = c.Bind(&payload); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	if err = validation.ValidateStruct(payload); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}

	category, err := h.categoryService.UpdateCategory(c.Request().Context(), payload, id)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, category)
}

// deleteCategory godoc
//
// @Summary      Delete a category
// @Description  Deletes an existing product category by ID
// @Tags         Category
// @Accept       json
// @Produce      json
// @Param        id       path      int64     true  "Category ID"
// @Success      204  {object}  map[string]any
// @Failure      401  {object}  map[string]any  "Unauthorized"
// @Failure      403  {object}  map[string]any  "Forbidden"
// @Failure      404  {object}  map[string]any
// @Failure      500  {object}  map[string]any
//
//	@Security		ApiKeyAuth
//
// @Router       /categories/{id} [delete]
func (h *CategoryHandler) deleteCategory(c echo.Context) error {
	id, err := utils.ParseStringToInt(c.Param("id"))
	if err != nil {
		return errorResponse.BadRequest(constants.EntityDoesNotExist, err)
	}
	err = h.categoryService.DeleteCategory(c.Request().Context(), id)
	if err != nil {
		return err
	}
	return c.NoContent(http.StatusNoContent)
}

// getCategoryByID godoc
//
// @Summary      Get a category by ID
// @Description  Retrieves an existing product category by ID
// @Tags         Category
// @Accept       json
// @Produce      json
// @Param        id       path      int64     true  "Category ID"
// @Success      200  {object}  views.CategoryResponse
// @Failure      401  {object}  map[string]any  "Unauthorized"
// @Failure      403  {object}  map[string]any  "Forbidden"
// @Failure      404  {object}  map[string]any
// @Failure      500  {object}  map[string]any
//
//	@Security		ApiKeyAuth
//
// @Router       /categories/{id} [get]
func (h *CategoryHandler) getCategoryByID(c echo.Context) error {
	id, err := utils.ParseStringToInt(c.Param("id"))
	if err != nil {
		return errorResponse.BadRequest(constants.EntityDoesNotExist, err)
	}

	category, err := h.categoryService.GetCategoryByID(c.Request().Context(), id)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, category)
}

// getAllCategories godoc
//
// @Summary      Get all categories
// @Description  Retrieves all product categories
// @Tags         Category
// @Accept       json
// @Produce      json
//
//	@Param			sorted 	query		bool	false	"sorted"
//
//	@Param			category_number 	query		int64	false	"category_number"
//
// @Success      200  {array}  views.CategoryResponse
// @Failure      401  {object}  map[string]any  "Unauthorized"
// @Failure      403  {object}  map[string]any  "Forbidden"
// @Failure      500  {object}  map[string]any
//
//	@Security		ApiKeyAuth
//
// @Router       /categories [get]
func (h *CategoryHandler) getAllCategories(c echo.Context) error {
	var q views.ListCategoryQueryParams
	if err := c.Bind(&q); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	if err := validation.ValidateStruct(q); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	categories, err := h.categoryService.GetAllCategories(c.Request().Context(), q)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, categories)
}
