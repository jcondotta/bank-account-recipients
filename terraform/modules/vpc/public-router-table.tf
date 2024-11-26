resource "aws_route_table" "public_router_table" {
  vpc_id = aws_vpc.recipients_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.recipients_internet_gateway.id
  }

  tags = merge({ Name = "recipients-public-router-table"},
    var.tags
  )
}

resource "aws_main_route_table_association" "set_public_router_table_as_main" {
  vpc_id         = aws_vpc.recipients_vpc.id
  route_table_id = aws_route_table.public_router_table.id
}

resource "aws_route_table_association" "public_subnet_a_to_public_router_table" {
  subnet_id      = aws_subnet.public_subnet_a.id
  route_table_id = aws_route_table.public_router_table.id
}

resource "aws_route_table_association" "public_subnet_b_to_public_router_table" {
  subnet_id      = aws_subnet.public_subnet_b.id
  route_table_id = aws_route_table.public_router_table.id
}
