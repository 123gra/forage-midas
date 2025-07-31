import pandas as pd 
from kafka import KafkaProducer
import json
import time

csv_path = 'credit_card_transactions.csv'

producer = KafkaProducer(
    bootstrap_servers = 'localhost:9092',
    value_serializer = lambda v: json.dumps(v).encode('utf-8')
)

topic = 'transactions'

chunk_size = 100

for chunk in pd.read_csv(csv_path, chunksize=chunk_size):

    for _, row in chunk.iterrows():
        producer.send(topic, row.to_dict())

    producer.flush()

    print(f"Sent {chunk_size} transactions, sleep for 10 seconds...")

    time.sleep(10)

producer.close()

print("All transactions sent.")

