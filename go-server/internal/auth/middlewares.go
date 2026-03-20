package auth

import (
	"context"
	"fmt"
	"net/http"
	"strings"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

type employeeKey string

const employeeCtx employeeKey = "employee"

type RoleKey string

const RoleCtx RoleKey = "role"

const (
	Manager RoleKey = "MANAGER"
	Cashier RoleKey = "CASHIER"
)

func (auth *JWTAuth) AuthMiddleware(employeeRepo repository.EmployeeRepository) echo.MiddlewareFunc {
	return func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			authHeader := c.Request().Header.Get("Authorization")
			if authHeader == "" {
				return errorResponse.UnAuthorized(fmt.Errorf(constants.InvalidAuthHeader))
			}

			parts := strings.SplitN(authHeader, " ", 2)
			if len(parts) != 2 || parts[0] != "Bearer" {
				return errorResponse.UnAuthorized(fmt.Errorf(constants.InvalidAuthHeader))
			}
			jwtToken, err := auth.ValidateToken(parts[1])
			if err != nil {
				return errorResponse.UnAuthorized(err)
			}
			employeeID, ok := jwtToken.Claims.(jwt.MapClaims)["sub"].(string)
			if !ok || err != nil {
				return errorResponse.UnAuthorized(err)
			}
			employee, err := employeeRepo.GetEmployeeByID(context.Background(), employeeID)
			if err != nil {
				return errorResponse.UnAuthorized(err)
			}
			ctx := c.Request().Context()
			ctx = context.WithValue(ctx, employeeCtx, employee.IDEmployee)
			ctx = context.WithValue(ctx, RoleCtx, employee.EmplRole)
			c.SetRequest(c.Request().WithContext(ctx))
			return next(c)
		}
	}
}

func (auth *JWTAuth) CheckRole(requiredRoles ...RoleKey) echo.MiddlewareFunc {
	return func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			employeeRole := GetRoleFromCtx(c.Request())
			for _, role := range requiredRoles {
				if employeeRole == string(role) {
					return next(c)
				}
			}
			return errorResponse.Forbidden(fmt.Errorf(constants.Forbidden))
		}
	}
}

func GetRoleFromCtx(r *http.Request) string {
	role, _ := r.Context().Value(RoleCtx).(string)
	return role
}
