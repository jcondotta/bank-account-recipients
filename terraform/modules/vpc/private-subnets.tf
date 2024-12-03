resource "aws_subnet" "private_subnet_a" {
  vpc_id            = aws_vpc.this.id
  cidr_block        = "10.0.0.0/26"
  availability_zone = "us-east-1a"
  map_public_ip_on_launch = false

  tags = merge(var.tags, { Name = "recipients-private-subnet-a" })
}

resource "aws_subnet" "private_subnet_b" {
  vpc_id            = aws_vpc.this.id
  cidr_block        = "10.0.0.64/26"
  availability_zone = "us-east-1b"
  map_public_ip_on_launch = false

  tags = merge(var.tags, { Name = "recipients-private-subnet-b" })
}