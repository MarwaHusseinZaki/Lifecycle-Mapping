/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.sample;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.marshalling.impl.ProtobufMessages.KnowledgeBase;
import org.drools.core.time.SessionPseudoClock;
import org.drools.runtime.conf.ClockTypeOption;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * This is a sample test of the evaluation process.
 */
public class ProcessTest extends JbpmJUnitBaseTestCase {

	@Test
	public void testEvaluationProcess() {
		
		//********************************* defining the drools part ***************
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		
		URL url1 = ProcessTest.class.getClassLoader().getResource("mappingrules.drl");
		URL url2 = ProcessTest.class.getClassLoader().getResource("Evaluation.bpmn");
		
		kbuilder.add(ResourceFactory.newFileResource(url1.getPath()), ResourceType.DRL);
		kbuilder.add(ResourceFactory.newFileResource(url2.getPath()), ResourceType.BPMN2);
		
		if(kbuilder.hasErrors()){
    		System.err.println(kbuilder.getErrors().toString()+ "ya5taaaaay");
    	}
		
		// define stream --> config
    	KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
    	config.setOption(EventProcessingOption.STREAM);
    	
    	// define kbase object
    	org.kie.internal.KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
    	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    	
    	// define ksession object for insertion in working memory
    	StatefulKnowledgeSession ksessiond = kbase.newStatefulKnowledgeSession();
    	
    	EntryPoint entryPointStoreOne = ksessiond.getEntryPoint("StoreOne");
    	
    	// define mevents 
    	Mevents m1 = new Mevents();
    	Mevents m2 = new Mevents();
    	Mevents m3 = new Mevents();
		
		//****************************************the hu task part ***********************************
		RuntimeManager manager = createRuntimeManager("Evaluation.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		KieRuntimeLogger log = KieServices.Factory.get().getLoggers().newThreadedFileLogger(ksession, "test", 1000);
		TaskService taskService = engine.getTaskService();
		
		// start a new process instance
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "krisv");
		params.put("reason", "Yearly performance evaluation");
		ProcessInstance processInstance = 
			ksession.startProcess("com.sample.evaluation", params);
		System.out.println("Process started ...");
		
		// complete Self Evaluation
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		assertEquals(1, tasks.size());
		TaskSummary task = tasks.get(0);
		System.out.println("'krisv' completing task " + task.getName() + ": " + task.getDescription());
		taskService.start(task.getId(), "krisv");
		m1.settask("start");
		m1.setresource("krisv");
		//taskService.suspend(task.getId(), "krisv");
		System.out.println(task.getStatusId());
		//m1.settask(task.getStatusId()); // set task t start
		entryPointStoreOne.insert(m1); //insert this task in entr point
		taskService.delegate(task.getId(), "krisv", "Marwa");
		//m2.settask("");
	//	ksessiond.insert(m1); //insert this task in entr point
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("performance", "exceeding");
		taskService.complete(task.getId(), "krisv", results);
		//System.out.println(task.getStatusId());
		
		// john from HR
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		
		assertEquals(1, tasks.size());
		task = tasks.get(0);
		System.out.println("'john' completing task " + task.getName() + ": " + task.getDescription());
		taskService.claim(task.getId(), "john");
		System.out.println(task.getStatusId());
		m2.settask(task.getStatusId()); // set task t start
		entryPointStoreOne.insert(m2);
		taskService.start(task.getId(), "john");
		//taskService.suspend(task.getId(), "krisv");
		//System.out.println(task.getStatusId());
		results = new HashMap<String, Object>();
		results.put("performance", "acceptable");
		taskService.complete(task.getId(), "john", results);
		//System.out.println(task.getStatusId());
		
		// mary from PM
		tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		assertEquals(1, tasks.size());
		task = tasks.get(0);
		System.out.println("'mary' completing task " + task.getName() + ": " + task.getDescription());
		taskService.claim(task.getId(), "mary");
		System.out.println("Mary   " + task.getStatus());
		taskService.start(task.getId(), "mary");
		System.out.println("Mary   " + task.getStatusId());
		m3.settask(task.getStatusId()); // set task t start
		entryPointStoreOne.insert(m3);
		System.out.println(task.getStatus());
		results = new HashMap<String, Object>();
		results.put("performance", "outstanding");
		taskService.complete(task.getId(), "mary", results);
		
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		System.out.println("Process instance completed");
		log.close();
		
		ksessiond.fireAllRules();
		ksessiond.dispose();
		
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

	public ProcessTest() {
		super(true, true);
	}
	
}
