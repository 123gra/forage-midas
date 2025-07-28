package main

import (
	"fmt"
	"log"
	"time"

	"github.com/jpmc/forge/go-analytics/config"
	"github.com/jpmc/forge/go-analytics/database"
	"gopkg.in/inf.v0"
)

func main() {

	fmt.Println("Testing cassandra connection setup...")

	// Load configuration
	// could load only cassandra, but want to test kafka running as well.
	cfg, err := config.Load()
	if err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	if err := testCassandraConnection(&cfg.Cassandra); err != nil {

		log.Fatalf("Connection test failed: %v", err)
	}

	fmt.Println("Cassandra connection successful!!")

	// Test SaveTransactionMetric function
	if err := testSaveTransactionMetric(&cfg.Cassandra); err != nil {
		log.Fatalf("SaveTransactionMetric test failed: %v", err)
	}

	fmt.Println("SaveTransactionMetric test successful!!")
}

func testCassandraConnection(cfg *config.CassandraConfig) error {

	db, err := database.NewCassandraConnection(*cfg)

	if err != nil {
		return fmt.Errorf("failed to connect: %w", err)
	}

	defer db.Close()

	// testing some query execution
	var version string

	if err := db.Session().Query("Select release_version from system.local;").Scan(&version); err != nil {
		return fmt.Errorf("failed to query system.local: %w", err)
	}

	fmt.Printf("connected to cassandra version: %s\n", version)

	return nil
}

func testSaveTransactionMetric(cfg *config.CassandraConfig) error {
	// Create database connection
	db, err := database.NewCassandraConnection(*cfg)
	if err != nil {
		return fmt.Errorf("failed to connect: %w", err)
	}
	defer db.Close()

	// Create repository
	repo := database.NewRepository(db.Session())

	// Create test transaction metric
	testMetric := &database.TransactionMetric{
		TransactionID: "test-txn-001",
		ProcessedAt:   time.Now(),
		Amount:        inf.NewDec(15075, 2), // 150.75
		Category:      "electronics",
		Merchant:      "Best Buy",
		IsFraud:       false,
		RiskScore:     inf.NewDec(15, 2), // 0.15
		Location:      "New York, NY",
	}

	fmt.Printf("Saving test transaction: %s, Amount: $%.2f, Category: %s\n",
		testMetric.TransactionID, testMetric.Amount.String(), testMetric.Category)

	// Save the transaction metric
	if err := repo.SaveTransactionMetric(testMetric); err != nil {
		return fmt.Errorf("failed to save transaction metric: %w", err)
	}

	fmt.Println("Transaction metric saved successfully!")

	// Verify the data was saved by retrieving it
	var retrievedMetric database.TransactionMetric
	query := `SELECT transaction_id, processed_at, amount, category, merchant, is_fraud, risk_score, location 
			  FROM midas_analytics.transaction_metrics WHERE transaction_id = ?`

	if err := db.Session().Query(query, testMetric.TransactionID).Scan(
		&retrievedMetric.TransactionID,
		&retrievedMetric.ProcessedAt,
		&retrievedMetric.Amount,
		&retrievedMetric.Category,
		&retrievedMetric.Merchant,
		&retrievedMetric.IsFraud,
		&retrievedMetric.RiskScore,
		&retrievedMetric.Location,
	); err != nil {
		return fmt.Errorf("failed to retrieve transaction metric: %w", err)
	}

	fmt.Printf("Retrieved transaction: %s, Amount: $%s, Category: %s\n",
		retrievedMetric.TransactionID, retrievedMetric.Amount.String(), retrievedMetric.Category)

	return nil
}
