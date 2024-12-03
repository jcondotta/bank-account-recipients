# Define the /login resource for retrieving access token
resource "aws_api_gateway_resource" "login" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_rest_api.this.root_resource_id
  path_part   = "login"
}

# Define the /api resource
resource "aws_api_gateway_resource" "api" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_rest_api.this.root_resource_id
  path_part   = "api"
}

# Define the /v1 resource under /api
resource "aws_api_gateway_resource" "v1" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.api.id
  path_part   = "v1"
}

# Define the /recipients resource under /v1
resource "aws_api_gateway_resource" "recipients" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.v1.id
  path_part   = "recipients"
}

# Define the /bank-account-id resource under /recipients
resource "aws_api_gateway_resource" "bank_account_id" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.recipients.id
  path_part   = "bank-account-id"
}

# Define the {bank-account-id} path parameter
resource "aws_api_gateway_resource" "bank_account_id_param" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.bank_account_id.id
  path_part   = "{bank-account-id}"
}

# Define the /recipient-name resource under /bank-account-id/{bank-account-id}
resource "aws_api_gateway_resource" "recipient_name" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.bank_account_id_param.id
  path_part   = "recipient-name"
}

# Define the {recipient-name} path parameter
resource "aws_api_gateway_resource" "recipient_name_param" {
  rest_api_id = aws_api_gateway_rest_api.this.id
  parent_id   = aws_api_gateway_resource.recipient_name.id
  path_part   = "{recipient-name}"
}
