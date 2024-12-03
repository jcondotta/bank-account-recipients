# DynamoDB VPC Endpoint
resource "aws_vpc_endpoint" "dynamodb_endpoint" {
  vpc_id           = aws_vpc.this.id
  service_name = "com.amazonaws.${var.aws_region}.dynamodb"
  vpc_endpoint_type = "Gateway"

  route_table_ids  = [
    aws_route_table.private_router_table_a.id,
    aws_route_table.private_router_table_b.id
  ]

  tags = merge( { Name = "recipients-dynamodb-vpc-endpoint"}, var.tags)
}