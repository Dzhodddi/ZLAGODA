package auth

import (
	repository "github.com/Dzhodddi/ZLAGODA/internal/repositories"
	"github.com/golang-jwt/jwt/v5"
	"github.com/labstack/echo/v4"
)

type Authenticator interface {
	ValidateToken(token string) (*jwt.Token, error)
	CheckRole(requiredRoles ...RoleKey) echo.MiddlewareFunc
	AuthMiddleware(employeeRepo repository.EmployeeRepository) echo.MiddlewareFunc
}
