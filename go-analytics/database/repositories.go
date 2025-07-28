package database

// data access layer

import (
	
	gocql "github.com/apache/cassandra-gocql-driver/v2"
)


type Repository struct {
	session gocql.Session
}

func NewRepository(session gocql.Session) *Repository {

	return &Repository{session: session}
}


