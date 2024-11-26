resource "aws_route_table" "private_router_table_a" {
  vpc_id = aws_vpc.recipients_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_nat_gateway.recipients_nat_gateway_a.id
  }

  tags = merge({ Name = "recipients-private-router-table-a"},
    var.tags
  )
}

resource "aws_route_table" "private_router_table_b" {
  vpc_id = aws_vpc.recipients_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_nat_gateway.recipients_nat_gateway_b.id
  }

  tags = merge({ Name = "recipients-private-router-table-b"},
    var.tags
  )
}

resource "aws_route_table_association" "private_subnet_a_to_private_router_table_a" {
  subnet_id      = aws_subnet.private_subnet_a.id
  route_table_id = aws_route_table.private_router_table_a.id
}

resource "aws_route_table_association" "private_subnet_b_to_private_router_table_b" {
  subnet_id      = aws_subnet.private_subnet_b.id
  route_table_id = aws_route_table.private_router_table_b.id
}
