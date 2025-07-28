package database

// cassandra connection. 

import (
	"fmt"
	"time"

	"github.com/gocql/gocql"
    "github.com/sirupsen/logrus"
    "go-analytics/config"
)

type CassandraDB struct {
	session *gocql.Session
	logger *logrus.Logger
}



