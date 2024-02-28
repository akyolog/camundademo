package com.camunda.demo.pictureapp;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;

import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.camunda.demo.pictureapp.service.PictureService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@SpringBootTest
@ZeebeSpringTest
public class TestAnimalApplication {

    @Autowired private ZeebeClient zeebe;

    @MockBean private PictureService pictureService;

    @Test
    public void testGettingCatPicture() throws Exception {

        ProcessVariables variables = new ProcessVariables();
        variables.setAnimalType("cat");

        // start the process
        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
            .bpmnProcessId(ProcessConstants.BPMN_PROCESS_ID)
            .latestVersion()
            .variables(variables)
            .send()
            .join();

        waitForProcessInstanceCompleted(processInstance);

        assertThat(processInstance)
            .hasPassedElement("GetAnimalPicture")
            .isCompleted();

        Mockito.verify(pictureService).selectAnimal("cat");
        Mockito.verifyNoMoreInteractions(pictureService);

    }
}