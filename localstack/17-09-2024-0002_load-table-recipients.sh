#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/dynamodb-recipients-table-settings.sh

awslocal dynamodb batch-write-item \
    --request-items \
        '{"'${RECIPIENTS_TABLE_NAME}'":
          [
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920c11-e1a9-7223-a79a-24ca44e60518"},
                  "name": {"S": "Jefferson Condotta"},
                  "iban": {"S": "ES9220804343856445468519"},
                  "createdAt": {"S": "2024-08-20T09:27:33.448631954"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920c11-fc94-758b-b1f4-377b6f05de4a"},
                  "name": {"S": "Jefferson William"},
                  "iban": {"S": "BR0648882682239868944168935S6"},
                  "createdAt": {"S": "2022-01-16T14:11:02.112445345"}
                }
              }
            }
          ]
        }'