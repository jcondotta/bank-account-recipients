resource "aws_api_gateway_authorizer" "jwt_authorizer" {
  rest_api_id    = aws_api_gateway_rest_api.recipients_api.id
  name           = "recipients_jwt_authorizer"
  type           = "TOKEN"
  authorizer_uri = var.lambda_invoke_uri
  identity_source = "method.request.header.Authorization"
}