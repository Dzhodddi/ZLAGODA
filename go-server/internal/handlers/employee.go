package handlers

import (
	"net/http"

	"github.com/Dzhodddi/ZLAGODA/internal/services"
	"github.com/labstack/echo/v4"
)

type EmployeeHandler struct {
	employeeService services.EmployeeService
}

func NewEmployeeHandler(employeeService services.EmployeeService) *EmployeeHandler {
	return &EmployeeHandler{
		employeeService: employeeService,
	}
}

func (h *EmployeeHandler) RegisterRouts(e *echo.Group) {
	category := e.Group("/employees")
	category.GET("/list", h.getAllEmployeeIDList)
}

func (h *EmployeeHandler) getAllEmployeeIDList(c echo.Context) error {
	employeeIDList, err := h.employeeService.GetEmployeeIDList(c.Request().Context())
	if err != nil {
		return err
	}
	return c.JSON(http.StatusOK, employeeIDList)
}
