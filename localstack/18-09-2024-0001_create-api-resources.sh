#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/api-gateway-bank-account-recipients-settings.sh
. /etc/localstack/init/ready.d/environment-variables/lambda-bank-account-recipients-function-settings.sh

API_GATEWAY_ID=$(awslocal apigateway get-rest-apis \
                --query "items[?name=='${API_GATEWAY_NAME}'].[id]" \
                --output text)

API_GATEWAY_ROOT_RESOURCE_ID=$(awslocal apigateway get-resources \
                              --rest-api-id "${API_GATEWAY_ID}" \
                              --query "items[?path=='/'].[id]" \
                              --output text)

API_RESOURCE_ID=$(awslocal apigateway create-resource \
                  --rest-api-id "${API_GATEWAY_ID}" \
                  --parent-id "${API_GATEWAY_ROOT_RESOURCE_ID}" \
                  --path-part "${PART_PATH_API}" \
                  --query 'id' \
                  --output text)

V1_RESOURCE_ID=$(awslocal apigateway create-resource \
                --rest-api-id "${API_GATEWAY_ID}" \
                --parent-id "${API_RESOURCE_ID}" \
                --path-part "${PART_PATH_V1}" \
                --query 'id' \
                --output text)

RECIPIENTS_RESOURCE_ID=$(awslocal apigateway create-resource \
                    --rest-api-id "${API_GATEWAY_ID}" \
                    --parent-id "${V1_RESOURCE_ID}" \
                    --path-part "${PART_PATH_RECIPIENTS}" \
                    --query 'id' \
                    --output text)

BANK_ACCOUNT_ID_RESOURCE_ID=$(awslocal apigateway create-resource \
                    --rest-api-id "${API_GATEWAY_ID}" \
                    --parent-id "${RECIPIENTS_RESOURCE_ID}" \
                    --path-part "${PART_PATH_BANK_ACCOUNT_ID}" \
                    --query 'id' \
                    --output text)

BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID=$(awslocal apigateway create-resource \
                    --rest-api-id "${API_GATEWAY_ID}" \
                    --parent-id "${BANK_ACCOUNT_ID_RESOURCE_ID}" \
                    --path-part "${PART_PATH_BANK_ACCOUNT_ID_PLACE_HOLDER}" \
                    --query 'id' \
                    --output text)

RECIPIENT_NAME_RESOURCE_ID=$(awslocal apigateway create-resource \
                    --rest-api-id "${API_GATEWAY_ID}" \
                    --parent-id "${BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID}" \
                    --path-part "${PART_PATH_RECIPIENT_NAME}" \
                    --query 'id' \
                    --output text)

RECIPIENT_NAME_PLACE_HOLDER_RESOURCE_ID=$(awslocal apigateway create-resource \
                    --rest-api-id "${API_GATEWAY_ID}" \
                    --parent-id "${RECIPIENT_NAME_RESOURCE_ID}" \
                    --path-part "${PART_PATH_RECIPIENT_NAME_PLACE_HOLDER}" \
                    --query 'id' \
                    --output text)
