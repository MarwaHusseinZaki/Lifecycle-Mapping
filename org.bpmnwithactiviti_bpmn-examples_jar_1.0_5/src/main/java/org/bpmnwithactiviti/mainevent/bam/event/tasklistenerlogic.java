package org.bpmnwithactiviti.mainevent.bam.event;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tasklistenerlogic")
@XmlAccessorType(XmlAccessType.FIELD)
public class tasklistenerlogic{

	// For correlating the events.
	@XmlElement
	private final String taskInstanceId;
        @XmlElement
	public Date receiveTime;
        @XmlElement
	public String eventType;
        @XmlElement
	public String performer;
        
	
	@SuppressWarnings("unused")
	private tasklistenerlogic() {
		taskInstanceId = null;
                receiveTime = null;
                eventType = null;
                performer = null;
                
	}
	
	public tasklistenerlogic(String processInstanceId, Date receiveTime, String eventType, String performer ) {
		this.taskInstanceId = processInstanceId;
                this.receiveTime = receiveTime;
                this.eventType = eventType;
                this.performer = performer;
                
	}

	public String gettaskInstanceId() {
		return taskInstanceId;
	}
        
        public Date getReceiveTime() {
		return receiveTime;
	}
        
        public String getEventType() {
		return eventType;
	}
        
        
        public String getPerformer() {
		return performer;
	}
        
        

	@Override
	public String toString() {
		return "task  " + eventType +  "  successfully: LoanRequestReceivedEvent{ taskInstanceId= "+taskInstanceId+ " ,receiveTime= " + receiveTime + " ,eventtype= " + eventType + " ,performer= " + performer +  " }";
	}
}
