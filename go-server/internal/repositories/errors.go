package repository

import "errors"

var (
	ErrNotFound         = errors.New("record not found")
	ErrConflict         = errors.New("record already exists")
	ErrForeignKey       = errors.New("referenced record does not exist")
	CashierDoesNotExist = errors.New("cashier does not exist")
)
