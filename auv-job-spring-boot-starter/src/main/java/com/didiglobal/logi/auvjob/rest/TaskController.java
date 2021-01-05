package com.didiglobal.logi.auvjob.rest;

import com.didiglobal.logi.auvjob.common.dto.TaskDto;
import com.didiglobal.logi.auvjob.core.task.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * task controller.
 *
 * @author dengshan
 */
@RestController
@RequestMapping(Constants.V1 + "/auv-job/task")
public class TaskController {

  @Autowired
  private TaskManager taskManager;

  @PostMapping("/add")
  public Object add(TaskDto taskDto) {
    return taskManager.add(taskDto);
  }

  @DeleteMapping("/delete")
  public Object delete(@RequestParam String taskCode) {
    return taskManager.delete(taskCode);
  }

  @PutMapping("/update")
  public Object update(TaskDto taskDto) {
    return taskManager.update(taskDto);
  }

  @PostMapping("execute")
  public void execute(@RequestParam String taskCode,
                      @RequestParam(defaultValue = "false", required = false) Boolean executeSubs) {
    taskManager.execute(taskCode, executeSubs);
  }

  @PutMapping("/pause")
  public Object pause(String taskCode) {
    return taskManager.pause(taskCode);
  }

  @PutMapping("/pauseAll")
  public Object pauseAll() {
    return taskManager.pauseAll();
  }

  @PutMapping("/resume")
  public Object resume(String taskCode) {
    return taskManager.resume(taskCode);
  }

  @PutMapping("/resumeAll")
  public Object resumeAll() {
    return taskManager.resumeAll();
  }
}
