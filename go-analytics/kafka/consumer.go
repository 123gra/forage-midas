package kafka

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/IBM/sarama"
	"github.com/jpmc/forge/go-analytics/config"
	"github.com/jpmc/forge/go-analytics/database"
	"github.com/sirupsen/logrus"
)

type Consumer struct {
	consumer sarama.ConsumerGroup
	topic    string
	db       *database.CassandraDB
	logger   *logrus.Logger
	// analytics *analytics.Processor
}

func NewConsumer(kafkaConfig config.KafkaConfig, db *database.CassandraDB, logger *logrus.Logger) (*Consumer, error) {

	saramaConfig := sarama.NewConfig()
	saramaConfig.Consumer.Group.Rebalance.Strategy = sarama.NewBalanceStrategyRoundRobin()
	saramaConfig.Consumer.Offsets.Initial = sarama.OffsetOldest
	saramaConfig.Consumer.Return.Errors = true

	consumer, err := sarama.NewConsumerGroup(kafkaConfig.Brokers, kafkaConfig.GroupID, saramaConfig)

	if err != nil {
		return nil, fmt.Errorf("failed to create consumer group: %w", err)
	}

	// will be implemented
	//analyticsProcessor := analytics.NewProcessor(db, logger)

	return &Consumer{
		consumer: consumer,
		topic:    kafkaConfig.Topic,
		db:       db,
		logger:   logger,
		//analytics: analyticsProcessor,
	}, nil
}

func (c *Consumer) Start(ctx context.Context) error {

	for {

		err := c.consumer.Consume(ctx, []string{c.topic}, c)

		if err != nil {
			c.logger.Errorf("Error from the consumer: %v", err)
			time.Sleep(time.Second)
		}

		// constantly check if there is a problem with the context window and shut down gracefully
		if ctx.Err() != nil {
			return ctx.Err()
		}
	}
}

// ConsumeClaim is called by consumer.Consumer everytime it runs. 
func (c *Consumer) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {

	for {
		// lets you handle the first message that comes (read amessage or cxt cancellation)
		select {
		// <- is used to recieve a value from a channel, blocks until val available
		case message := <-claim.Messages():
			c.logger.Infof("Recieved message from partition %d at offset %d", message.Partition, message.Offset)

			if err := c.processMessage(message.Value); err != nil {
				c.logger.Errorf("Error processing the message: %v", err)
			}

			session.MarkMessage(message, "")

		case <-session.Context().Done():
			return nil
		}
	}
}

// methods required by the sarama interface, no resource allocation needed
func (c *Consumer) Setup(sarama.ConsumerGroupSession) error {
	return nil
}

func (c *Consumer) Cleanup(sarama.ConsumerGroupSession) error {
	return nil
}


func (c *Consumer) processMessage(data []byte) error {

	var transaction AnalyticsTransaction

	if err := json.Unmarshal(data, &transaction); err != nil {
		return fmt.Errorf("failed to unmarshal transaction: %w", err)
	}

	c.logger.Infof("Processing transaction: %s, Amount: %.2f, Category: %s",
		transaction.TransNum, transaction.Amt, transaction.Category)

	// TODO: Process analytics and store in Cassandra
	// if err := c.analytics.ProcessTransaction(&transaction); err != nil {
	//     return fmt.Errorf("failed to process analytics: %w", err)
	// }

	return nil
}
