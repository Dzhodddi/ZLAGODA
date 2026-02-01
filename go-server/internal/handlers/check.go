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
	"github.com/labstack/gommon/log"
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
	check.GET("", h.getCheckList)
	check.GET("/today", h.getCheckListWithinToday)
	check.GET("/price", h.getTotalPrice)
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

// getCheckList godoc
//
// @Summary      Get all checks
// @Description  Retrieves all checks
// @Tags         Checks
// @Accept       json
// @Produce      json
//
//	@Param			employee_id 	query		string	false	"employee_id"
//
//	@Param			start_date 	query		string	true	"start_date"
//
//	@Param			end_date 	query		string	true	"end_date"
//
// @Success      200  {array}  views.CheckListResponse
// @Failure 400	{object}  map[string]any
// @Failure      500  {object}  map[string]any
// @Router       /checks [get]
func (h *CheckHandler) getCheckList(c echo.Context) error {
	var q views.CheckListQueryParams
	if err := c.Bind(&q); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	startDate, endDate, err := h.validateQueryParams(&q)
	if err != nil {
		return err
	}
	checkList, err := h.checkService.GetCheckList(c.Request().Context(), q.EmployeeID, *startDate, *endDate)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, checkList)
}

// getCheckList godoc
//
// @Summary      Get all checks by specific cashier within today
// @Description  Retrieves all checks by specific cashier within today
// @Tags         Checks
// @Accept       json
// @Produce      json
//
//	@Param			employee_id 	query		string	true	"employee_id"
//
// @Success      200  {array}  views.CheckListResponse
// @Failure 400	{object}  map[string]any
// @Failure      500  {object}  map[string]any
// @Router       /checks/today [get]
func (h *CheckHandler) getCheckListWithinToday(c echo.Context) error {
	var q views.CheckListQueryWithThisDayParams
	if err := c.Bind(&q); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	if err := validation.ValidateStruct(q); err != nil {
		return errorResponse.ValidationError(constants.ValidationError, err)
	}
	timeNow := time.Now()
	today := time.Date(timeNow.Year(), timeNow.Month(), timeNow.Day(), 0, 0, 0, 0, timeNow.Location())
	log.Info(q.EmployeeID)
	checkList, err := h.checkService.GetCheckList(c.Request().Context(), &q.EmployeeID, today, today.Add(24*time.Hour-time.Nanosecond))
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, checkList)
}

// getTotalPrice godoc
//
// @Summary      Get total price of checks of all or specific cashier within date rage
// @Description  Retrieves total price of checks of all or specific cashier within date rage
// @Tags         Checks
// @Accept       json
// @Produce      json
//
//	@Param			employee_id 	query		string	false	"employee_id"
//
//	@Param			start_date 	query		string	true	"start_date"
//
//	@Param			end_date 	query		string	true	"end_date"
//
// @Success      200  {array}  map[string]float64
// @Failure 400	{object}  map[string]any
// @Failure      500  {object}  map[string]any
// @Router       /checks/price [get]
func (h *CheckHandler) getTotalPrice(c echo.Context) error {
	var q views.CheckListQueryParams
	if err := c.Bind(&q); err != nil {
		return errorResponse.BadRequest(constants.ValidationError, err)
	}
	startDate, endDate, err := h.validateQueryParams(&q)
	if err != nil {
		return err
	}
	totalPrice, err := h.checkService.GetTotalCheckPrice(c.Request().Context(), q, *startDate, *endDate)
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, map[string]interface{}{"total_price": totalPrice})
}

func (h *CheckHandler) validateQueryParams(q *views.CheckListQueryParams) (*time.Time, *time.Time, error) {
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
