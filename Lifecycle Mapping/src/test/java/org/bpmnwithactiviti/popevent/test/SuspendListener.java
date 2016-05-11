/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bpmnwithactiviti.popevent.test;

import com.espertech.esper.client.EPServiceProviderManager;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.bpmnwithactiviti.mainevent.bam.event.tasklistenerlogic;
import org.activiti.engine.RuntimeService;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.test.ActivitiRule;
/**
 *
 * @author Marwa
 */
public class SuspendListener implements TaskListener{
    
    @Override
    public void notify(DelegateTask delegateTask){
    // Custom logic goes here
        tasklistenerlogic event = new tasklistenerlogic(delegateTask.getId(), 
                                new Date().getTime() ,
				(String) delegateTask.getEventName(), (String) delegateTask.getId() );
        //delegateTask.getVariable("resource")
       // delegateTask.addCandidateUser("John");
      //  delegateTask.addCandidateUser("Atef");
    ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem-fullhistory.xml");
    String PID = delegateTask.getProcessInstanceId();      
    RuntimeService runtimeService = activitiRule.getRuntimeService();
    runtimeService.suspendProcessInstanceById(PID);
    
    
    
    
    
                 System.out.println("\n");
		System.out.println(">>> Throwing suspended event: "+event);
		EPServiceProviderManager.getDefaultProvider().getEPRuntime()
				.getEventSender("tasklistenerlogic")
				.sendEvent(event);
                
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SuspendListener.class.getName()).log(Level.SEVERE, null, ex);
        }
                runtimeService.activateProcessInstanceById(PID);
                System.out.println("\n");
		System.out.println("process resumed again");
  }
}
