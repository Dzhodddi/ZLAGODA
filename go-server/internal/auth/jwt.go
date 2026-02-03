package auth

import (
	"fmt"
	"github.com/Dzhodddi/ZLAGODA/internal/config"
	"github.com/golang-jwt/jwt/v5"
)

type JWTAuth struct {
	secret string
	aud    string
	iss    string
}

func NewJWTAuth(config *config.Config) *JWTAuth {
	return &JWTAuth{
		secret: config.JWTSecret,
		aud:    config.JWTAud,
		iss:    config.JWTIssuer,
	}
}

func (auth *JWTAuth) ValidateToken(tokenString string) (*jwt.Token, error) {
	return jwt.Parse(tokenString, func(token *jwt.Token) (any, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(auth.secret), nil
	},
		jwt.WithExpirationRequired(),
		jwt.WithAudience(auth.aud),
		jwt.WithIssuer(auth.iss),
		jwt.WithValidMethods([]string{jwt.SigningMethodHS512.Name}),
	)
}
