#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/api-gateway-bank-account-recipients-settings.sh
. /etc/localstack/init/ready.d/environment-variables/lambda-bank-account-recipients-function-settings.sh

API_GATEWAY_ID=$(awslocal apigateway get-rest-apis \
                --query "items[?name=='${API_GATEWAY_NAME}'].[id]" \
                --output text)

BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID=$(awslocal apigateway get-resources \
                                          --rest-api-id "${API_GATEWAY_ID}" \
                                          --query "items[?pathPart=='${PART_PATH_BANK_ACCOUNT_ID_PLACE_HOLDER}'].[id]" \
                                          --output text)

awslocal apigateway put-method \
    --rest-api-id "${API_GATEWAY_ID}" \
    --resource-id "${BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID}" \
    --http-method GET \
    --authorization-type NONE

awslocal apigateway put-method-response \
    --rest-api-id "${API_GATEWAY_ID}" \
    --resource-id "${BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID}" \
    --http-method GET \
    --status-code 200 \
    --response-models '{"application/json": "Empty"}'

BANK_ACCOUNT_RECIPIENTS_LAMBDA_FUNCTION_ARN=$(awslocal lambda get-function \
    --function-name "${BANK_ACCOUNT_RECIPIENTS_LAMBDA_FUNCTION_NAME}" \
    --query 'Configuration.FunctionArn' \
    --output text)

awslocal apigateway put-integration \
    --rest-api-id "${API_GATEWAY_ID}" \
    --resource-id "${BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID}" \
    --http-method GET \
    --integration-http-method POST \
    --type AWS_PROXY \
    --uri "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/$BANK_ACCOUNT_RECIPIENTS_LAMBDA_FUNCTION_ARN/invocations"

awslocal apigateway put-integration-response \
    --rest-api-id "${API_GATEWAY_ID}" \
    --resource-id "${BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID}" \
    --http-method GET \
    --status-code 200 --selection-pattern ''