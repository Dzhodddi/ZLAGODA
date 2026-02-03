package mocks

import (
	"github.com/Dzhodddi/ZLAGODA/internal/auth"
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

type MockAuth struct {
}

func NewMockAuth() auth.Authenticator {
	return MockAuth{}
}

func (m MockAuth) ValidateToken(_ string) (*jwt.Token, error) {
	return nil, nil
}

func (m MockAuth) CheckRole(_ ...auth.RoleKey) echo.MiddlewareFunc {
	return func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			return next(c)
		}
	}
}

func (m MockAuth) AuthMiddleware(employeeRepo repository.EmployeeRepository) echo.MiddlewareFunc {
	return func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			return next(c)
		}
	}
}
