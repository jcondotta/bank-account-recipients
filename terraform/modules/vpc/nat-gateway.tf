resource "aws_nat_gateway" "recipients_nat_gateway_a" {
  allocation_id = aws_eip.recipients_nat_elastic_ip_a.id
  subnet_id     = aws_subnet.public_subnet_a.id

  tags = merge({ Name = "recipients-nat-gateway-a"},
    var.tags
  )
}

resource "aws_nat_gateway" "recipients_nat_gateway_b" {
  allocation_id = aws_eip.recipients_nat_elastic_ip_b.id
  subnet_id     = aws_subnet.public_subnet_b.id

  tags = merge({ Name = "recipients-nat-gateway-b"},
    var.tags
  )
}