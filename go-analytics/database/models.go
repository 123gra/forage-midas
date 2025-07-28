package database

// cassandra data models

import (
	"time"
)

// TransactionMetric represents a transaction metric stored in Cassandra
type TransactionMetric struct {
	TransactionID string    `cql:"transaction_id"`
	ProcessedAt   time.Time `cql:"processed_at"`
	Amount        float64   `cql:"amount"`
	Category      string    `cql:"category"`
	Merchant      string    `cql:"merchant"`
	IsFraud       bool      `cql:"is_fraud"`
	RiskScore     float64   `cql:"risk_score"`
	Location      string    `cql:"location"`
}

// UserBehavior represents user behavior metrics
type UserBehavior struct {
	UserID            string `cql:"user_id"`
	Date              string `cql:"date"`
	TotalTransactions int64  `cql:"total_transactions"`
	TotalAmount       int64  `cql:"total_amount"`
	FraudCount        int64  `cql:"fraud_count"`
}

// TimeSeriesMetric represents time-series analytics data
type TimeSeriesMetric struct {
	MetricName string            `cql:"metric_name"`
	TimeBucket string            `cql:"time_bucket"`
	Timestamp  time.Time         `cql:"timestamp"`
	Value      float64           `cql:"value"`
	Metadata   map[string]string `cql:"metadata"`
}

// TransactionSummary represents aggregated transaction data
type TransactionSummary struct {
	Date              string  `cql:"date"`
	Category          string  `cql:"category"`
	TotalTransactions int64   `cql:"total_transactions"`
	TotalAmount       float64 `cql:"total_amount"`
	AverageAmount     float64 `cql:"average_amount"`
	FraudCount        int64   `cql:"fraud_count"`
	FraudPercentage   float64 `cql:"fraud_percentage"`
}

// RiskProfile represents user risk assessment
type RiskProfile struct {
	UserID           string    `cql:"user_id"`
	LastUpdated      time.Time `cql:"last_updated"`
	RiskScore        float64   `cql:"risk_score"`
	RiskLevel        string    `cql:"risk_level"`
	TransactionCount int64     `cql:"transaction_count"`
	TotalAmount      float64   `cql:"total_amount"`
	FraudCount       int64     `cql:"fraud_count"`
	Categories       []string  `cql:"categories"`
}

// MerchantAnalytics represents merchant-specific analytics
type MerchantAnalytics struct {
	Merchant         string   `cql:"merchant"`
	Date             string   `cql:"date"`
	TransactionCount int64    `cql:"transaction_count"`
	TotalAmount      float64  `cql:"total_amount"`
	AverageAmount    float64  `cql:"average_amount"`
	FraudCount       int64    `cql:"fraud_count"`
	FraudRate        float64  `cql:"fraud_rate"`
	Categories       []string `cql:"categories"`
}

// LocationAnalytics represents location-based analytics
type LocationAnalytics struct {
	Location         string  `cql:"location"`
	Date             string  `cql:"date"`
	TransactionCount int64   `cql:"transaction_count"`
	TotalAmount      float64 `cql:"total_amount"`
	AverageAmount    float64 `cql:"average_amount"`
	FraudCount       int64   `cql:"fraud_count"`
	FraudRate        float64 `cql:"fraud_rate"`
	Population       int     `cql:"population"`
}

// DailyMetrics represents daily aggregated metrics
type DailyMetrics struct {
	Date              string   `cql:"date"`
	TotalTransactions int64    `cql:"total_transactions"`
	TotalAmount       float64  `cql:"total_amount"`
	AverageAmount     float64  `cql:"average_amount"`
	FraudCount        int64    `cql:"fraud_count"`
	FraudRate         float64  `cql:"fraud_rate"`
	UniqueUsers       int64    `cql:"unique_users"`
	UniqueMerchants   int64    `cql:"unique_merchants"`
	TopCategories     []string `cql:"top_categories"`
}

// HourlyMetrics represents hourly aggregated metrics
type HourlyMetrics struct {
	Date              string  `cql:"date"`
	Hour              int     `cql:"hour"`
	TotalTransactions int64   `cql:"total_transactions"`
	TotalAmount       float64 `cql:"total_amount"`
	AverageAmount     float64 `cql:"average_amount"`
	FraudCount        int64   `cql:"fraud_count"`
	FraudRate         float64 `cql:"fraud_rate"`
	PeakHour          bool    `cql:"peak_hour"`
}

// CategoryAnalytics represents category-specific analytics
type CategoryAnalytics struct {
	Category         string   `cql:"category"`
	Date             string   `cql:"date"`
	TransactionCount int64    `cql:"transaction_count"`
	TotalAmount      float64  `cql:"total_amount"`
	AverageAmount    float64  `cql:"average_amount"`
	FraudCount       int64    `cql:"fraud_count"`
	FraudRate        float64  `cql:"fraud_rate"`
	RiskLevel        string   `cql:"risk_level"`
	TopMerchants     []string `cql:"top_merchants"`
}

// Alert represents fraud or risk alerts
type Alert struct {
	AlertID       string    `cql:"alert_id"`
	CreatedAt     time.Time `cql:"created_at"`
	AlertType     string    `cql:"alert_type"`
	Severity      string    `cql:"severity"`
	UserID        string    `cql:"user_id"`
	TransactionID string    `cql:"transaction_id"`
	Description   string    `cql:"description"`
	RiskScore     float64   `cql:"risk_score"`
	Status        string    `cql:"status"`
	ResolvedAt    time.Time `cql:"resolved_at"`
	ResolvedBy    string    `cql:"resolved_by"`
}
