package org.bpmnwithactiviti.popevent.test;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;

public class Task11 implements JavaDelegate,ExecutionListener {

	public static ProcessEngine processEngine;
	public static RepositoryService repositoryService;
	public static RuntimeService runtimeService;
	public static ProcessInstance processInstance;
	public void execute(DelegateExecution delegate) throws Exception {
		

		
		for(int i=0;i<10;i++){
			System.out.println("Task 1 ::::::::Welcome to Bosch");
			Thread.sleep(100);
		}
		
		
	}
	public void notify(DelegateExecution delegate) throws Exception {
		String id = delegate.getProcessInstanceId();
		System.out.println("ID is :::::::::::::"+id);
		
		runtimeService.suspendProcessInstanceById(id);
                System.out.println("process suspended");
		
	}

}
