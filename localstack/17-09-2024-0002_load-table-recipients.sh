#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/dynamodb-recipients-table-settings.sh

awslocal dynamodb batch-write-item \
    --request-items \
        '{"'${RECIPIENTS_TABLE_NAME}'":
          [
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"N": "998372"},
                  "name": {"S": "Jefferson Condotta"},
                  "iban": {"S": "ES9220804343856445468519"},
                  "createdAt": {"S": "2024-08-20T09:27:33.448631954"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"N": "773641"},
                  "name": {"S": "Jefferson William"},
                  "iban": {"S": "BR0648882682239868944168935S6"},
                  "createdAt": {"S": "2022-01-16T14:11:02.112445345"}
                }
              }
            }
          ]
        }'