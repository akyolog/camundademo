
package com.camunda.demo.pictureapp;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;

import java.time.Duration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.DeployResourceCommandStep1;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.testcontainer.ZeebeProcessTest;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import io.zeebe.containers.ZeebeContainer;

@Testcontainers
@ZeebeProcessTest
public class PictureappContainerTest {

    @Container
    private final ZeebeContainer zeebeContainer = new ZeebeContainer();

    private static final Logger LOG = LoggerFactory.getLogger(PictureappContainerTest.class);

    @Test
    public void testGettingCatPicture() {

        final BpmnModelInstance process = Bpmn.createExecutableProcess(ProcessConstants.BPMN_PROCESS_ID)
                .startEvent()
                .endEvent()
                .done();

        final ZeebeClient zeebeClient = ZeebeClient.newClientBuilder()
                .gatewayAddress(zeebeContainer.getExternalGatewayAddress())
                .usePlaintext()
                .build();

        final DeploymentEvent deploymentEvent = zeebeClient.newDeployResourceCommand()
                .addProcessModel(process, "AnimalPictureApp.bpmn")
                .send()
                .join();

        BpmnAssert.assertThat(deploymentEvent)
                .containsProcessesByBpmnProcessId(ProcessConstants.BPMN_PROCESS_ID);

        ProcessVariables variables = new ProcessVariables();
        variables.setAnimalType("cat");

        final ProcessInstanceResult processInstance = zeebeClient
                .newCreateInstanceCommand()
                .bpmnProcessId(ProcessConstants.BPMN_PROCESS_ID)
                .latestVersion()
                .variables(variables)
                .withResult()
                .requestTimeout(Duration.ofSeconds(60))
                .send()
                .join();

        Assertions.assertThat(processInstance.getProcessDefinitionKey())
                .isEqualTo(deploymentEvent.getProcesses().get(0).getProcessDefinitionKey());

        LOG.info("Process instance created with key: "
                + processInstance.getProcessInstanceKey()
                + " and completed with results: "
                + processInstance.getVariables());

    }
}