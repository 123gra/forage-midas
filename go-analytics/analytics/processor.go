package analytics

import (
	"fmt"
	"time"

	"github.com/jpmc/forge/go-analytics/database"
	"github.com/jpmc/forge/go-analytics/kafka"
	"github.com/sirupsen/logrus"
	"gopkg.in/inf.v0"
)

type Processor struct {
	db     *database.CassandraDB
	logger *logrus.Logger
	repo   *database.Repository
}

func NewProcessor(db *database.CassandraDB, logger *logrus.Logger) *Processor {

	return &Processor{

		db:     db,
		logger: logger,
		repo:   database.NewRepository(db.Session()),
	}
}

func (p *Processor) ProcessTransaction(transaction *kafka.AnalyticsTransaction) error {

	p.logger.Infof("processing analytics for transaction: %s", transaction.TransNum)

	riskScore := p.calculateRiskScore(transaction)

	// Convert float64 amount to inf.Dec for Cassandra
	amount := inf.NewDec(int64(transaction.Amt*100), 2) // Convert to cents

	metric := &database.TransactionMetric{
		TransactionID: transaction.TransNum,
		ProcessedAt:   time.Now(),
		Amount:        amount,
		Category:      transaction.Category,
		Merchant:      transaction.Merchant,
		IsFraud:       transaction.IsFraud,
		RiskScore:     riskScore,
		Location:      fmt.Sprintf("%s, %s", transaction.City, transaction.State),
	}

	// Save to database
	if err := p.repo.SaveTransactionMetric(metric); err != nil {
		return fmt.Errorf("failed to save transaction metric: %w", err)
	}

	// Update user behavior
	date := time.Now().Format("2006-01-02")
	if err := p.repo.UpdateUserBehavior(transaction.CcNum, date, transaction.Amt, transaction.IsFraud); err != nil {
		return fmt.Errorf("failed to update user behavior: %w", err)
	}

	// Save time-series metrics
	if err := p.saveTimeSeriesMetrics(transaction); err != nil {
		return fmt.Errorf("failed to save time-series metrics: %w", err)
	}

	// Check for fraud alerts
	if err := p.checkForAlerts(transaction, riskScore); err != nil {
		return fmt.Errorf("failed to check for alerts: %w", err)
	}

	p.logger.Infof("Successfully processed and saved transaction: %s", transaction.TransNum)
	return nil
}

func (p *Processor) saveTimeSeriesMetrics(transaction *kafka.AnalyticsTransaction) error {
	now := time.Now()
	hourBucket := now.Format("2006-01-02-15")
	dayBucket := now.Format("2006-01-02")

	metrics := []*database.TimeSeriesMetric{
		{
			MetricName: "transaction_volume",
			TimeBucket: hourBucket,
			Timestamp:  now,
			Value:      1.0, // 1 transaction
			Metadata:   map[string]string{"category": transaction.Category},
		},
		{
			MetricName: "transaction_amount",
			TimeBucket: hourBucket,
			Timestamp:  now,
			Value:      transaction.Amt, // Use float64 directly
			Metadata:   map[string]string{"category": transaction.Category},
		},
		{
			MetricName: "fraud_incidents",
			TimeBucket: dayBucket,
			Timestamp:  now,
			Value:      boolToFloat(transaction.IsFraud),
			Metadata:   map[string]string{"category": transaction.Category},
		},
		{
			MetricName: "average_transaction_amount",
			TimeBucket: hourBucket,
			Timestamp:  now,
			Value:      transaction.Amt, // Use float64 directly
			Metadata:   map[string]string{"category": transaction.Category},
		},
	}

	for _, metric := range metrics {
		if err := p.repo.SaveTimeSeriesMetric(metric); err != nil {
			return err
		}
	}

	return nil
}

func (p *Processor) calculateRiskScore(transaction *kafka.AnalyticsTransaction) *inf.Dec {

	riskScore := 0.0

	if transaction.IsFraud {
		riskScore += 50.0
	}

	if transaction.Amt > 1000 {
		riskScore += 20.0
	} else if transaction.Amt > 500 {
		riskScore += 10.0
	}

	// mapping high risk categories to their associated risk score
	highRiskCategories := map[string]float64{
		"electronics":    15.0,
		"jewelry":        20.0,
		"travel":         25.0,
		"gaming":         20.0,
		"cryptocurrency": 30.0,
	}

	if risk, exists := highRiskCategories[transaction.Category]; exists {
		riskScore += risk
	}

	// higher risk score in big cities
	if transaction.CityPop > 1000000 {
		riskScore += 5.0
	}

	// Late night transactions have higher risk score
	// Use the transaction's timestamp instead of the current time
	var hour int
	if transaction.UnixTime != 0 {
		hour = time.Unix(transaction.UnixTime, 0).Hour()
	} else if transaction.TransDateTransTime != "" {
		// Attempt to parse the TransDateTransTime if UnixTime is not available
		parsedTime, err := time.Parse("2006-01-02 15:04:05", transaction.TransDateTransTime)
		if err == nil {
			hour = parsedTime.Hour()
		}
	}
	if hour >= 22 || hour <= 6 {
		riskScore += 10
	}

	// Distance-based risk (if user location differs significantly from merchant)
	if transaction.Lat != 0 && transaction.Lon != 0 && transaction.MerchLat != 0 && transaction.MerchLon != 0 {
		distance := p.calculateDistance(transaction.Lat, transaction.Lon, transaction.MerchLat, transaction.MerchLon)
		if distance > 100 { // More than 100 km
			riskScore += 15.0
		}
	}

	// Convert float64 to inf.Dec (multiply by 100 to preserve 2 decimal places)
	riskScoreInt := int64(riskScore * 100)
	return inf.NewDec(riskScoreInt, 2)
}

func (p *Processor) checkForAlerts(transaction *kafka.AnalyticsTransaction, riskScore *inf.Dec) error {
	// Convert inf.Dec to float64 for comparison and storage
	riskScoreFloat := infDecToFloat(riskScore)

	// High risk score alert
	if riskScoreFloat > 70.0 {
		alert := &database.Alert{
			AlertID:       fmt.Sprintf("alert_%s_%d", transaction.TransNum, time.Now().Unix()),
			CreatedAt:     time.Now(),
			AlertType:     "high_risk_score",
			Severity:      "high",
			UserID:        transaction.CcNum,
			TransactionID: transaction.TransNum,
			Description:   fmt.Sprintf("High risk transaction detected. Risk score: %.2f", riskScoreFloat),
			RiskScore:     riskScoreFloat, // Use float64
			Status:        "open",
		}

		if err := p.repo.SaveAlert(alert); err != nil {
			return err
		}

		p.logger.Warnf("High risk alert created for transaction %s with risk score %.2f", transaction.TransNum, riskScoreFloat)
	}

	// Large amount alert
	if transaction.Amt > 2000 {
		alert := &database.Alert{
			AlertID:       fmt.Sprintf("alert_%s_%d", transaction.TransNum, time.Now().Unix()),
			CreatedAt:     time.Now(),
			AlertType:     "large_amount",
			Severity:      "medium",
			UserID:        transaction.CcNum,
			TransactionID: transaction.TransNum,
			Description:   fmt.Sprintf("Large transaction amount detected: $%.2f", transaction.Amt),
			RiskScore:     riskScoreFloat, // Use float64
			Status:        "open",
		}

		if err := p.repo.SaveAlert(alert); err != nil {
			return err
		}
	}

	return nil
}

func (p *Processor) calculateDistance(lat1, lon1, lat2, lon2 float64) float64 {

	// This is a simplified version for demonstration
	deltaLat := lat2 - lat1
	deltaLon := lon2 - lon1

	// Convert to kilometers (rough approximation)
	distance := (deltaLat*deltaLat + deltaLon*deltaLon) * 111.0

	return distance
}

func boolToFloat(b bool) float64 {
	if b {
		return 1.0
	}
	return 0.0
}

func infDecToFloat(d *inf.Dec) float64 {
	if d == nil {
		return 0.0
	}
	// Convert inf.Dec to float64 (simplified conversion)
	// In a real implementation, you might want more precise conversion
	return float64(d.UnscaledBig().Int64()) / float64(inf.NewDec(1, d.Scale()).UnscaledBig().Int64())
}
