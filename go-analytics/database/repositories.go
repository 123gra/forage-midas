package database

// data access layer

import (
	//"fmt"
	//"time"

	gocql "github.com/apache/cassandra-gocql-driver/v2"
)

type Repository struct {
	session *gocql.Session
}

func NewRepository(session *gocql.Session) *Repository {

	return &Repository{session: session}
}

// SaveTransactionMetric saves a transaction metric to Cassandra
func (r *Repository) SaveTransactionMetric(metric *TransactionMetric) error {

	query := `
	INSERT INTO midas_analytics.transaction_metrics
	(transaction_id, processed_at, amount, category, merchant, is_fraud, risk_score, location)
	VALUES (?, ?, ?, ?, ?, ?, ?, ?)
	`

	return r.session.Query(query,
		metric.TransactionID,
		metric.ProcessedAt,
		metric.Amount,
		metric.Category,
		metric.Merchant,
		metric.IsFraud,
		metric.RiskScore,
		metric.Location,
	).Exec()
}
