import aws_cdk as core
import aws_cdk.assertions as assertions

from audio_recognition_deployment.audio_recognition_deployment_stack import AudioRecognitionDeploymentStack

# example tests. To run these tests, uncomment this file along with the example
# resource in audio_recognition_deployment/audio_recognition_deployment_stack.py
def test_sqs_queue_created():
    app = core.App()
    stack = AudioRecognitionDeploymentStack(app, "audio-recognition-deployment")
    template = assertions.Template.from_stack(stack)

#     template.has_resource_properties("AWS::SQS::Queue", {
#         "VisibilityTimeout": 300
#     })
