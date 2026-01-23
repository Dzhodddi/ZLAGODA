package handlers

import (
	"context"
	"net/http"

	"github.com/labstack/echo/v4"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/services"
	validation "github.com/Dzhodddi/ZLAGODA/internal/validator"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
)

type SaleHandler struct {
	service services.SaleService
}

func NewSaleHandler(service services.SaleService) *SaleHandler {
	return &SaleHandler{service: service}
}

func (h *SaleHandler) RegisterRoutes(e *echo.Group) {
	sale := e.Group("/sales")
	sale.POST("", h.createNewSale)
}

// createNewSale godoc
//
// @Summary      Create a new createNewSale
// @Description  Creates a new sale
// @Tags         Sales
// @Accept       json
// @Produce      json
// @Param        payload  body      views.CreateNewSale  true  "Sale data"
// @Success      201  {object}  views.SaleResponse
// @Failure      400  {object}  map[string]any  "Invalid request payload"
// @Failure      422  {object}  map[string]any  "Validation error"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /sales [post]
func (h *SaleHandler) createNewSale(c echo.Context) error {
	var sale views.CreateNewSale
	if err := c.Bind(&sale); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	if err := validation.ValidateStruct(&sale); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}

	newSale, err := h.service.CreateNewSale(context.Background(), sale)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusCreated, newSale)
}
