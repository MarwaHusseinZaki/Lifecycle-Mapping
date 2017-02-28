package com.sample;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    public static final void main(String[] args) {
        try {
        	
        	///*********************** reading from XML **********************
        	String filetype,fileprocessinstance,fileprocessmodel,filetask,filetimestamp,filedata,fileresource;
            
            //File fXmlFile = new File("D:/mywork/zcurrenttask30aug/loginstance1.xml");
      		File fXmlFile = new File("D:/FCI/Masters/NEW/work/jBPM/jbpm-installer/workspace/ex1/loginstance1.xml");
    			 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    				Document doc = dBuilder.parse(fXmlFile);
    				
    				doc.getDocumentElement().normalize();
    				
    				//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
    				
    				NodeList nList = doc.getElementsByTagName("event");
    				
    				//System.out.println("----------------------------");
    				
    				for (int temp = 0; temp < nList.getLength(); temp++){
    					
    					Node nNode = nList.item(temp);
    					//System.out.println("\nCurrent Element :" + nNode.getNodeName());
    					
    					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
    						 
    						Element eElement = (Element) nNode;
    						
    						filetype = eElement.getElementsByTagName("type").item(0).getTextContent();
    						fileprocessinstance = eElement.getElementsByTagName("process-instance").item(0).getTextContent();
    						fileprocessmodel = eElement.getElementsByTagName("process-model").item(0).getTextContent();
    						filetask = eElement.getElementsByTagName("task").item(0).getTextContent();
    						filetimestamp = eElement.getElementsByTagName("time-stamp").item(0).getTextContent().replace("\n","");
    						filedata = eElement.getElementsByTagName("data").item(0).getTextContent();
    						fileresource = eElement.getElementsByTagName("resource").item(0).getTextContent();
    						
    						System.out.println("Sending Event:" + filetype + " *** " + fileprocessinstance+ " *** " + fileprocessmodel + " *** " + filetask + " *** " + filetimestamp+ " *** " + filedata + " *** " + fileresource);
    					}
    				}
    				///*********************** end of reading from XML **********************
        	
            // load up the knowledge base
	        /*KieServices ks = KieServices.Factory.get();
    	    KieContainer kContainer = ks.getKieClasspathContainer();
        	KieSession kSession = kContainer.newKieSession("ksession-rules");*/
        	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        	
        	URL url = DroolsTest.class.getClassLoader().getResource("existpattern.drl");
        	kbuilder.add(ResourceFactory.newFileResource(url.getPath()), ResourceType.DRL);
        	if(kbuilder.hasErrors()){
        		System.err.println(kbuilder.getErrors().toString()+ "ya5taaaaay");
        	}
        	
        	// define stream --> config
        	KieBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        	config.setOption(EventProcessingOption.STREAM);
        	
        	//define pseudo clock --> conf
        	KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        	conf.setOption(org.kie.api.runtime.conf.ClockTypeOption.get("pseudo"));
        	
        	
        	// define kbase object
        	org.kie.internal.KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
        	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        	
        	// define ksession object for insertion in working memory
        	StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        	
        	// define the conf object of clock
        	ksession = kbase.newStatefulKnowledgeSession(conf, null);
        	
        	// define pseudo clock object
        	SessionPseudoClock clock = ksession.getSessionClock();
        	
        	
        	//*********************** define entry points ************************
        	EntryPoint entryPointStoreOne = ksession.getEntryPoint("StoreOne");
        	//EntryPoint entryPointStoreTwo = ksession.getEntryPoint("StoreTwo");
        	
        	//**************************insert events into entrypoint (stream) *******************
        	/*insertEvent(entryPointStoreOne, new Event(), "A",1);
        	insertEvent(entryPointStoreOne, new Event(), "B",2);
        	insertEvent(entryPointStoreOne, new Event(), "X",3);
        //	insertEvent(entryPointStoreOne, new Event(), "A",1);
        	insertEvent(entryPointStoreOne, new Event(), "E",4);
        	insertEvent(entryPointStoreOne, new Event(), "A",5);
        	insertEvent(entryPointStoreOne, new Event(), "C",6);
        	insertEvent(entryPointStoreOne, new Event(), "Z",7);
        	insertEvent(entryPointStoreOne, new Event(), "A",8);
        	insertEvent(entryPointStoreOne, new Event(), "Y",9);
        	insertEvent(entryPointStoreOne, new Event(), "M",10);
        	insertEvent(entryPointStoreOne, new Event(), "A",11);
        //	insertEvent(entryPointStoreOne, new Event(), "L",11);
        	insertEvent(entryPointStoreOne, new Event(), "F",12);
        	insertEvent(entryPointStoreOne, new Event(), "H",13);
        //	insertEvent(entryPointStoreOne, new Event(), "I",14);
        //	insertEvent(entryPointStoreOne, new Event(), "G",12);
*/        	
        	Event e1 = new Event();
        	e1.settask("A");
        	e1.settistamp(1);
        	e1.setresource("Atef");
        	e1.setprocessinstance("1");
        	entryPointStoreOne.insert(e1);
        	
        	Event e2 = new Event();
        	e2.settask("B");
        	e2.settistamp(2);
        	e2.setresource("Alaa");
        	e2.setprocessinstance("1");
        	entryPointStoreOne.insert(e2);
        	
        	Event e3 = new Event();
        	e3.settask("X");
        	e3.settistamp(3);
        	e3.setprocessinstance("1");
        	entryPointStoreOne.insert(e3);
        	
        	/*Event e4 = new Event();
        	e4.settask("A");
        	e4.settistamp(4);
        	entryPointStoreOne.insert(e4);*/
        	
        	Event e5 = new Event();
        	e5.settask("E");
        	e5.settistamp(5);
        	e5.setprocessinstance("1");
        	entryPointStoreOne.insert(e5);
        	
        	Event e6 = new Event();   ////////////////////AAAAAAAAAAAAAAAAAAAAAA
        	e6.settask("A");
        	e6.settistamp(6);
        	e6.setprocessinstance("1");
        	entryPointStoreOne.insert(e6);
        	
        	Event e7 = new Event();
        	e7.settask("C");
        	e7.settistamp(7);
        	e7.setprocessinstance("1");
        	entryPointStoreOne.insert(e7);
        	
        	Event e8 = new Event();
        	e8.settask("Z");
        	e8.settistamp(8);
        	e8.setprocessinstance("1");
        	entryPointStoreOne.insert(e8);
        	
        	Event e9 = new Event();       //////AAAAAAAAAAAAAAAAA
        	e9.settask("A");
        	e9.settistamp(9);
        	e9.setprocessinstance("1");
        	entryPointStoreOne.insert(e9);
        	
        	Event e10 = new Event();
        	e10.settask("Y");
        	e10.settistamp(10);
        	e10.setprocessinstance("1");
        	entryPointStoreOne.insert(e10);
        	
        	Event e11 = new Event();
        	e11.settask("M");
        	e11.settistamp(11);
        	e11.setresource("Marwa");
        	e11.setprocessinstance("1");
        	entryPointStoreOne.insert(e11);
        	
        	Event e12 = new Event();     // AAAAA
        	e12.settask("A");
        	e12.settistamp(12);
        	e12.setprocessinstance("1");
        	entryPointStoreOne.insert(e12);
        	
        	/*Event e13 = new Event();
        	e13.settask("L");
        	e13.settistamp(13);
        	entryPointStoreOne.insert(e13);*/
        	
        	Event e14 = new Event();
        	e14.settask("F");
        	e14.settistamp(14);
        	e14.setprocessinstance("1");
        	entryPointStoreOne.insert(e14);
        	
        	Event e166 = new Event();
        	e166.settask("I");
        	e166.settistamp(14);
        	e166.setprocessinstance("1");
        	entryPointStoreOne.insert(e166);
        	
        	Event e15 = new Event();
        	e15.settask("H");
        	e15.settistamp(15);
        	e15.setprocessinstance("1");
        	entryPointStoreOne.insert(e15);
        	
        	/*Event e16 = new Event();
        	e16.settask("I");
        	e16.settistamp(16);
        	entryPointStoreOne.insert(e16);*/
        	
        	Event e17 = new Event();
        	e17.settask("G");
        	e17.settistamp(17);
        	e17.setresource("Galal");
        	e17.setprocessinstance("1");
        	entryPointStoreOne.insert(e17);
        	
        	Event e18 = new Event();
        	e18.settask("N");
        	e18.settistamp(18);
        	e18.setresource("Marwa");
        	e18.setprocessinstance("1");
        	entryPointStoreOne.insert(e18);
        	clock.advanceTime(7, TimeUnit.MINUTES);
        	
        	Event e19 = new Event();
        	e19.settask("O");
        	e19.settistamp(19);
        	e19.setprocessinstance("1");
        	entryPointStoreOne.insert(e19);
        	
        	Event e20 = new Event();
        	e20.settask("J");
        	e20.settistamp(20);
        	e20.setprocessinstance("1");
        	entryPointStoreOne.insert(e20);
        	
        	Event e21 = new Event();
        	e21.settask("K");
        	e21.settistamp(21);
        	e21.setprocessinstance("1");
        	entryPointStoreOne.insert(e21);
        	clock.advanceTime(1, TimeUnit.MINUTES);
        	
        	Event e22 = new Event();
        	e22.settask("P");
        	e22.settistamp(19);
        	e22.setprocessinstance("1");
        	entryPointStoreOne.insert(e22);
        	
        	
        	Event e23 = new Event();
        	e23.settask("Q");
        	e23.settistamp(22);
        	e23.setprocessinstance("1");
        	entryPointStoreOne.insert(e23);
        	clock.advanceTime(1, TimeUnit.MINUTES);
        	
        	/*Event e24 = new Event();
        	e24.settask("R");
        	e24.settistamp(23);
        	entryPointStoreOne.insert(e24);
        	clock.advanceTime(1, TimeUnit.MINUTES);*/
        	
        	Event e25 = new Event();
        	e25.settask("Q");
        	e25.settistamp(24);
        	e25.setprocessinstance("1");
        	entryPointStoreOne.insert(e25);
        //	clock.advanceTime(1, TimeUnit.MINUTES);
        	
        	Event e24 = new Event();
        	e24.settask("R");
        	e24.settistamp(26);
        	e24.setprocessinstance("1");
        	entryPointStoreOne.insert(e24);
      //  	clock.advanceTime(1, TimeUnit.MINUTES);
        	
        	Event e27 = new Event();
        	e27.settask("evaluateloanrequest");
        	e27.settistamp(27);
        	e27.settype("created");
        	e27.setprocessinstance("1");
        	entryPointStoreOne.insert(e27);
        	
        	Event e28 = new Event();
        	e28.settask("evaluateloanrequest");
        	e28.settistamp(28);
        	e28.settype("allocated");
        	e28.setresource("Galal");
        	e28.setprocessinstance("1");
        	entryPointStoreOne.insert(e28);
        	
        	Event e29 = new Event();
        	e29.settask("evaluateloanrequest");
        	e29.settistamp(29);
        	e29.settype("allocated");
        	e29.setresource("Marwa");
        	e29.setprocessinstance("1");
        	entryPointStoreOne.insert(e29);
        	
        	Event e30 = new Event();
        	e30.settask("preparenotificationletter");
        	e30.settistamp(29);
        	e30.settype("completed");
        	e30.setresource("Marwa");
        	e30.setprocessinstance("1");
        	entryPointStoreOne.insert(e30);
        	
        	
        	//******************************** insert compliance rules into working memory ************
        	Comprules c1 = new Comprules();
        	c1.setpattern("exist");
        	c1.setantecedent("A");
        	c1.setscopestart("E");
        	c1.setmultiplicity(2);
        	ksession.insert(c1);
        	
        	Comprules c8 = new Comprules();
        	c8.setpattern("exist");
        	c8.setantecedent("evaluateloanrequest");
        	c8.setscopestart("created");
        	c8.setscopeend("allocated");
        	c8.setmultiplicity(1);
        	ksession.insert(c8);
        	
        	Comprules c9 = new Comprules();
        	c9.setpattern("separationofduty");
        	c9.setplana("evaluateloanrequest");
        	c9.setplanb("preparenotificationletter");
        	ksession.insert(c9);
        	
        	Comprules c2 = new Comprules();
        	c2.setpattern("absence");
        	c2.setantecedent("G");
        	//c1.setscopestart("E");
        	//c1.setmultiplicity(2);
        	ksession.insert(c2);
        	
        	Comprules c3 = new Comprules();
        	c3.setpattern("response");
        //	c3.setantecedent("H");
        //	c3.setconsequent("I");
        //	c3.setscopestart("H");
        //	c3.setscopeend("I");
        	c3.setplana("H");
        	c3.setplanb("I");
        	c3.setisBefore(false);
        	//c1.setscopestart("E");
        	//c1.setmultiplicity(2);
        	ksession.insert(c3);
        	
        	Comprules c4 = new Comprules();
        	c4.setpattern("responsesec");
        	c4.setplana("N");
        	c4.setplanb("O");
        	c4.setisBefore(false);
        	c4.settimespan(10-1);
        	//c1.setscopestart("E");
        	//c1.setmultiplicity(2);
        	ksession.insert(c4);
        	
        	Comprules c5 = new Comprules();
        	c5.setpattern("responsethi");
        	c5.setplana("J");
        	c5.setwa("K");
        	c5.setplanb("P");
        	//c1.setscopestart("E");
        	//c1.setmultiplicity(2);
        	ksession.insert(c5);
        	
        	Comprules c6 = new Comprules();
        	c6.setpattern("onetooneresponse");
        	c6.setplana("Q");
        	c6.setplanb("R");
        	ksession.insert(c6);
        	
        	Comprules c7 = new Comprules();
        	c7.setpattern("separationofduty");
        	c7.setplana("M");
        	c7.setplanb("N");
        	ksession.insert(c7);
        	
        	ksession.fireAllRules();
        	ksession.dispose();
        	
        	

            
    } catch(Exception e){
		e.printStackTrace();
    }
    	
    }
    
    /*private static void insertEvent(EntryPoint entryPoint, Event event, String task, long tistamp){
    	event.settask(task);
    	event.settistamp(tistamp);
    	entryPoint.insert(event);
    	
    }*/
    
    }

    


