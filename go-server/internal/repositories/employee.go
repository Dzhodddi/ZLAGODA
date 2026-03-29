package repository

import (
	"context"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/jmoiron/sqlx"
)

type EmployeeRepository interface {
	GetEmployeeByID(ctx context.Context, employeeID string) (generated.GetEmployeeByIDRow, error)
	GetEmployeeIDList(ctx context.Context) ([]generated.GetEmployeeIDListRow, error)
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
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	return r.queries.GetEmployeeByID(ctx, employeeID)
}

func (r *employeeRepository) GetEmployeeIDList(ctx context.Context) ([]generated.GetEmployeeIDListRow, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	return r.queries.GetEmployeeIDList(ctx)
}
