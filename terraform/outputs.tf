output "api_gateway_invoke_url" {
  description = "The base URL for the recipients API Gateway"
  value       = module.apigateway.api_gateway_invoke_url
}