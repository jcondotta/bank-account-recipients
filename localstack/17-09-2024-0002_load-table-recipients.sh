#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/dynamodb-recipients-table-settings.sh

aws dynamodb batch-write-item \
    --profile jcondotta \
    --region us-east-1 \
    --request-items \
        '{"'recipients-prod'":
          [
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920bff-1338-7efd-ade6-e9128debe5d4"},
                  "recipientName": {"S": "Jefferson Condotta"},
                  "recipientIban": {"S": "ES3801283316232166447417"},
                  "createdAt": {"S": "2024-08-20T09:27:33.448631954"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920bff-1338-7efd-ade6-e9128debe5d4"},
                  "recipientName": {"S": "Indalecio Condotta"},
                  "recipientIban": {"S": "BR2346424773884897968151332C5"},
                  "createdAt": {"S": "2022-01-16T14:11:02.112445345"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920bff-1338-7efd-ade6-e9128debe5d4"},
                  "recipientName": {"S": "Virginio Condotta"},
                  "recipientIban": {"S": "IT49W0300203280114524628857"},
                  "createdAt": {"S": "2022-01-16T14:11:02.112445345"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920bff-1338-7efd-ade6-e9128debe5d4"},
                  "recipientName": {"S": "Patrizio Condotta"},
                  "recipientIban": {"S": "IT93Q0300203280175171887193"},
                  "createdAt": {"S": "2024-08-20T09:27:33.448631954"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01920bff-1338-7efd-ade6-e9128debe5d4"},
                  "recipientName": {"S": "Jessica Condotta"},
                  "recipientIban": {"S": "BR4873995739459736698619729E5"},
                  "createdAt": {"S": "2024-08-20T09:27:33.448631954"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "bankAccountId": {"S": "01921f7f-5672-70ac-8c7e-6d7a941706cb"},
                  "recipientName": {"S": "Jefferson Condotta"},
                  "recipientIban": {"S": "ES3801283316232166447417"},
                  "createdAt": {"S": "2022-01-16T14:11:02.112445345"}
                }
              }
            }
          ]
        }'