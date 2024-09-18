#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/api-gateway-bank-account-recipients-settings.sh

API_GATEWAY_ID=$(awslocal apigateway get-rest-apis \
                --query "items[?name=='${API_GATEWAY_NAME}'].[id]" \
                --output text)

awslocal apigateway create-deployment \
    --rest-api-id "${API_GATEWAY_ID}" \
    --stage-name "${DEPLOYMENT_DEFAULT_STAGE_NAME}"


echo "Try the following URL for accessing the API Gateway"
echo "http://${API_GATEWAY_ID}.execute-api.localhost.localstack.cloud:4566/${DEPLOYMENT_DEFAULT_STAGE_NAME}"