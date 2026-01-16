package handlers

import (
	"net/http"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/services"
	validation "github.com/Dzhodddi/ZLAGODA/internal/validator"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/labstack/echo/v4"
)

type CheckHandler struct {
	checkService services.CheckService
}

func NewCheckHandler(checkService services.CheckService) *CheckHandler {
	return &CheckHandler{
		checkService: checkService,
	}
}

func (h *CheckHandler) RegisterRouts(e *echo.Group) {
	check := e.Group("/checks")
	check.POST("", h.createCheck)
}

// createCheck godoc
//
// @Summary      Create a new check
// @Description  Creates a new check
// @Tags         Checks
// @Accept       json
// @Produce      json
// @Param        payload  body      views.CreateNewCheck  true  "Check data"
// @Success      201  {object}  views.CheckResponse
// @Failure      400  {object}  map[string]any  "Invalid request payload"
// @Failure      422  {object}  map[string]any  "Validation error"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /checks [post]
func (h *CheckHandler) createCheck(c echo.Context) error {
	var payload views.CreateNewCheck
	if err := c.Bind(&payload); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	if err := validation.ValidateStruct(payload); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	printTime, err := time.Parse(time.RFC3339, payload.PrintDate)
	if err != nil {
		return errorResponse.BadRequest(constants.InvalidTimeFormat, err)
	}
	check, err := h.checkService.CreateCheck(c.Request().Context(), payload, printTime)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusCreated, check)
}
