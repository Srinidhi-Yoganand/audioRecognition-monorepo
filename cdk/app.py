import aws_cdk as cdk
from my_stack import MyStack
from aws_cdk import Environment


app = cdk.App()
MyStack(app, "MyLocalStack",
        env=cdk.Environment(account="000000000000", region="us-east-1"))  

app.synth()
