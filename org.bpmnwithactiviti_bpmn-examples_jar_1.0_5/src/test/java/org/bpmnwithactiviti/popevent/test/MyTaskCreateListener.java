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
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.test.ActivitiRule;
import org.bpmnwithactiviti.mainevent.bam.event.tasklistenerlogic;

/**
 *
 * @author Marwa
 */
public class MyTaskCreateListener implements TaskListener{
    public void notify(DelegateTask delegateTask) {
    // Custom logic goes here
        tasklistenerlogic event = new tasklistenerlogic(delegateTask.getId(), 
                                 delegateTask.getCreateTime(), 
				(String) delegateTask.getEventName(), (String) delegateTask.getAssignee() );
        //delegateTask.getVariable("resource")
       // delegateTask.addCandidateUser("John");
      //  delegateTask.addCandidateUser("Atef");
                 System.out.println("\n");
		System.out.println(">>> Throwing event: "+event);
		EPServiceProviderManager.getDefaultProvider().getEPRuntime()
				.getEventSender("tasklistenerlogic")
				.sendEvent(event);
                
            
  }
    
    
}
