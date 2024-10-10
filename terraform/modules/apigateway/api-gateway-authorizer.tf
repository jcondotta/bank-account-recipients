resource "aws_api_gateway_authorizer" "jwt_authorizer" {
  rest_api_id    = aws_api_gateway_rest_api.recipients_api.id
  name           = "recipients_jwt_authorizer"
  type           = "TOKEN"
  authorizer_uri = "arn:aws:apigateway:${var.aws_region}:lambda:path/2015-03-31/functions/${var.lambda_function_arn}/invocations"
  identity_source = "method.request.header.Authorization"
}