package handlers

import (
	"net/http"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/auth"
	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/services"
	validation "github.com/Dzhodddi/ZLAGODA/internal/validator"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/labstack/echo/v4"
)

type SaleHandler struct {
	saleService services.SaleService
	auth        auth.Authenticator
}

func NewSaleHandler(service services.SaleService, auth auth.Authenticator) *SaleHandler {
	return &SaleHandler{
		saleService: service,
		auth:        auth,
	}
}

func (h *SaleHandler) RegisterRouts(e *echo.Group) {
	sales := e.Group("/sales")
	sales.GET("", h.getAllSales, h.auth.CheckRole(auth.Manager))
}

// getAllSales godoc
//
// @Summary      Get all sales
// @Description  Retrieves all sales
// @Tags         Sale
// @Accept       json
// @Produce      json
//
//	@Param			start_date 	query		string	true	"start_date"
//
//	@Param			end_date 	query		string	true	"end_date"
//
// @Success      200  {array}  views.SaleResponse
// @Failure      401  {object}  map[string]any  "Unauthorized"
// @Failure      403  {object}  map[string]any  "Forbidden"
// @Failure      500  {object}  map[string]any
//
//	@Security		ApiKeyAuth
//
// @Router       /sales [get]
func (h *SaleHandler) getAllSales(c echo.Context) error {
	var q views.SaleListQueryParams
	if err := c.Bind(&q); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	startDate, endDate, err := h.validateQueryParams(&q)
	if err != nil {
		return nil
	}
	sales, err := h.saleService.GetAllSalesWithinDate(c.Request().Context(), *startDate, *endDate, q.LastCheckNumber)
	if err != nil {
		return nil
	}
	return c.JSON(http.StatusOK, sales)
}

func (h *SaleHandler) validateQueryParams(q *views.SaleListQueryParams) (*time.Time, *time.Time, error) {
	if err := validation.ValidateStruct(q); err != nil {
		return nil, nil, errorResponse.ValidationError(constants.ValidationError, err)
	}
	startDate, err := time.Parse(constants.DateLayout, q.StartDate)
	if err != nil {
		return nil, nil, errorResponse.BadRequest(constants.InvalidTimeFormat, err)
	}
	endDate, err := time.Parse(constants.DateLayout, q.EndDate)
	if err != nil {
		return nil, nil, errorResponse.BadRequest(constants.InvalidTimeFormat, err)
	}
	if startDate.After(endDate) {
		return nil, nil, errorResponse.BadRequest(constants.InvalidTimeFormat, err)
	}
	return &startDate, &endDate, nil
}
