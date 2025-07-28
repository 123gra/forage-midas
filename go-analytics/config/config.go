package config

type CassandraConfig struct {

	// the required info to connect to cassandra ex port username password etc.

	Hosts    []string
	Port     int
	Username string
	Password string
	Keyspace string
}

// NewCassandraConfig returns a default configuration for local Cassandra development
func NewCassandraConfig() *CassandraConfig {
	return &CassandraConfig{
		Hosts:    []string{"127.0.0.1"}, // From "Connected to Test Cluster at 127.0.0.1:9042"
		Port:     9042,                  // Default Cassandra CQL port
		Username: "",                    // No authentication for local development
		Password: "",                    // No authentication for local development
		Keyspace: "midas_analytics",
	}
}
