package utils

import (
	"database/sql"
	"strconv"
)

func ToNullString(s *string) sql.NullString {
	if s == nil {
		return sql.NullString{Valid: false}
	}
	return sql.NullString{Valid: true, String: *s}
}

func SqlNullToString(s sql.NullString) *string {
	if !s.Valid {
		return nil
	}
	return &s.String
}

func ParseStringToInt(s string) (int64, error) {
	id, err := strconv.ParseInt(s, 10, 64)
	if err != nil {
		return -1, err
	}
	return id, nil
}
