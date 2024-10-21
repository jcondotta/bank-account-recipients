#resource "aws_ecs_task_definition" "recipients_ecs_task_definition" {
#  family                   = "bank-account-recipients-task"
#  network_mode             = "awsvpc"
#  requires_compatibilities = ["FARGATE"]
#  cpu                      = "256"
#  memory                   = "512"
#  execution_role_arn       = aws_iam_role.recipients_ecs_task_execution_exec.arn
#  task_role_arn            = aws_iam_role.recipients_ecs_task_execution_exec.arn
#
#  container_definitions = jsonencode([
#    {
#      name  = "bank-account-recipients-container"
#      image = "jcondotta/bank-account-recipients:latest"
#      essential = true
#      portMappings = [
#        {
#          containerPort = 8080
#          hostPort      = 8080
#          protocol      = "tcp"
#        }
#      ]
#      logConfiguration = {
#        logDriver = "awslogs"
#        options = {
#          awslogs-group         = "/ecs/bank-account-recipients"
#          awslogs-region        = var.aws_region
#          awslogs-stream-prefix = "ecs"
#        }
#      }
#    }
#  ])
#
#  tags = var.tags
#}
