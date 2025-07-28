package main

import (
	"fmt"
	"log"

	"github.com/jpmc/forge/go-analytics/config"
	"github.com/jpmc/forge/go-analytics/database"
)

func main() {

	fmt.Println("Testing cassandra connection setup...")

	cfg := config.NewCassandraConfig()

	if err := testCassandraConnection(cfg); err != nil {

		log.Fatalf("Connection test failed: %v", err)
	}

	fmt.Println("Cassandra connection successful!!")
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
