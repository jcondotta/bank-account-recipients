resource "aws_eip" "recipients_nat_elastic_ip_a" {
  domain = "vpc"

  tags = merge({ Name = "recipients-nat-elastic-ip-a"},
    var.tags
  )
}

resource "aws_eip" "recipients_nat_elastic_ip_b" {
  domain = "vpc"

  tags = merge({ Name = "recipients-nat-elastic-ip-b"},
    var.tags
  )
}