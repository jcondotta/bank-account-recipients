#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/dynamodb-recipients-table-settings.sh

awslocal dynamodb batch-write-item \
    --request-items \
        '{"'${RECIPIENTS_TABLE_NAME}'":
          [
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920c11-fc94-758b-b1f4-377b6f05de4a"},
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
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920c11-fc94-758b-b1f4-377b6f05de4a"},
                  "name": {"S": "Jefferson Feitosa"},
                  "iban": {"S": "BR0648882682239868944168999S9"},
                  "createdAt": {"S": "2022-01-16T14:11:02.112445345"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920c11-fc94-758b-b1f4-377b6f05de4a"},
                  "name": {"S": "Patrizio Condotta"},
                  "iban": {"S": "ES9234080434000044543219"},
                  "createdAt": {"S": "2024-08-20T09:27:33.448631954"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01921a68-8d69-7fc1-952e-9beeac6b0c75"},
                  "name": {"S": "Virginio Condotta"},
                  "iban": {"S": "BR0648882682239868944168911S1"},
                  "createdAt": {"S": "2022-01-16T14:11:02.112445345"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01921a68-8d69-7fc1-952e-9beeac6b0c75"},
                  "name": {"S": "Indalecio Condotta"},
                  "iban": {"S": "BR0648882682239868944168911S1"},
                  "createdAt": {"S": "2022-01-16T14:11:02.112445345"}
                }
              }
            }
          ]
        }'