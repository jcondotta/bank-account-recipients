#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/api-gateway-bank-account-recipients-settings.sh

awslocal apigateway create-rest-api \
  --name "${API_GATEWAY_NAME}" \
  --endpoint-configuration types=REGIONAL \
  --description "Bank Account Recipients' API Gateway"