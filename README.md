# moniepointexam
Spring Boot REST API for merchant transaction analytics — built with PostgreSQL, JPA, and JDBC batch loading.

## Requirements
- Java 21
- PostgreSQL
- Maven

  
## Setup

1. Clone the repository
2. Create a PostgreSQL database called "moniepoint"
3. Update "src/main/resources/application.properties" with your DB credentials
4. Add CSV files to "data/sample_data/" folder so as to load the csv files into the database
5. Run the application

## Running
```bash
mvn spring-boot:run
```

## Endpoints given for the project are :

| Method | Endpoint | Description |

##
 1. GET | /analytics/top-merchant | Top merchant by transaction volume |
 2. GET | /analytics/monthly-active-merchants | Unique active merchants per month |
 3. GET | /analytics/product-adoption | Merchant count per product |
 4. GET | /analytics/kyc-funnel | KYC funnel channel stage counts |
 5. GET | /analytics/failure-rates | Failure rate per product |
