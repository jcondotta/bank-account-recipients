# SSM Parameter Store VPC Endpoint
resource "aws_vpc_endpoint" "ssm_endpoint" {
  vpc_id              = aws_vpc.this.id
  service_name        = "com.amazonaws.${var.aws_region}.ssm"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true

  subnet_ids = [
    aws_subnet.private_subnet_a.id,
    aws_subnet.private_subnet_b.id
  ]

  security_group_ids = [
    aws_security_group.ssm_vpce_security_group.id
  ]

  tags = merge({ Name = "recipients-ssm-vpc-endpoint" }, var.tags)
}