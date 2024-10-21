#resource "aws_ecs_service" "recipients_ecs_service" {
#  name            = "bank-account-recipients-service"
#  cluster         = aws_ecs_cluster.recipients_ecs_cluster.id
#  task_definition = aws_ecs_task_definition.recipients_ecs_task_definition.arn
#  desired_count   = 1
#  launch_type     = "FARGATE"
#
#  network_configuration {
#    subnets         = data.aws_subnets.default.ids
#    security_groups = [aws_security_group.recipients_security_group.id]
#    assign_public_ip = true
#  }
#}
