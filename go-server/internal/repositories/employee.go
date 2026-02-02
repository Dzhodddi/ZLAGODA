package repository

import (
	"context"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/jmoiron/sqlx"
	"time"
)

type EmployeeRepository interface {
	GetEmployeeByID(ctx context.Context, employeeID string) (generated.GetEmployeeByIDRow, error)
}

type employeeRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewEmployeeRepository(db *sqlx.DB) EmployeeRepository {
	return &employeeRepository{
		db:      db,
		queries: generated.New(db),
	}
}

func (r *employeeRepository) GetEmployeeByID(ctx context.Context, employeeID string) (generated.GetEmployeeByIDRow, error) {
	ctx, cancel := context.WithTimeout(ctx, 5*time.Second)
	defer cancel()

	return r.queries.GetEmployeeByID(ctx, employeeID)
}
