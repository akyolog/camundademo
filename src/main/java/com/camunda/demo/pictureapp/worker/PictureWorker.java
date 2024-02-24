
package com.camunda.demo.pictureapp.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import com.camunda.demo.pictureapp.ProcessVariables;
import com.camunda.demo.pictureapp.service.PictureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PictureWorker {

  private static final Logger LOG = LoggerFactory.getLogger(PictureWorker.class);

  private final PictureService myService;

  public PictureWorker(PictureService myService) {
    this.myService = myService;
  }

  @JobWorker
  public ProcessVariables invokeMyService(@VariablesAsType ProcessVariables variables) {
    LOG.info("Invoking myService with variables: " + variables);

    boolean result = myService.myOperation(variables.getBusinessKey());

    return new ProcessVariables()
        .setResult(result); // new object to avoid sending unchanged variables
  }
}
