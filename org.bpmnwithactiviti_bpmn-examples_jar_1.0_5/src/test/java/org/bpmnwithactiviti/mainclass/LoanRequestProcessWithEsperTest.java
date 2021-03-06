package org.bpmnwithactiviti.mainclass;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPPreparedStatement;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.bpmnwithactiviti.mainevent.bam.event.tasklistenerlogic;
import org.bpmnwithactiviti.common.AbstractTest;
import static org.junit.Assert.assertTrue;
import org.bpmnwithactiviti.popevent.test.MyTaskCreateListener;
import static org.junit.Assert.assertNotNull;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoanRequestProcessWithEsperTest extends AbstractTest {	
	
	@Rule 
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem-fullhistory.xml");
	
	private EPAdministrator epAdmin;
	
	@Before
	public void startEsper() {
		Configuration configuration = new Configuration();
		configuration.addEventType("tasklistenerlogic", tasklistenerlogic.class.getName());
		EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(configuration);
		epAdmin = epService.getEPAdministrator();
		
                String item1 = "create";
                String item2 = "assignment";
                String item3 = "complete";
                String item4 = "startoncreate";
                
                // mapping queries part (direct mapping of create, assignment, complete) 
		 epAdmin.createEPL( 
				"select * from tasklistenerlogic", "processDuration"); 
                 epAdmin.createEPL("create window intermwindow.win:keepall() as select taskInstanceId,receiveTime,eventType,performer from tasklistenerlogic", "cepStatementmainquerywindow"); //create a new named window 
                 
                 epAdmin.createEPL("insert into intermwindow(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId, receiveTime,'created',performer from tasklistenerlogic where eventType = 'create' ", "cepStatementmainquerywindowii1");
                 epAdmin.createEPL("insert into intermwindow(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId,receiveTime,'allocated',performer from tasklistenerlogic where eventType = 'assignment' ", "cepStatementmainquerywindowii2");
                 epAdmin.createEPL("insert into intermwindow(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId,receiveTime,'completed',performer from tasklistenerlogic where eventType = 'complete' ", "cepStatementmainquerywindowii3");
                 epAdmin.createEPL("insert into intermwindow(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId,receiveTime,'started',performer from tasklistenerlogic where eventType = 'startoncreate' ","cepStatementmainquerywindowii4");
                 
                 epAdmin.createEPL("select * from intermwindow", "cepStatementmainquerywindowo");
                 
                 String stmt1 = "insert into tasklistenerlogic(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId, receiveTime,'created',performer from tasklistenerlogic where eventType = ? ";
                 EPPreparedStatement prepared1 = epService.getEPAdministrator().prepareEPL(stmt1);
                 prepared1.setObject(1, item1);
                 EPStatement statementpattern1 = epService.getEPAdministrator().create(prepared1);
                 
                 String stmt2 = "insert into tasklistenerlogic(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId,receiveTime,'allocated',performer from tasklistenerlogic where eventType = ? ";
                 EPPreparedStatement prepared2 = epService.getEPAdministrator().prepareEPL(stmt2);
                 prepared2.setObject(1, item2);
                 EPStatement statementpattern2 = epService.getEPAdministrator().create(prepared2);
                 
                 String stmt3 = "insert into tasklistenerlogic(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId,receiveTime,'completed',performer from tasklistenerlogic where eventType = ? ";
                 EPPreparedStatement prepared3 = epService.getEPAdministrator().prepareEPL(stmt3);
                 prepared3.setObject(1, item3);
                 EPStatement statementpattern3 = epService.getEPAdministrator().create(prepared3);
                 
                 String stmt4 = "insert into tasklistenerlogic(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId,receiveTime,'started',performer from tasklistenerlogic where eventType = ? ";
                 EPPreparedStatement prepared4 = epService.getEPAdministrator().prepareEPL(stmt4);
                 prepared4.setObject(1, item4);
                 EPStatement statementpattern4 = epService.getEPAdministrator().create(prepared4);
                 
                 
                 
                // epAdmin.createEPL("insert into tasklistenerlogic(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId, receiveTime,'created',performer from tasklistenerlogic where eventType = 'create' ", "insertinmains1");
               //  epAdmin.createEPL("insert into tasklistenerlogic(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId,receiveTime,'allocated',performer from tasklistenerlogic where eventType = 'assignment' ", "insertinmains2");
                // epAdmin.createEPL("insert into tasklistenerlogic(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId,receiveTime,'completed',performer from tasklistenerlogic where eventType = 'complete' ", "insertinmains3");
              //   EPStatement s3 = epAdmin.createEPL("create window int.win:keepall() as select taskInstanceId,receiveTime,requestedAmount,eventType,resource from tasklistenerlogic", "q1");
               //  EPStatement s3 = epAdmin.createEPL("insert into int(taskInstanceId, receiveTime, requestedAmount, eventType, resource ) select taskInstanceId,receiveTime,requestedAmount,eventType,resource from tasklistenerlogic","q2");
                // EPStatement s3 = epAdmin.createEPL("select * from int", "q3");
                 
                  

	}
	
	private Double avgProcessDuration = null;

	@Test
	@Deployment(resources={"chapter14/loanrequest_withespertest.bpmn20.xml"})
	public void testEsperActivitiSetup() throws Exception {
		RuntimeService runtimeService = activitiRule.getRuntimeService();
                RepositoryService repositoryService = activitiRule.getRepositoryService();
		TaskService taskService = activitiRule.getTaskService();
                
                
		String performer = "Galal";
                Configuration configuration = new Configuration();
                EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(configuration);
	//	epAdmin = epService.getEPAdministrator();
                
               // EPStatement s3 = epAdmin.getStatement("q3");
		EPStatement epStatement = epAdmin.getStatement("processDuration"); // select all events in main stream
                
                EPStatement epStatement1cr = epAdmin.getStatement("cepStatementmainquerywindow"); // create int window with the same style of main stream
                
                // direct mapping of create, assignment, complete 
                EPStatement epStatementi1 = epAdmin.getStatement("cepStatementmainquerywindowii1"); 
                EPStatement epStatementi2 = epAdmin.getStatement("cepStatementmainquerywindowii2");
                EPStatement epStatementi3 = epAdmin.getStatement("cepStatementmainquerywindowii3");
                 EPStatement epStatementi4 = epAdmin.getStatement("cepStatementmainquerywindowii4");
                
             //   EPStatement epstatmains1 = epAdmin.getStatement("insertinmains1");
            //    EPStatement epstatmains2 = epAdmin.getStatement("insertinmains2");
             //   EPStatement epstatmains3 = epAdmin.getStatement("insertinmains3");
                
          //      EPStatement s1 = epAdmin.getStatement("q1");
            //    EPStatement s2 = epAdmin.getStatement("q2");
              //  EPStatement s3 = epAdmin.getStatement("q3");
                
                    
                
                 
                
                EPStatement epStatemento1 = epAdmin.getStatement("cepStatementmainquerywindowo"); // select all events from int window stream
                
                //listener of the direct mapping part 
	  epStatemento1.addListener(new UpdateListener () {
	    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
	      //avgProcessDuration = newEvents[0].get("avgProcessDuration");
              System.out.println("event mapped successfully:  " + newEvents[0].getUnderlying() );
               // System.out.println("\n");

	    }
	  }); 
          
          // select all events in the tasklistenerlogic stream
          epStatement.addListener(new UpdateListener () {
	    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
	      //avgProcessDuration = newEvents[0].get("avgProcessDuration");
              System.out.println("ALL EVENTS IN STREAM:  " + newEvents[0].getUnderlying() );
               // System.out.println("\n");

	    }
	  });
          
        // EPStatement s3 =  epAdmin.createEPL("select * from tasklistenerlogic where performer = \"Sultan\" ");
        // EPStatement s3 =  epAdmin.createEPL("select * from pattern [ every tasklistenerlogic(performer = \"Sultan\") -> tasklistenerlogic(performer != \"Sultan\") ]");
        
        // reallocation part 
       // EPStatement s3 = epAdmin.createEPL("select * from pattern [ every tasklistenerlogic(eventType = \"create\") -> tasklistenerlogic(eventType = \"assignment\") ]"); 
       // trying to perform insert into 
    //   EPStatement s3 = epAdmin.createEPL("select * from pattern [ every tasklistenerlogic(eventType = \"created\", performer = \"Galal\") -> tasklistenerlogic(eventType = \"allocated\", performer != \"Galal\") ]");
      //  EPStatement s3 = epAdmin.createEPL("insert into intermwindow select * from pattern [ every tasklistenerlogic(eventType = \"created\", performer = \"Galal\") -> tasklistenerlogic(eventType = \"allocated\", performer != \"Galal\") ]");
        
        String stmtp = "insert into intermwindow select * from pattern [ every tasklistenerlogic(eventType = \"startoncreate\", performer = ?) -> tasklistenerlogic(eventType = \"allocated\", performer != ?) ]";
                 EPPreparedStatement preparedp = epService.getEPAdministrator().prepareEPL(stmtp);
                 preparedp.setObject(1, performer);
                 preparedp.setObject(2, performer);
                 EPStatement statementpattern = epService.getEPAdministrator().create(preparedp);
   //     EPStatement s4 = epAdmin.createEPL("insert into tasklistenerlogic(taskInstanceId, receiveTime, eventType, performer ) select taskInstanceId, receiveTime,'realoooooooocccaaattteeeddd',performer from tasklistenerlogic where eventType = 'reallocate' ");
     //   EPStatement s3 = epAdmin.createEPL("insert into intermwindow select taskInstanceId, receiveTime,'allocatedwithnostate',performer from pattern [ every tasklistenerlogic(eventType = \"create\", performer = \"Galal\") -> tasklistenerlogic(eventType = \"assignment\", performer != \"Galal\") ]");
        //   EPStatement s4 = epAdmin.createEPL("select * from  tasklistenerlogic ");
                 // EPStatement s3 = epAdmin.getStatement("q3");
          EPStatement s6 = epAdmin.createEPL("select * from tasklistenerlogic where eventType = 'Suspended' ");
          s6.addListener(new UpdateListener () {
	    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
	      //avgProcessDuration = newEvents[0].get("avgProcessDuration");
              System.out.println("EVENT SELECTED:  " + newEvents[0].getUnderlying() );
               // System.out.println("\n");

	    }
	  });
                
            /*     tasklistenerlogic event2 = new tasklistenerlogic(taskService.createTaskQuery().singleResult().getId(),"Escalate", "Ramadan");
                
                
		EPServiceProviderManager.getDefaultProvider().getEPRuntime()
				.getEventSender("tasklistenerlogic")
				.sendEvent(event2); */
                
                //********************************** end of escalation part ********************************
                
                 
         //listener of anything related to experiments
                 statementpattern.addListener(new UpdateListener () {
	    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
	      //avgProcessDuration = newEvents[0].get("avgProcessDuration");
              tasklistenerlogic event2 = new tasklistenerlogic(taskService.createTaskQuery().singleResult().getId(), taskService.createTaskQuery().singleResult().getCreateTime() , "reallocate", "nullllll");
              System.out.println("event reallocated with no state successfully:  " + newEvents[0].getUnderlying() );
              EPServiceProviderManager.getDefaultProvider().getEPRuntime()
				.getEventSender("tasklistenerlogic")
				.sendEvent(event2);

	    } 
	  });   
                 
          /* s4.addListener(new UpdateListener () {
	    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
	      //avgProcessDuration = newEvents[0].get("avgProcessDuration");
              System.out.println("ana hena aho ^_^ :  " + newEvents[0].getUnderlying() );

	    }
	  }); */
          
		
          
		// Start first loan request
	  Map<String, Object> processVariables = new HashMap<String, Object>();
		//processVariables.put("loanAmount", 10);
               // processVariables.put("resource", "John");
               
               
             
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("loanrequest_withespertest");//, processVariables); //start new process instance 
		// System.out.println("Process isntance id is "+pi.getId());
                 
                 //suspend part
                 Execution execution = runtimeService.createExecutionQuery()
                    .processInstanceId(pi.getId()).activityId("waitState").singleResult();
                // System.out.println("Helooooooo");
                    assertNotNull(execution);
                    
                    //throw suspended event
              //      String assignee = taskService.createTaskQuery().singleResult().getAssignee();
                tasklistenerlogic event1 = new tasklistenerlogic(runtimeService.createExecutionQuery().singleResult().getId(), null , "Suspended", "Galal");
                
                System.out.println(event1);
		EPServiceProviderManager.getDefaultProvider().getEPRuntime()
				.getEventSender("tasklistenerlogic")
				.sendEvent(event1);
                    
                    
                    //resume part
                    runtimeService.signal(execution.getId());   
               //  System.out.println("yaraaaaaaab "+ execution.getId());
                 
                 //trace task id and name
                 String task = taskService.createTaskQuery().singleResult().getTaskDefinitionKey();
                 String taskid = taskService.createTaskQuery().singleResult().getId();
                    System.out.println("taskName --> " + task + "   taskID --->  "+ taskid);
                // runtimeService.suspendProcessInstanceById(pi.getId());
               //  System.out.println(pi.isSuspended());
                 
                 
                 
		//Thread.sleep(1000);

		// Evaluate first loan request
		//processVariables = new HashMap<String, Object>();
		//processVariables.put("requestApproved", true);
                
                taskService.createTaskQuery().singleResult();//.taskId("preparenotificationletter");
                event1 = new tasklistenerlogic(taskService.createTaskQuery().singleResult().getId(), taskService.createTaskQuery().singleResult().getCreateTime(), "startoncreate", taskService.createTaskQuery().singleResult().getAssignee());
                
                System.out.println(event1);
		EPServiceProviderManager.getDefaultProvider().getEPRuntime()
				.getEventSender("tasklistenerlogic")
				.sendEvent(event1);
 //taskService.createTaskQuery().processInstanceId(pi.getId());//singleResult(); // create and assign command ( create a new task instance)
                
                //taskService.createTaskQuery().taskAssignee("John");//.singleResult(); // create and assign command
               // processVariables.put("resource", "Atef");
                //taskService.createTaskQuery().taskAssignee("Atef");//.singleResult();
                
                
                //************************ retrieval trials **************************************************
            /*    List<Task> tasks = taskService.createTaskQuery().taskAssignee("Sultan").list();
                for(int i=0; i<tasks.size(); i++){
                    
                } */

                
                
                //*****************************end of trials *****************************************
              
                //************************ set a new resource *************************************
              //  taskService.setAssignee(taskService.createTaskQuery().singleResult().getId(),"Atef");
              //  taskService.setAssignee(taskService.createTaskQuery().singleResult().getId(),"Ramadan");

               //**************** delegate task part **********************************************

                
              taskService.delegateTask(taskService.createTaskQuery().singleResult().getId(), "Marwa");
          
               event1 = new tasklistenerlogic(taskService.createTaskQuery().singleResult().getId(), taskService.createTaskQuery().singleResult().getCreateTime(), "delegate", taskService.createTaskQuery().singleResult().getAssignee());
                
                System.out.println(event1);
		EPServiceProviderManager.getDefaultProvider().getEPRuntime()
				.getEventSender("tasklistenerlogic")
				.sendEvent(event1);
                
                 
                
                // claim part 
                
                taskService.claim(taskService.createTaskQuery().singleResult().getId(), null);
                taskService.claim(taskService.createTaskQuery().singleResult().getId(), "Alaa");
                
             //   taskService.setVariable(taskService.createTaskQuery().singleResult().getId(), "Suspend", "suspend");
                
             //  System.out.println(taskService.getVariable(taskService.createTaskQuery().singleResult().getId(), "Suspend"));
               
                
              //  taskService.createTaskQuery().taskDefinitionKey("evaluateLoanRequest");
                //taskService.createTaskQuery().singleResult(); // create and assign command

               // taskService.claim(taskService.createTaskQuery().singleResult().getId(), "Atef");
               
              // String pID = pi.getProcessInstanceId();
              // System.out.println(pID);
               
               
               
              // runtimeService.activateProcessInstanceById(pID);
               
              String name = taskService.createTaskQuery().singleResult().getName();
                System.out.println("tttttttttttt" + name);
              
                // complete part
		taskService.complete(taskService.createTaskQuery().singleResult().getId(), processVariables);
                
                
                
                // create of next task
                task = taskService.createTaskQuery().singleResult().getTaskDefinitionKey();
                 taskid = taskService.createTaskQuery().singleResult().getId();
                 
                 taskService.complete(taskService.createTaskQuery().singleResult().getId(), processVariables);
                 System.out.println("taskName --> " + task + "  taskID --->  "+ taskid);
                  
                 //create another process instance
               //      pi = runtimeService.startProcessInstanceByKey("loanrequest_withespertest");//, processVariables); //start new process instance 
		// System.out.println("Process isntance id is "+pi.getId());
                 
                 
            //    ProcessInstance po = runtimeService.startProcessInstanceByKey("loanrequest_withespertest");
		//System.out.println("Process isntance id is "+po.getId());
                
                 
		//System.out.println("<<< avgProcessDuration = " + avgProcessDuration);
		//assertTrue(avgProcessDuration >= 1000);

		//**************** Start second loan request
		//processVariables = new HashMap<String, Object>();
		//processVariables.put("loanAmount", 20);
               //processVariables.put("resource", "Saeed");
               // processVariables.put("resource", "Atef");


		//runtimeService.startProcessInstanceByKey("loanrequest_withespertest");
//		
//		Thread.sleep(2000);
//
//		// Evaluate second loan request
		//processVariables = new HashMap<String, Object>();
//		processVariables.put("requestApproved", true);
//                
             //   taskService.createTaskQuery().taskDefinitionKey("acceptloan");  // create and assign command
                
              //  taskService.delegateTask(taskService.createTaskQuery().singleResult().getId(), "Alaa");
//                
//                
		//taskService.complete(taskService.createTaskQuery().singleResult().getId(), processVariables);
                
		
		//System.out.println("<<< avgProcessDuration = " + avgProcessDuration);
		//assertTrue(avgProcessDuration >= 1500);
	}
}