package config

import (
	"fmt"
	"strings"
	"time"

	"github.com/spf13/viper"
)

type Env string

const (
	Dev  Env = "dev"
	Prod Env = "prod"
	Test Env = "test"
)

type Config struct {
	Env       Env    `mapstructure:"env"`
	Port      string `mapstructure:"port"`
	JWTSecret string `mapstructure:"jwt_secret"`
	JWTAud    string `mapstructure:"jwt_aud"`
	JWTIssuer string `mapstructure:"jwt_issuer"`

	PostgresDSN        string        `mapstructure:"postgres_dsn"`
	MaxOpenConnections int           `mapstructure:"db_max_open_connections"`
	MaxIdleConnections int           `mapstructure:"max_idle_connections"`
	ConnMaxLifetime    time.Duration `mapstructure:"connection_lifetime"`
}

func Load() (*Config, error) {
	v := viper.New()

	v.SetEnvPrefix("ZLAGODA")
	v.SetEnvKeyReplacer(strings.NewReplacer(".", "_", "-", "_"))
	v.AutomaticEnv()
	v.SetConfigFile(".env")
	v.SetConfigType("env")

	if err := v.ReadInConfig(); err != nil {
		return nil, fmt.Errorf("failed reading config: %v", err)
	}

	var config Config
	if err := v.Unmarshal(&config); err != nil {
		return nil, fmt.Errorf("failed unmarshalling config: %v", err)
	}
	return &config, nil
}
