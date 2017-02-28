package com.sample;

import java.math.BigDecimal;

public class Mevents {
   /*public enum Antecedent {
      A, NA
   }
   public enum Consequent {
	      A, B, NA
	   }
   public enum Pattern {
	      Exists, Absence, sequence, precedes, response
	   }
   public enum WA {
	      A, F, X, NA
	   }
   
   public enum ScopeStart {
      E, F, S, X, M, NA
   }
   
   public enum ScopeEnd {
	     E, F, S, NA
	   }*/
   
   private String type;
   private String task;
   private String processinstance;
   private String processmodel;
   private long tistamp;
   private String data;
   private String resource;
   
   /*private BigDecimal sellPrice;
   private Type typeofItem;
   private BigDecimal localTax;*/
   
   public String gettype() {
	      return type;
	   }
	   
	   public void settype(String type) {
	      this.type = type;
	   }
	   
	   public String gettask() {
		      return task;
		   }
		   
		   public void settask(String task) {
		      this.task = task;
		   }
		   
		   public String getprocessinstance() {
			      return processinstance;
			   }
			   
			   public void setprocessinstance(String processinstance) {
			      this.processinstance = processinstance;
			   }
			   
			   public String getprocessmodel() {
				      return processmodel;
				   }
				   
				   public void setprocessmodel(String processmodel) {
				      this.processmodel = processmodel;
				   }
				   
				   public long gettistamp() {
					      return tistamp;
					   }
					   
					   public void settistamp(long tistamp) {
					      this.tistamp = tistamp;
					   }
					   
					   public String getdata() {
						      return data;
						   }
						   
						   public void setdata(String data) {
						      this.data = data;
						   }
						   
						   public String getresource() {
							      return resource;
							   }
							   
							   public void setresource(String resource) {
							      this.resource = resource;
							   }
   
   
		   
}