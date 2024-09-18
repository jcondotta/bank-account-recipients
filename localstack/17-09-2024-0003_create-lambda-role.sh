#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/lambda-bank-account-recipients-role-settings.sh
. /etc/localstack/init/ready.d/environment-variables/dynamodb-recipients-table-settings.sh

RECIPIENTS_TABLE_ARN=$(awslocal dynamodb describe-table \
                --table-name "${RECIPIENTS_TABLE_NAME}" \
                --query 'Table.TableArn' \
                --output text)

awslocal iam create-role \
    --role-name "${BANK_ACCOUNT_RECIPIENTS_LAMBDA_ROLE_NAME}" \
    --assume-role-policy-document \
    '{
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "DynamoDbPermissions",
                "Effect": "Allow",
                "Action": [
                    "dynamodb:GetItem",
                    "dynamodb:PutItem",
                    "dynamodb:DeleteItem",
                    "dynamodb:Query"
                ],
                "Resource": [
                    "'${RECIPIENTS_TABLE_ARN}'"
                ]
            }
        ]
    }'