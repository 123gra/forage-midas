package database

// cassandra connection.

import (
	"fmt"
	"time"

	"go-analytics/config"

	"github.com/apache/cassandra-gocql-driver/v2"
	"github.com/sirupsen/logrus"
)

type CassandraDB struct {
	cluster *gocql.ClusterConfig 
	session *gocql.Session
	logger *logrus.Logger
}




