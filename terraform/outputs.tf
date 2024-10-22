output "api_gateway_invoke_url" {
  value = (var.environment == "localstack" ?
    "http://${module.apigateway.api_gateway_id}.execute-api.localhost.localstack.cloud:4566/${var.environment}" :
    module.apigateway.api_gateway_invoke_url)
  description = "API Gateway invoke URL based on the environment"
}