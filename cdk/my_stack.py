import os
from aws_cdk import (
    Stack,
    aws_s3 as s3,
    aws_lambda as _lambda,
    aws_apigateway as apigw,
    aws_s3_deployment as s3deploy,
    aws_ec2 as ec2,
    Duration,
    CfnOutput,
)
from constructs import Construct
from dotenv import load_dotenv

load_dotenv() 

class MyStack(Stack):
    def __init__(self, scope: Construct, id: str, **kwargs):
        super().__init__(scope, id, **kwargs)

        bucket = s3.Bucket(self, "FrontendBucket",
                           website_index_document="index.html",
                           bucket_name="frontend-static-site", 
                           public_read_access=True,
                           block_public_access=s3.BlockPublicAccess(
                            block_public_acls=False,
                            block_public_policy=False,
                            ignore_public_acls=False,
                            restrict_public_buckets=False
                        )
                    )
        
        s3deploy.BucketDeployment(self, "DeployWebsite",
            sources=[s3deploy.Source.asset("./assets/dist")],
            destination_bucket=bucket,
            retain_on_delete=False,  
        )
        
        vpc = ec2.Vpc(self, "AppVPC",
                      max_azs=2,  
                      nat_gateways=1, 
                      subnet_configuration=[
                          ec2.SubnetConfiguration(
                              name="Public",
                              subnet_type=ec2.SubnetType.PUBLIC,
                              cidr_mask=24
                          ),
                          ec2.SubnetConfiguration(
                              name="Private",
                              subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS, 
                              cidr_mask=24
                          )
                      ]
                     )
        
        lambda_security_group = ec2.SecurityGroup(
            self, "LambdaSecurityGroup",
            vpc=vpc,
            description="Allow Lambda to access RDS",
            allow_all_outbound=True 
        )

        lambda_function = _lambda.Function(
            self, "SpringBootLambda",
            runtime=_lambda.Runtime.JAVA_11,
            handler="org.sadp.audiorecognition.lambda.LambdaHandler::handleRequest",
            code=_lambda.Code.from_asset("assets/jar"),  
            memory_size=1024,
            timeout=Duration.seconds(30),
            vpc=vpc,
            vpc_subnets=ec2.SubnetSelection(subnet_type=ec2.SubnetType.PRIVATE_WITH_EGRESS), 
            security_groups=[lambda_security_group],
            environment={
                "SPRING_PROFILES_ACTIVE": os.getenv("SPRING_PROFILES_ACTIVE"),
                "DB_USERNAME": os.getenv("DB_USERNAME"),
                "DB_PASSWORD": os.getenv("DB_PASSWORD"),
                "DB_HOST": os.getenv("DB_HOST"),
                "DB_PORT": os.getenv("DB_PORT"),
                "DB_NAME": os.getenv("DB_NAME"),
            }
        )

        api = apigw.LambdaRestApi(self, "ApiGateway", handler=lambda_function, proxy=True)

        CfnOutput(self, "ApiUrl", value=api.url)
        CfnOutput(self, "FrontendBucketUrl", value=bucket.bucket_website_url)
        CfnOutput(self, "VPCId", value=vpc.vpc_id)
