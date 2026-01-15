package db

import (
	"context"
	"github.com/jmoiron/sqlx"
	"time"

	_ "github.com/lib/pq"
)

type PoolConfig struct {
	MaxOpenConnections int
	MaxIdleConnections int
	ConnMaxLifetime    time.Duration
}

type DatabaseConfig struct {
	Driver string
	DSN    string

	Pool *PoolConfig
}

func NewPostgresConnection(cfg DatabaseConfig) (*sqlx.DB, error) {
	db, err := sqlx.Open(cfg.Driver, cfg.DSN)
	if err != nil {
		return nil, err
	}
	if cfg.Pool != nil {
		db.SetMaxOpenConns(cfg.Pool.MaxOpenConnections)
		db.SetMaxIdleConns(cfg.Pool.MaxIdleConnections)
		db.SetConnMaxLifetime(cfg.Pool.ConnMaxLifetime)
	}
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err = db.PingContext(ctx); err != nil {
		_ = db.Close()
		return nil, err
	}

	return db, nil
}
