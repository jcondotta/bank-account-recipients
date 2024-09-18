#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/lambda-bank-account-recipients-role-settings.sh
. /etc/localstack/init/ready.d/environment-variables/lambda-bank-account-recipients-function-settings.sh

BANK_ACCOUNT_RECIPIENTS_LAMBDA_ROLE_ARN=$(awslocal iam get-role \
                           --role-name "${BANK_ACCOUNT_RECIPIENTS_LAMBDA_ROLE_NAME}" \
                           --query 'Role.Arn' \
                           --output text)

LOCALSTACK_URL='http://localstack:4566'

awslocal lambda create-function \
    --function-name "${BANK_ACCOUNT_RECIPIENTS_LAMBDA_FUNCTION_NAME}" \
    --runtime java17 \
    --zip-file fileb:///home/localstack/bank-account-recipients-0.1.jar \
    --handler io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction \
    --role "${BANK_ACCOUNT_RECIPIENTS_LAMBDA_ROLE_ARN}" \
    --architectures arm64 \
    --memory-size 1024 \
    --timeout 2700 \
    --environment Variables="{AWS_DYNAMODB_ENDPOINT='${LOCALSTACK_URL}'}"