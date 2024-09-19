#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/api-gateway-bank-account-recipients-settings.sh

API_GATEWAY_ID=$(awslocal apigateway get-rest-apis \
                --query "items[?name=='bank-account-recipients-java-17-api'].[id]" \
                --output text)

BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID=$(awslocal apigateway get-resources \
                                          --rest-api-id "${API_GATEWAY_ID}" \
                                          --query "items[?pathPart=='{bank-account-id}'].[id]" \
                                          --output text)

BANK_ACCOUNT_ID='01920c12-797d-7ad5-8ba8-c6075ebd77a2'
RECIPIENT_NAME="recipient-name-test"

awslocal apigateway test-invoke-method \
  --rest-api-id "${API_GATEWAY_ID}" \
  --resource-id "${BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID}" \
  --http-method POST \
  --body '{"name": "'${RECIPIENT_NAME}'", "iban": "BR83 7736 6662 1110 9934"}' \
  --path-with-query-string "/api/v1/recipients/bank-account-id/${BANK_ACCOUNT_ID}"

awslocal apigateway test-invoke-method \
  --rest-api-id "${API_GATEWAY_ID}" \
  --resource-id "${BANK_ACCOUNT_ID_PLACE_HOLDER_RESOURCE_ID}" \
  --http-method GET \
  --path-with-query-string "/api/v1/recipients/bank-account-id/${BANK_ACCOUNT_ID}"

RECIPIENT_NAME_PLACE_HOLDER_RESOURCE_ID=$(awslocal apigateway get-resources \
                                          --rest-api-id "${API_GATEWAY_ID}" \
                                          --query "items[?pathPart=='{recipient-name}'].[id]" \
                                          --output text)

awslocal apigateway test-invoke-method \
  --rest-api-id "${API_GATEWAY_ID}" \
  --resource-id "${RECIPIENT_NAME_PLACE_HOLDER_RESOURCE_ID}" \
  --http-method DELETE \
  --path-with-query-string "/api/v1/recipients/bank-account-id/${BANK_ACCOUNT_ID}/recipient-name/${RECIPIENT_NAME}"