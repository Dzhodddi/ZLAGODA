package handlers

import (
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/services"
	validation "github.com/Dzhodddi/ZLAGODA/internal/validator"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/labstack/echo/v4"
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
	card.GET("", h.listCustomerCards)
	cardNumber := card.Group("/:cardNumber")
	cardNumber.GET("", h.getCustomerCard)
	cardNumber.PUT("", h.updateCustomerCard)
	cardNumber.DELETE("", h.deleteCustomerCard)
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

// getCustomerCard godoc
//
// @Summary      Get a customer card by number
// @Description  Retrieves a customer discount card by its number
// @Tags         CustomerCard
// @Accept       json
// @Produce      json
// @Param        cardNumber path string true "Customer card number"
// @Success      200  {object}  views.CustomerCardResponse
// @Failure      404  {object}  map[string]any  "Entity not found"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /customer-cards/{cardNumber} [get]
func (h *CardHandler) getCustomerCard(c echo.Context) error {
	cardNumber := c.Param("cardNumber")
	card, err := h.service.GetCustomerCard(c.Request().Context(), cardNumber)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, card)
}

// updateCustomerCard godoc
//
// @Summary      Update a customer card
// @Description  Updates an existing customer discount card
// @Tags         CustomerCard
// @Accept       json
// @Produce      json
// @Param        cardNumber path string true "Customer card number"
// @Param        payload  body      views.UpdateCustomerCard  true  "Customer card data"
// @Success      200  {object}  views.CustomerCardResponse
// @Failure      400  {object}  map[string]any  "Validation error"
// @Failure      404  {object}  map[string]any  "Entity not found"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /customer-cards/{cardNumber} [put]
func (h *CardHandler) updateCustomerCard(c echo.Context) error {
	cardNumber := c.Param("cardNumber")
	var payload views.UpdateCustomerCard
	if err := c.Bind(&payload); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	if err := validation.ValidateStruct(payload); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	card, err := h.service.UpdateCustomerCard(c.Request().Context(), payload, cardNumber)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, card)
}

// deleteCustomerCard godoc
//
// @Summary      Delete a customer card
// @Description  Deletes an existing customer discount card
// @Tags         CustomerCard
// @Accept       json
// @Produce      json
// @Param        cardNumber path string true "Customer card number"
// @Success      204  {object}  map[string]any
// @Failure      404  {object}  map[string]any  "Entity not found"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /customer-cards/{cardNumber} [delete]
func (h *CardHandler) deleteCustomerCard(c echo.Context) error {
	cardNumber := c.Param("cardNumber")
	err := h.service.DeleteCustomerCard(c.Request().Context(), cardNumber)
	if err != nil {
		return err
	}
	return c.NoContent(http.StatusNoContent)
}

// listCustomerCards godoc
//
// @Summary      List all customer cards
// @Description  Retrieves a list of all customer discount cards
// @Tags         CustomerCard
// @Accept       json
// @Produce      json
//
//	@Param			percent	query		int	false	"percent"
//	@Param			sorted 	query		bool	false	"sorted"
//
// @Success      200  {array}  views.CustomerCardResponse
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /customer-cards [get]
func (h *CardHandler) listCustomerCards(c echo.Context) error {
	var q views.ListQueryParams
	if err := c.Bind(&q); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	if err := validation.ValidateStruct(q); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	cards, err := h.service.ListCustomerCards(c.Request().Context(), q)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, cards)
}
