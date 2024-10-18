## This repository is under heavy development

# Cardano Foundation Explorer Aggregator
This repository implements an aggregator application for the Cardano Foundation Explorer.
We will use [yaci-store](https://github.com/bloxbean/yaci-store) as a light weight and modular indexer.

## API
The Aggregator provides an API to retrieve the aggregated data. The following endpoints are available:
* `GET /addresstxcount` - Returns the number of transactions for all address - Pagination is possible
* `GET /addresstxcount/{address}` - Returns the number of transactions for a specific address
* `GET /poolstatus/{poolId}` - Returns the status of a specific pool
* `GET /poolstatus` - Returns the status of all pools - Pagination is possible
* `GET /poolstatus/latest` - Returns the latest status of all pools

The Swagger API can be found at `/swagger-ui/index.html`

### Configuration
All configurations are in `.env` file. The `cf-explorer-aggregator` allows to configure which aggregator to enable. 
The following Indexers are currently implemented and are enabled by default:
- `AddressTxCount` - Indexes the number of transactions for an address 
- `PoolStatus` - Aggregates the number of active pools per epoch

<details>
  <summary><i>All environment variables</i></summary>

| Name                                  | Description                                | Default Value                            |
|---------------------------------------|--------------------------------------------|------------------------------------------|
| `NETWORK`                             | Which network to use                       | `mainnet`                                |
| `PROTOCOL_MAGIC`                      | Cardano protocol magic                     | `764824073`                              |
| `CARDANO_NODE_HOST`                   | Cardano node host                          | `backbone.mainnet.cardanofoundation.org` |
| `CARDANO_NODE_PORT`                   | Cardano node port                          | `3001`                                   |
| `DB_DRIVER`                           | Database driver                            | `postgresql`                             |
| `DB_HOST`                             | Database host                              | `db`                                     |
| `DB_PORT`                             | Database port                              | `5432`                                   |
| `DB_SCHEMA`                           | Database schema                            | `exploreragg`                            |
| `DB_USER`                             | Database user                              | `exploreragg`                            |
| `DB_PASSWORD`                         | Database password                          | `exploreragg`                            |
| `DB_PATH`                             | Database path                              | `./explorer-aggregator-data`             |
| `DOCKER_IMAGE_TAG`                    | Docker Image tag of cf-explorer-aggregator | `latest`                                 |
| `AGGREGATOR_ADDRESS_TX_COUNT_ENABLED` | Enable AddressTxCount indexer              | `true`                                   |
| `POOL_STATUS_ENABLED`                 | Enable PoolStatus indexer                  | `false`                                  |
</details>

### How to run
1. Clone the repository
2. Change `.env` file with your configurations
3. Run `docker-compose --env-file .env up -d --build`