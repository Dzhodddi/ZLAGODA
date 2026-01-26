package repository

import (
	"context"
	"fmt"
	"time"

	"github.com/Dzhodddi/ZLAGODA/internal/constants"
	"github.com/Dzhodddi/ZLAGODA/internal/db/generated"
	"github.com/Dzhodddi/ZLAGODA/internal/views"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

type Product struct {
	SellingPrice float64 `db:"selling_price"`
	Quantity     int     `db:"quantity"`
	UPC          string  `db:"upc"`
}

type CheckStoreProduct struct {
	CheckNumber  string    `db:"check_number"`
	UPC          string    `db:"upc"`
	SellingPrice float64   `db:"selling_price"`
	Quantity     int       `db:"quantity"`
	CheckDate    time.Time `db:"check_date"`
}

type CheckRepository interface {
	CreateNewCheck(
		ctx context.Context,
		check views.CreateNewCheck,
		printTime time.Time,
		formattedSqlPayload string,
		payload []interface{},
		keys []string,
	) (*generated.Check, error)
	calculateCheckTotalSumIfValid(
		ctx context.Context,
		tx *sqlx.Tx,
		formattedSqlPayload string,
		resultLen int,
		args []any,
	) (*[]Product, float64, error)
	saveProducts(
		ctx context.Context,
		tx *sqlx.Tx,
		productPayload []CheckStoreProduct,
	) error
	reduceStoreProductQuantity(
		ctx context.Context,
		tx *sqlx.Tx,
		storeProducts []CheckStoreProduct,
	) error
}

type checkRepository struct {
	db      *sqlx.DB
	queries *generated.Queries
}

func NewCheckRepository(db *sqlx.DB) CheckRepository {
	return &checkRepository{
		db:      db,
		queries: generated.New(db.DB),
	}
}

func (r *checkRepository) CreateNewCheck(
	ctx context.Context,
	check views.CreateNewCheck,
	printTime time.Time,
	formattedSqlPayload string,
	payload []interface{},
	keys []string,
) (*generated.Check, error) {
	ctx, cancel := context.WithTimeout(ctx, constants.DatabaseTimeOut)
	defer cancel()

	tx, err := r.db.BeginTxx(ctx, nil)
	if err != nil {
		return nil, err
	}
	txQueries := generated.New(tx.Tx)
	defer func() {
		_ = tx.Rollback()
	}()
	productsList, totalPrice, err := r.calculateCheckTotalSumIfValid(ctx, tx, formattedSqlPayload, len(keys), append([]interface{}{keys}, payload...))
	if err != nil {
		return nil, err
	}
	newCheck, err := txQueries.CreateNewCheck(
		ctx,
		generated.CreateNewCheckParams{
			CheckNumber: check.CheckNumber,
			IDEmployee:  check.IDEmployee,
			CardNumber:  check.CardNumber,
			PrintDate:   printTime,
			SumTotal:    totalPrice,
			Vat:         check.VAT,
		},
	)
	if err != nil {
		if pgErr, ok := err.(*pq.Error); ok {
			switch pgErr.Code {
			case "23503": // Foreign key constraint violation
				return nil, ErrForeignKey
			case "23505": // Unique constraint violation
				return nil, ErrConflict
			}
		}
		return nil, err
	}
	storeProductList := make([]CheckStoreProduct, 0, len(*productsList))
	for _, p := range *productsList {
		storeProductList = append(storeProductList, CheckStoreProduct{
			CheckNumber:  check.CheckNumber,
			UPC:          p.UPC,
			SellingPrice: p.SellingPrice,
			Quantity:     p.Quantity,
			CheckDate:    printTime,
		})
	}
	err = r.saveProducts(ctx, tx, storeProductList)
	if err != nil {
		return nil, err
	}
	err = r.reduceStoreProductQuantity(ctx, tx, storeProductList)
	if err != nil {
		return nil, err
	}
	if err = tx.Commit(); err != nil {
		return nil, err
	}
	return &newCheck, nil
}

func (r *checkRepository) calculateCheckTotalSumIfValid(ctx context.Context, tx *sqlx.Tx, formattedSqlPayload string, resultLen int, args []any) (*[]Product, float64, error) {
	var products []Product
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
	SELECT s.selling_price, p.quantity, s.upc
	FROM found_products f
	JOIN store_product s ON s.upc = f.upc
	JOIN payload p ON p.upc = f.upc
	WHERE s.products_number >= p.quantity;
`, formattedSqlPayload), args...)
	if err != nil {
		return nil, -1, err
	}
	query = tx.Rebind(query)
	err = tx.SelectContext(ctx, &products, query, args...)
	if err != nil {
		return nil, -1, err
	}
	if len(products) != resultLen {
		return nil, -1, ErrForeignKey
	}
	totalPrice := 0.0
	for _, p := range products {
		totalPrice += p.SellingPrice * float64(p.Quantity)
	}
	return &products, totalPrice, nil
}

func (r *checkRepository) saveProducts(ctx context.Context, tx *sqlx.Tx, productPayload []CheckStoreProduct) error {
	query := `INSERT INTO check_store_product (check_number, UPC, selling_price, quantity, check_date) VALUES (:check_number, :upc, :selling_price, :quantity, :check_date)`
	_, err := tx.NamedExecContext(ctx, query, productPayload)
	return err
}

func (r *checkRepository) reduceStoreProductQuantity(ctx context.Context, tx *sqlx.Tx, storeProducts []CheckStoreProduct) error {
	query := `UPDATE store_product SET products_number = products_number - :quantity WHERE UPC = :upc`
	stmt, err := tx.PrepareNamedContext(ctx, query)
	if err != nil {
		return err
	}
	defer func() {
		_ = stmt.Close()
	}()

	for _, product := range storeProducts {
		if _, err = stmt.ExecContext(ctx, product); err != nil {
			return err
		}
	}
	return nil
}
