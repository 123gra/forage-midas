package kafka

import "time"

// analytics transaction represents the transaction data recieved from kafka

type AnalyticsTransaction struct {
    TransDateTransTime string  `json:"transDateTransTime"`
    UnixTime          int64   `json:"unixTime"`
    CcNum             string  `json:"ccNum"`
    Amt               float64 `json:"amt"`
    Category          string  `json:"category"`
    Merchant          string  `json:"merchant"`
    IsFraud           bool    `json:"isFraud"`
    City              string  `json:"city"`
    State             string  `json:"state"`
    Zip               string  `json:"zip"`
    Lat               float64 `json:"lat"`
    Lon               float64 `json:"lon"`
    MerchLat          float64 `json:"merchLat"`
    MerchLon          float64 `json:"merchLon"`
    MerchZipcode      string  `json:"merchZipcode"`
    TransNum          string  `json:"transNum"`
    Gender            string  `json:"gender"`
    Dob               string  `json:"dob"`
    Job               string  `json:"job"`
    CityPop           int     `json:"cityPop"`
}

// processed transaction complete with analytics
type ProcessedTransaction struct {
	TransactionID    string    `json:"transaction_id"`
    ProcessedAt      time.Time `json:"processed_at"`
    RiskScore        float64   `json:"risk_score"`
    Category         string    `json:"category"`
    Amount           float64   `json:"amount"`
    IsFraud          bool      `json:"is_fraud"`
    Location         string    `json:"location"`
    MerchantCategory string    `json:"merchant_category"`
}
