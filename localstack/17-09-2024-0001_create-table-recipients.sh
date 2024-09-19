#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/dynamodb-recipients-table-settings.sh

awslocal dynamodb create-table \
   --table-name "${RECIPIENTS_TABLE_NAME}" \
   --attribute-definitions \
          AttributeName=bankAccountId,AttributeType=S \
          AttributeName=name,AttributeType=S \
   --key-schema \
          AttributeName=bankAccountId,KeyType=HASH \
          AttributeName=name,KeyType=RANGE \
   --billing-mode=PROVISIONED \
   --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5