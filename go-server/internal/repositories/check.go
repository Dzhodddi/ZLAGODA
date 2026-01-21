package repository

import (
	"context"
	"fmt"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	errorResponse "github.com/Dzhodddi/ZLAGODA/internal/errors"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

type CheckRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewCheckRepository(db *sqlx.DB) *CheckRepository {
	return &CheckRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

// TODO [add calculation of totat sum of products, deletion from store_product table and create and insert data into check_product table]
func (r *CheckRepository) CreateNewCheck(
	ctx context.Context,
	check views.CreateNewCheck,
	printTime time.Time,
	formattedSqlPayload string,
	payload []interface{},
	keys []string,
) (*generated.Check, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()
	err := r.isPossibleToCreateCheck(ctx, formattedSqlPayload, len(keys), append([]interface{}{keys}, payload...))
	if err != nil {
		return nil, err
	}
	newCheck, err := r.queries.CreateNewCheck(
		ctx,
		generated.CreateNewCheckParams{
			CheckNumber: check.CheckNumber,
			IDEmployee:  check.IDEmployee,
			CardNumber:  check.CardNumber,
			PrintDate:   printTime,
			SumTotal:    0,
			Vat:         check.VAT,
		},
	)
	if err != nil {
		if pgErr, ok := err.(*pq.Error); ok {
			switch pgErr.Code {
			case "23503": // Foreign key constraint violation
				return nil, errorResponse.BadForeignKey()
			case "23505": // Unique constraint violation
				return nil, errorResponse.Conflict()
			}
		}
		return nil, err
	}
	return &newCheck, nil
}

func (r *CheckRepository) isPossibleToCreateCheck(ctx context.Context, formattedSqlPayload string, resultLen int, args []any) error {
	var count int
	query, args, err := sqlx.In(fmt.Sprintf(`
	WITH
	found_products AS (
	  SELECT upc
	  FROM store_product
	  WHERE upc IN (?)
	),
	payload (upc, quantity) AS (
	  VALUES %s
	)
	SELECT COUNT(*)
	FROM found_products f
	JOIN store_product s ON s.upc = f.upc
	JOIN payload p ON p.upc = f.upc
	WHERE s.products_number >= p.quantity;
`, formattedSqlPayload), args...)
	if err != nil {
		return err
	}
	query = r.db.Rebind(query)
	err = r.db.QueryRowxContext(ctx, query, args...).Scan(&count)
	if err != nil {
		return err
	}
	if count != resultLen {
		return errorResponse.BadForeignKey()
	}
	return nil
}

func (r *CheckRepository) calculateProductSum(ctx context.Context) (float64, error) {
	return 0, nil
}
