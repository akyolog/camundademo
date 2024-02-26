package com.camunda.demo.pictureapp.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camunda.demo.pictureapp.ProcessConstants;
import com.camunda.demo.pictureapp.ProcessVariables;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;

@RestController
@RequestMapping("/")
public class PictureRestController {
    
private static final Logger LOG = LoggerFactory.getLogger(PictureRestController.class);

  @Autowired
  private ZeebeClient zeebe;

  @PostMapping("/startPictureProcess")
  public ProcessVariables startProcessInstance(@RequestBody Map<String, Object> variables) {

    LOG.info("Starting process `" + ProcessConstants.BPMN_PROCESS_ID + "` with variables: " + variables);

    final ProcessInstanceResult processInstanceResult = zeebe.newCreateInstanceCommand()
        .bpmnProcessId(ProcessConstants.BPMN_PROCESS_ID)
        .latestVersion()
        .variables(variables)
        .withResult()
        .send()
        .join();

    LOG.info("Process instance created with key: "
                + processInstanceResult.getProcessInstanceKey()
                + " and completed with results: "
                + processInstanceResult.getVariables());   
                
    ProcessVariables results = processInstanceResult.getVariablesAsType(ProcessVariables.class); 
    return results;
  }

}
