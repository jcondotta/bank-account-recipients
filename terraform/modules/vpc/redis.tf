#resource "aws_elasticache_subnet_group" "recipients_redis_subnet_group" {
#  name       = "recipients-redis-serverless-subnet-group"
#  subnet_ids = [aws_subnet.private_subnet_a.id, aws_subnet.private_subnet_b.id] # Replace with your private subnet IDs
#
#  tags = {
#    Name = "Redis Serverless Subnet Group"
#  }
#}
#
#resource "aws_elasticache_replication_group" "redis_serverless" {
#  replication_group_id          = "my-redis-serverless"
#  engine                        = "redis"
#  engine_version                = "7.x" # Check AWS documentation for the latest supported serverless version
#
#  automatic_failover_enabled     = true
#  security_group_ids             = [aws_security_group.recipients_redis_security_group.id]
#  subnet_group_name              = aws_elasticache_subnet_group.recipients_redis_subnet_group.name
#  transit_encryption_enabled     = true
#  at_rest_encryption_enabled     = true
#
#  tags = {
#    Name = "My Redis Serverless Cache"
#  }
#}