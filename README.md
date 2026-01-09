# Midas
Project repo for the JPMC Advanced Software Engineering Forage program
In the simulation I:

1-Integrated Kafka into a Spring Boot microservice to consume and deserialize high-volume transaction messages using a configurable topic and embedded Kafka test framework.

2-Implemented transaction validation and persistence logic with Spring Data JPA and an H2 SQL database, including entity modeling and balance updates across relational User records.

3-Connected the service to an external REST Incentive API using RestTemplate, processing incentive responses and incorporating them into transactional workflows.

4-Developed a REST endpoint for querying user balances, returning JSON responses through a Spring controller while maintaining clean architectural boundaries.

5-Verified system behavior using Maven test suites and debugger-driven inspection, ensuring reliability across message ingestion, database operations, and external API interactions.

Check out the simulation here: https://www.theforage.com/simulations/jpmorgan/advanced-software-engineering-r0fm