# Define the POST method for /login
resource "aws_api_gateway_method" "post_login" {
  rest_api_id   = aws_api_gateway_rest_api.recipients_api.id
  resource_id   = aws_api_gateway_resource.login.id
  http_method   = "POST"
  authorization = "NONE"  # No authentication required for login
}

resource "aws_api_gateway_integration" "post_login_integration" {
  rest_api_id             = aws_api_gateway_rest_api.recipients_api.id
  resource_id             = aws_api_gateway_resource.login.id
  http_method             = aws_api_gateway_method.post_login.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = var.lambda_invoke_uri
}

# Define the POST method for /api/v1/recipients
resource "aws_api_gateway_method" "post_recipients" {
  rest_api_id   = aws_api_gateway_rest_api.recipients_api.id
  resource_id   = aws_api_gateway_resource.recipients.id
  http_method   = "POST"
  authorization = "NONE"
}

# POST method integration with Lambda function
resource "aws_api_gateway_integration" "post_recipients_integration" {
  rest_api_id             = aws_api_gateway_rest_api.recipients_api.id
  resource_id             = aws_api_gateway_resource.recipients.id
  http_method             = aws_api_gateway_method.post_recipients.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = var.lambda_invoke_uri
}

# Define the GET method for /api/v1/recipients/bank-account-id/{bank-account-id}
resource "aws_api_gateway_method" "get_bank_account_recipients" {
  rest_api_id   = aws_api_gateway_rest_api.recipients_api.id
  resource_id   = aws_api_gateway_resource.bank_account_id_param.id
  http_method   = "GET"
  authorization = "NONE"

  # Specify that bank-account-id is a required path parameter
  request_parameters = {
    "method.request.path.bank-account-id" = true
  }
}

# GET method integration with Lambda function
resource "aws_api_gateway_integration" "get_bank_account_recipients_integration" {
  rest_api_id             = aws_api_gateway_rest_api.recipients_api.id
  resource_id             = aws_api_gateway_resource.bank_account_id_param.id
  http_method             = aws_api_gateway_method.get_bank_account_recipients.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = var.lambda_invoke_uri

  # Map the path parameters to the Lambda function
  request_parameters = {
    "integration.request.path.bank-account-id" = "method.request.path.bank-account-id"
  }
}

# Define the DELETE method for /api/v1/recipients/bank-account-id/{bank-account-id}/recipient-name/{recipient-name}
resource "aws_api_gateway_method" "delete_recipients" {
  rest_api_id   = aws_api_gateway_rest_api.recipients_api.id
  resource_id   = aws_api_gateway_resource.recipient_name_param.id
  http_method   = "DELETE"
  authorization = "NONE"

  # Specify required path parameters
  request_parameters = {
    "method.request.path.bank-account-id" = true
    "method.request.path.recipient-name"  = true
  }
}

# DELETE method integration with Lambda function
resource "aws_api_gateway_integration" "delete_recipients_integration" {
  rest_api_id             = aws_api_gateway_rest_api.recipients_api.id
  resource_id             = aws_api_gateway_resource.recipient_name_param.id
  http_method             = aws_api_gateway_method.delete_recipients.http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = var.lambda_invoke_uri

  # Map the path parameters to the Lambda function
  request_parameters = {
    "integration.request.path.bank-account-id" = "method.request.path.bank-account-id"
    "integration.request.path.recipient-name"  = "method.request.path.recipient-name"
  }
}
