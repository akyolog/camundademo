
package com.camunda.demo.pictureapp.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import com.camunda.demo.pictureapp.ProcessVariables;
import com.camunda.demo.pictureapp.service.PictureService;

import java.io.File;
import java.io.IOException;

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

  @JobWorker(type = "GetAnimalPicture", autoComplete = false)
  public ProcessVariables invokeMyService(final JobClient client, final ActivatedJob job, @VariablesAsType ProcessVariables variables) {

    LOG.info("Invoking AnimalPicture Process with variables: " + variables);

    ProcessVariables responVariables = new ProcessVariables();
    try {

      String result = myService.selectAnimal(variables.getAnimalType());
      responVariables.setImage(result);
      responVariables.setResult(true);
      // complete the job
      client.newCompleteCommand(job.getKey())
            .variables(responVariables)
            .send()
            .join();

    } catch (IOException e) {
      responVariables.setResult(false);
      responVariables.setError(e.getMessage());
      e.printStackTrace();
      // cancel the job
      client.newCompleteCommand(job.getKey())
            .variables(responVariables)
            .send()
            .exceptionally( throwable -> { throw new RuntimeException("Could not complete job, Error:" + e.getMessage(), throwable); });

    }

    return responVariables; // new object to avoid sending unchanged variables
  }


}
