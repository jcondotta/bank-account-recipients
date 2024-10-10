# Define the IAM Role for the Lambda function
resource "aws_iam_role" "recipients_lambda_role_exec" {
  name = "${var.lambda_function_name}-exec-role"
  assume_role_policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [{
        "Action" : "sts:AssumeRole",
        "Principal" : {
          "Service" : "lambda.amazonaws.com"
        },
        "Effect" : "Allow"
      }]
    }
  )
  tags = var.tags
}

# IAM Role Policy to allow Lambda to interact with DynamoDB and CloudWatch Logs
resource "aws_iam_role_policy" "lambda_policy" {
  name = "${var.lambda_function_name}-policy"
  role = aws_iam_role.recipients_lambda_role_exec.id
  policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Action" : "logs:CreateLogGroup",
          "Effect" : "Allow",
          "Resource" : "arn:aws:logs:${var.aws_region}:${var.current_aws_account_id}:log-group:/aws/lambda/*"
        },
        {
          "Action" : [
            "logs:CreateLogStream",
            "logs:PutLogEvents"
          ],
          "Effect" : "Allow",
          "Resource" : "arn:aws:logs:${var.aws_region}:${var.current_aws_account_id}:log-group:/aws/lambda/${var.lambda_function_name}:*"
        },
        {
          "Action" : [
            "dynamodb:PutItem",
            "dynamodb:DeleteItem",
            "dynamodb:GetItem",
            "dynamodb:Query"
          ],
          "Effect" : "Allow",
          "Resource" : var.dynamodb_table_arn
        },
        {
          "Effect": "Allow",
          "Action": "ssm:GetParameter",
          "Resource": var.jwt_signature_secret_arn
        }
      ]
    }
  )
}
