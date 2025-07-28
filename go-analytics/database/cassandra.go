package database

// cassandra connection.

import (
	"fmt"
	"time"

	"github.com/jpmc/forge/go-analytics/config"

	gocql "github.com/apache/cassandra-gocql-driver/v2"
	"github.com/sirupsen/logrus"
)

type CassandraDB struct {
	session *gocql.Session
	logger  *logrus.Logger
}

func NewCassandraConnection(cfg config.CassandraConfig) (*CassandraDB, error) {

	cluster := gocql.NewCluster(cfg.Hosts...)
	cluster.Keyspace = cfg.Keyspace
	cluster.Consistency = gocql.Quorum
	cluster.Timeout = 10 * time.Second
	cluster.ConnectTimeout = 10 * time.Second

	// check user and pass provided in the config file
	if cfg.Username != "" && cfg.Password != "" {
		cluster.Authenticator = gocql.PasswordAuthenticator{
			Username: cfg.Username,
			Password: cfg.Password,
		}
	}

	session, err := cluster.CreateSession()

	if err != nil {
		return nil, fmt.Errorf("failed to create cassandra connection: %w", err)
	}

	db := &CassandraDB{
		session: session,
		logger:  logrus.New(),
	}

	// Only initialize schema in development mode or when explicitly requested
	// In production, schema should be managed separately
	if cfg.InitializeSchema {
		if err := db.initializeSchema(); err != nil {
			session.Close()
			return nil, fmt.Errorf("failed to init schema: %w", err)
		}
	}

	return db, nil
}

func (db *CassandraDB) Close() {

	if db.session != nil {
		db.session.Close()
	}
}

func (db *CassandraDB) Session() *gocql.Session {
	return db.session
}

func (db *CassandraDB) initializeSchema() error {

	createKeyspaceQuery := `
        CREATE KEYSPACE IF NOT EXISTS analytics 
        WITH replication = {
            'class': 'SimpleStrategy',
            'replication_factor' : 3
        }
    `

	if err := db.session.Query(createKeyspaceQuery).Exec(); err != nil {
		return fmt.Errorf("Failed to create keyspace: %w", err)
	}

	tables := []string{
		createTransactionMetricsTable,
		createUserBehaviorTable,
		createTimeSeriesMetricsTable,
	}

	for _, table := range tables {

		if err := db.session.Query(table).Exec(); err != nil {
			return fmt.Errorf("failed to create table: %w", err)
		}
	}

	db.logger.Info("Cassandra scheme initilized successfully")

	return nil
}

const createTransactionMetricsTable = `
    CREATE TABLE IF NOT EXISTS analytics.transaction_metrics (
        transaction_id text,
        processed_at timestamp,
        amount decimal,
        category text,
        merchant text,
        is_fraud boolean,
        risk_score decimal,
        location text,
        PRIMARY KEY (transaction_id, processed_at)
    ) WITH CLUSTERING ORDER BY (processed_at DESC)
`

const createUserBehaviorTable = `
    CREATE TABLE IF NOT EXISTS analytics.user_behavior (
        user_id text,
        date date,
        total_transactions counter,
        total_amount counter,
        fraud_count counter,
        PRIMARY KEY (user_id, date)
    )
`

const createTimeSeriesMetricsTable = `
    CREATE TABLE IF NOT EXISTS analytics.time_series_metrics (
        metric_name text,
        time_bucket text,
        timestamp timestamp,
        value decimal,
        metadata map<text, text>,
        PRIMARY KEY ((metric_name, time_bucket), timestamp)
    ) WITH CLUSTERING ORDER BY (timestamp DESC)
`
