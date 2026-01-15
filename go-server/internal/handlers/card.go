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

type CardHandler struct {
	service services.CardService
}

func NewCardHandler(service services.CardService) *CardHandler {
	return &CardHandler{service: service}
}

func (h *CardHandler) RegisterRouts(route *echo.Group) {
	card := route.Group("/customer-cards")
	card.POST("", h.createNewCustomerCard)
}

// createNewCustomerCard godoc
//
// @Summary      Create a new customer card
// @Description  Creates a new customer discount card and stores it in the database
// @Tags         CustomerCard
// @Accept       json
// @Produce      json
// @Param        payload  body      views.CreateNewCustomerCard  true  "Customer card data"
// @Success      201  {object}  views.CustomerCardResponse
// @Failure      400  {object}  map[string]any  "Validation error"
// @Failure      422  {object}  map[string]any  "Validation error"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /customer-cards [post]
func (h *CardHandler) createNewCustomerCard(c echo.Context) error {
	var payload views.CreateNewCustomerCard
	if err := c.Bind(&payload); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	if err := validation.ValidateStruct(payload); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	card, err := h.service.CreateNewCustomerCard(c.Request().Context(), payload)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusCreated, card)
}
