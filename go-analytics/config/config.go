package config

import (
	"github.com/spf13/viper"
)

type Config struct {
	Kafka     KafkaConfig     `mapstructure:"kafka"`
	Cassandra CassandraConfig `mapstructure:"cassandra"`
}

type KafkaConfig struct {
	Brokers []string `mapstructure:"brokers"`
	Topic   string   `mapstructure:"topic"`
	GroupID string   `mapstructure:"group_id"`
}

type CassandraConfig struct {
	Hosts    []string `mapstructure:"hosts"`
	Keyspace string   `mapstructure:"keyspace"`
	Username string   `mapstructure:"username"`
	Password string   `mapstructure:"password"`
}


// loads from config with defaults
func Load() (*Config, error) {
	viper.SetConfigName("config")
	viper.SetConfigType("yaml")
	viper.AddConfigPath(".")
	viper.AddConfigPath("./config")

	// Set defaults
	viper.SetDefault("kafka.brokers", []string{"localhost:9092"})
	viper.SetDefault("kafka.topic", "transactions")
	viper.SetDefault("kafka.group_id", "go-analytics-group")
	viper.SetDefault("cassandra.hosts", []string{"127.0.0.1:9042"})
	viper.SetDefault("cassandra.keyspace", "midas_analytics")
	viper.SetDefault("cassandra.username", "") // none required for local dev
	viper.SetDefault("cassandra.password", "") // none required for local dev

	if err := viper.ReadInConfig(); err != nil {
		// Use defaults if config file not found
		if _, ok := err.(viper.ConfigFileNotFoundError); !ok {
			return nil, err
		}
	}

	var config Config

	if err := viper.Unmarshal(&config); err != nil {
		return nil, err
	}

	return &config, nil
}

// NewKafkaConfig returns Kafka configuration with defaults (can changed if needed)
func NewKafkaConfig() *KafkaConfig {
	return &KafkaConfig{
		Brokers: []string{"localhost:9092"},
		Topic:   "transactions",
		GroupID: "go-analytics-group",
	}
}

// NewCassandraConfig returns Cassandra configuration with defaults (can change if needed)
func NewCassandraConfig() *CassandraConfig {
	return &CassandraConfig{
		Hosts:    []string{"127.0.0.1:9042"},
		Keyspace: "midas_analytics",
		Username: "",
		Password: "",
	}
}
