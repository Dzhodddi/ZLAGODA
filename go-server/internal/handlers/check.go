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
	checkNumberGroup := check.Group("/:checkNumber")
	checkNumberGroup.DELETE("", h.deleteCheck)
	checkNumberGroup.GET("", h.getCheckWithProducts)
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

// deleteCheck godoc
//
// @Summary      Delete a check by check number
// @Description  Delete an existing check by check number
// @Tags         Checks
// @Accept       json
// @Produce      json
// @Param        checkNumber path string true "Check number"
// @Success      204  {object}  map[string]any
// @Failure      404  {object}  map[string]any  "Entity not found"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /checks/{checkNumber} [delete]
func (h *CheckHandler) deleteCheck(c echo.Context) error {
	checkNumber := c.Param("checkNumber")
	if err := h.checkService.DeleteCheck(c.Request().Context(), checkNumber); err != nil {
		return err
	}
	return c.NoContent(http.StatusNoContent)
}

// getCheckWithProducts godoc
//
// @Summary      Get a check by check number with products list
// @Description  Get a check by check number with products list
// @Tags         Checks
// @Accept       json
// @Produce      json
// @Param        checkNumber path string true "Check number"
// @Success      200  {object}	views.CheckResponseWithProducts
// @Failure      404  {object}  map[string]any  "Entity not found"
// @Failure      500  {object}  map[string]any  "Internal server error"
// @Router       /checks/{checkNumber} [get]
func (h *CheckHandler) getCheckWithProducts(c echo.Context) error {
	checkNumber := c.Param("checkNumber")
	check, err := h.checkService.GetCheck(c.Request().Context(), checkNumber)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, check)
}
