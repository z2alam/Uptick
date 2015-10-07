<%@ page language="java" contentType="text/html; 
         charset=US-ASCII" pageEncoding="US-ASCII"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="ece1779.cloudmsngr.servlets.MessageCache" %>
<%@ page import="ece1779.cloudmsngr.servlets.SessionCache" %>
<%@ page import="java.util.*, java.io.*" %>

<!DOCTYPE html>
<html>

<head>
<title>CloudMessenger</title>
<script>
    var $cont = $('#divMessageHistory');
    $cont[0].scrollTop = $cont[0].scrollHeight;
    
    function goToBottom(){
		$('#divContactList').scrollTop($('#divContactList').attr("scrollHeight"));
    }
    
    
    function selectContact(){
        document.forms["userForm"].submit();
    }
    
   
    function confirmDeleteContact() {
        var x;
        if (confirm("Are you sure you want to Delete the Contact?") == true) {
            x = "You pressed OK!";
            document.getElementById("confirmDeleteContact").innerHTML = x;
        }         
    }
    
    function confirmDeleteHistory() {
        var x;
        if (confirm("Are you sure you want to Delete the Contact's History?") == true) {
            x = "You pressed OK!";
            document.getElementById("confirmDeleteHistory").innerHTML = x;
        }         
    }
</script>
<style>
    #divHeader {
        background:#002c59;
        color:white;
        text-align:center;
        top: 0;	 
    	width: 100%;
    	height:70px;
    	position: absolute; 	
    }
    #tableHead
    {
    	width:100%;
    }
    .tdHeader
    {
    	width:33%;
    }
    #divLogo
    {
    	width:60px;
    	height:60px;
    	padding:0px 20px 0px 20px;
    }
    #divHeaderMsg
    {
    	text-align:center;
        margin:auto;
        width:600px;
    }
    #divLogoutBtn
    {
    	width:200px;
    	height:15px;
    	float:right;
    	padding:0px 0px 15px 0px;
    }
    footer {
        background:#002c59;
        color:white;
        clear:both;
        text-align:center;	
    	width: 100%;
    	position: absolute;
    	bottom:0; 
    }
    body 
    {
        background-color:#d9fedf;
        overflow-x:hidden;
    }
    .btnContact
    {
    	background: #FFEAF5;
    	border:none;
    	width:100%;
    	height:100%;
    	font-family: Georgia;
    	font-size:large;
    }
    .btnStandard 
    {
        background: #CC3300;
        -webkit-border-radius: 9;
        -moz-border-radius: 9;
        border-radius: 9px;
        text-shadow: 4px 1px 3px #666666;
        font-family: Georgia;
        color: #ffffff;
        font-size:small;
        padding: 3px 20px 3px 20px;
        text-decoration: none;
        margin:0 auto;
    }
    .btnStandard:hover {
        background: #FF9900;
        text-decoration: none;
    } 
    .sessionButton
    {
    	background: #CC3300;
        -webkit-border-radius: 9;
        -moz-border-radius: 9;
        border-radius: 9px;
        text-shadow: 4px 1px 3px #666666;
        font-family: Georgia;
        color: #ffffff;
        font-size:medium;
        padding: 3px 6px 3px 6px;
        text-decoration: none;
        margin:0 auto;
    }
    table {
        text-align:center;
        margin:auto;
    }
    span,p, form {
      font-family: Georgia;
      font-size:small;
      background-color:clear;
    }
    #divSpaceBottom{
        height:250px;
    }
    #divWrapper 
    {
        margin:auto;
        padding:65px 0px 60px 0px;
        text-align:center;   
    	width: 100%;
    	height:700px;
    }
    #divCurrent {
        width:670px;
        height:600px;  
        background: white;
        margin:auto;
        border-radius:.7em;
        float:right;
        
    }
    #ctrlHead
    {
    	margin: 5px;
    }
    #button {
        margin:20px;   
    } 
    #divInner
    {
    	width:970px;
        height:auto; 
        margin:auto;
    }
    #divContacts
    {
        float:left;
    	background: white;
        border-radius:.7em;
        height:100%;
    	width:235px;
    	-moz-box-shadow:    inset 0 0 10px #000000;
        -webkit-box-shadow: inset 0 0 10px #000000;
        box-shadow:         inset 0 0 10px #000000;
        margin:auto;
    }
    #divContactList
    {
        overflow-y: auto;
        overflow-x:hidden;
        height:260px;
    	background-color:#FFEAF5;
    	-moz-box-shadow:    inset 0 0 10px #000000;
        -webkit-box-shadow: inset 0 0 10px #000000;
        box-shadow:         inset 0 0 10px #000000;
        
    }
    .contactId
    {
        text-align:center;
        border-bottom:solid 1px grey;
    }
    .selectedContactId  
    {
        text-align:center;
        color:White;
        padding:7px 0px 7px 0px;
        background: #19538c;
    	-moz-box-shadow:    inset 0 0 10px #009499;
        -webkit-box-shadow: inset 0 0 10px #009499;
        box-shadow:         inset 0 0 10px #009499;
    }
    .delContact
    {
    	display:none;
    	padding:0px 15px 0px 0px;
    	width:30px;
    	float:left;
    }
    .contactId:hover .delContact
    {
    	display:block;
    }
    .selectedContactId:hover .delContact
    {
    	display:block;
    }
    #newContact
    {
    	padding: 10px 0px 20px 0px;
    	border-top:solid 2px grey;
    }
    a.deleteCurrentUserConvo {outline:none; }
    a.deleteCurrentUserConvo:hover {text-decoration:none;}  
    a.deleteCurrentUserConvo span {
        z-index:10;display:none; padding:20px;
        line-height:16px;
    }
    a.deleteCurrentUserConvo:hover span{
        display:inline; position:absolute; color:#111;
        border:1px solid #DCA; background:#fffAF0;}
       
    } 
    a.deleteCurrentUserConvo span
    {
        border-radius:4px;
        box-shadow: 5px 5px 8px #CCC;
    }

    
    #divMessageHistory
    {
        height:450px; 
        margin:0px 5px 0px 5px;
        overflow-y: auto;
        overflow-x:hidden;
        border-top:solid 1px grey;
    }
    .divSingleMessageSent
    {
    	padding:5px;
    }
    .divSingleMessageRec
    {
    	padding:5px;
    }
    #divWriteMessage
    { 
    	width:100%;
    	background-color:#19538c;
        border-radius:.7em;
    	-moz-box-shadow:    inset 0 0 10px #000000;
        -webkit-box-shadow: inset 0 0 10px #000000;
        box-shadow:         inset 0 0 10px #000000;
    }
    #divMessage
    {
    	padding: 5px 0px 5px 0px;
    }
    #divSend
    {
    	padding: 0px 0px 5px 0px;
    }
    .divNameBox
    {
    	border: solid 1px black; 
    	border-radius:.9em; 
    	padding:5px; 
    	background-color:#4ca6a6; 
    	color:White;
    }
    .tdHistoryMessage
    { 
    	border-radius:.9em; 
    	padding:5px; 
    	margin:5px;
    	background-color:#DCEDFF;
    	-moz-box-shadow:    inset 0 0 8px #000000;
        -webkit-box-shadow: inset 0 0 8px #000000;
        box-shadow:         inset 0 0 8px #000000;
    }
    .fontHistoryMessageText
    {
    	margin:0px 5px 0px 5px;
    }
    	

</style>
</head>

<!-- ------------ BODY --------------------------------------------------------------------------------- -->

<body 
	background="../images/bg-wallpaper2-2.jpg"
	onload="goToBottom();"
	>
	    <div id="divHeader">
	        <table id="tableHead">
	            <tr>
	                <td class="tdHeader">
	                    <div id="divLogo">
	                        <img id="CMlogo" src="../images/CM2.png" style="height:60px; width:60px;">
	                    </div>
	                </td>
	                <td class="tdHeader">
	                    <div id="divHeaderMsg">
		                        <h2>Welcome to CloudMessenger 
		                        <% if (session.getAttribute("user_name") != null) {  %>
		                        	 <%= session.getAttribute("user_name") %>! 
								<% } %> 
								</h2>
	                    </div>
	                </td>
	                <td class="tdHeader">
	                    <div id="divLogoutBtn">                            
	                        <a href="<%= session.getAttribute("logout_url") %>" >
				                <button class="btnStandard" value="Logout" type="submit" name="submit" style="width:166px;"> Logout</button>
			                </a>
	                    </div>
	                </td>
	            </tr>
	        </table>
	    </div>

		<form action="/servlet/CloudMessenger" method="post">
        <div id="divWrapper">
        	<div id="divErrMsg" style="padding:5px">
                <font style="margin-left:10px" size="3" color="red">
	             <%
	             if (session.getAttribute("ERROR_MSG") != null) {
	             %>
	             	<%= session.getAttribute("ERROR_MSG") %>
	             <%
	             }
	             %>
                </font>
            </div>
            <div id="divInner">
                <!-- ------------------------------------------ CONTACTS ------------------------------------------------- -->
                <div id="divContacts" onload="goToBottom()">
                    <!-- Send to a number outside of contacts -->
                    <table>
                        <tr><td>
                            <div style="font-style: oblique; font-size: larger; padding:15px 0px 0px 0px;">
                                Send to Number:
                            </div>
                        
                        </td></tr>
                        <tr><td>
                            <div id="sendToNumber" style="padding:5px 0px 15px 0px; border-bottom:solid 2px grey;">
                                <input name="phoneNumber" placeholder="(e.g +14161231234)" style="width: 75%;">  
                                <button  class="btnStandard" value="SelectNum" type="submit" name="submit" style="width:166px;">Select Number</button> 
                            </div>
                        </td></tr>
                        <tr><td>
                            <div style="font-style: oblique; font-size: larger; padding:15px 0px 15px 0px;">
                                OR <br />Choose from Contacts:
                            </div>
                        </td></tr>
                        <tr>
                        </tr>
                    </table>
                    <!--------------- Send to a number from contact list --------------->
                    <div id="divContactList">
						<!-- Obtain contacts from datastore -->
						<%
							if (session.getAttribute("user_name") != null) {
    							DatastoreService ds1 = DatastoreServiceFactory.getDatastoreService();
    							String curr_user_email = session.getAttribute("user_name").toString();
    							
    							String curr_contact_name = "";
    							if (session.getAttribute("contact_name") != null)
    								curr_contact_name = session.getAttribute("contact_name").toString();
    							String saved_contact_name = "";
	    						
								@SuppressWarnings("deprecation")
								Query phone_num_query1 = new Query("UserContacts")
															.addFilter("user_email", Query.FilterOperator.EQUAL, curr_user_email)
															.addFilter("contact_name", Query.FilterOperator.NOT_EQUAL, "");
								PreparedQuery pq1 = ds1.prepare(phone_num_query1);
								
								Key key1 = null;
								for (Entity en:pq1.asIterable()) {
									key1 = en.getKey();
									saved_contact_name = en.getProperty("contact_name").toString();
									if (saved_contact_name.equals(curr_contact_name)){
									%>
										<div class="selectedContactId">
											<table>
												<tr>
													<td>
														<div class="delContact">
															
																<input value="deleteContact:<%= saved_contact_name %>" type="submit" name="submit" style="background-image:url(../images/delete_btn.jpg); width:20px; height:20px; background-size: 100% 100%; color:transparent"/>
															
														</div>
													</td>
													<td>
														<font style="color:Yellow; font-family: Georgia;font-size:large;">
															<%= saved_contact_name %>
														</font>
														
													</td>
												</tr>
											</table>     
										</div>
									<%
									}
									else {
									%>
										<div class="contactId">
											<table>
												<tr>
													<td>
														<div class="delContact">
															
																<input value="deleteContact:<%= saved_contact_name %>" type="submit" name="submit" style="background-image:url(../images/delete_btn.jpg); width:20px; height:20px; background-size: 100% 100%; color:transparent"/>
															
														</div>
													</td>
													<td style="padding:5px 0px 5px 0px">
														<a href="/" onclick="selectContact()" >
															<button class="btnContact" value="selectContact:<%= saved_contact_name %>" type="submit" name="submit" >
															<%= saved_contact_name %>
															</button>
														</a>
													</td>
												</tr>
											</table>     
										</div>
									<%	
									}
								}
							}
    						
						%>
                    </div> <!-- divContactList -->
                    <!-- add a new contact -->
                    <div id="newContact">
                        <table>
                            <tr><td>
                                <div style="font-style: oblique; font-size: larger; padding:5px 0px 0px 0px;">
                                    Add a new Contact:
                                </div>
                            </td></tr>
                            <tr><td>
                                <div style="font-style: oblique; font-size: larger; padding:5px 0px 0px 0px;">
                                    Name:
                                </div>
                            </td></tr>
                            <tr><td>                            
                                <input name="newContactName" placeholder="Max 20 characters" style="width: 150px; padding:0px 0px 5px 0px;">  
                            </td></tr>
                            <tr><td>
                                <div style="font-style: oblique; font-size: larger; padding:5px 0px 0px 0px;">
                                    Number:
                                </div>
                            </td></tr>
                            <tr><td>                            
                                <input name="newPhoneNumber" placeholder="(e.g +14161231234)" style="width: 150px; padding:0px 0px 5px 0px;">  
                            </td></tr>
                            <tr><td>
                            	<button  class="btnStandard" value="AddNumber" type="submit" name="submit" style="width:166px;">Add Number</button>
                            </td></tr>
                        </table>                
                    </div>
                </div> <!-- divContacts -->
                <!-- ------------------------------------------ MESSAGES ---------------------------------------------------- -->
                <div id="divCurrent">
                    <div id="ctrlHead" >
                    <%
                    	if (session.getAttribute("phone_number") != null && 
                    			session.getAttribute("user_name") != null) {
                    %>
                        <table style="width:100%; padding-left:10px; padding-right:10px">
                            <tr>
                                <td>
                                    <div id="divCurrentUserName" style="float:left">
                                        <span style="font-size: larger; font-style:normal">
											<%
												String contact_number = session.getAttribute("phone_number").toString();
												String user_email = session.getAttribute("user_name").toString();
												SessionCache contact_session = SessionCache.GetContactSessionFromCache(contact_number);
												
												if (contact_session == null || !contact_session.GetOwnerId().equals(user_email)) {
											%>
											<button class="sessionButton" value="StartSession" type="submit" name="submit" >Start Session</button> 
											<%
												} else {
											%>
											<button class="sessionButton" value="StopSession" type="submit" name="submit" >Stop Session</button> 
											<%
												}	
											%>
											with
                                        <span style="font-size: large; font-style: oblique; color:Olive"><%= session.getAttribute("contact_name") %></span> 
                                        </span>
                                    </div>
                                </td>
                                <td>
                                    <div id="divDeleteCurrentUserConvo" style="float:right; width:auto; height:auto;" >
                                        <a href="deleteContact.html" class="deleteCurrentUserConvo">
                                            <input value="deleteHistory:<%= session.getAttribute("contact_name") %>" type="submit" name="submit" style="background-image:url(../images/delete_btn.jpg); width:20px; height:20px; background-size: 100% 100%; color:transparent"/>
                                            <span>Delete current contact's message history</span>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </table>   
					<%
						} else {
					%>
							<span style="font-size:larger; font-style:normal; width:100%; padding-left:10px; padding-right:10px;">
								Please select a Phone number or Choose a contact from contact list :)
							</span>
					<%
						}
					%>             
                    </div>
                    <!-- ----------------------  Messages History ----------------------------- -->
                    <div id="divMessageHistory">
                    	<%
				    	if (session.getAttribute("UserContactKey") != null) { 
					    	Long keyId = (Long) session.getAttribute("UserContactKey");
					    	
					    	// First try to retrieve msg history from cache.
					    	MessageCache cache = MessageCache.GetMessageHistoryFromCache(keyId);
					    	if (cache != null) {
					    		int num_entries = cache.GetNumEntries();
					    		
					    		for (int i = 0; i < num_entries; ++i) {
					    			String message = cache.GetMessage(i);
									boolean fromSelf = cache.GetFromSelf(i);
									Date dateTime = cache.GetTimestamp(i);
									
			    					if (fromSelf) {	
						%>
						<div style="clear: both; float: right; display: block; position: relative;">
							<br/>
                            <table>
                                <tr>
                                  <td class="tdHistoryMessage" >
                                    <font class="fontHistoryMessageText">
                                        <span style="float:left; margin-left:10px"> <%= message %> </span> <br /> <span style="float:right; margin-right:10px; font-style:italic; font-size:75%;">- <%= dateTime %></span>
                                    </font>
                                  </td>
                                  <td >
                                    <div class="divNameBox"> You </div>
                                  </td>
                                </tr>
                            </table>
                        </div>
						
						<%
									}
									else {
						%>
                        <div style="clear: both; float: left; display: block; position: relative;">
                        	<br/>
                            <table>
                                <tr>
                                  <td >
                                    <div class="divNameBox"> <%= session.getAttribute("contact_name") %> </div>
                                  </td>
                                  <td class="tdHistoryMessage" >
                                    <font class="fontHistoryMessageText" >
                                        <span style="float:left; margin-left:10px"> <%= message %> </span> <br /> <span style="float:right; margin-right:10px; font-style:italic; font-size:75%;">- <%= dateTime %></span>
                                    </font>
                                  </td>
                                </tr>
                            </table>
                        </div>
						
						<%
									}
					    		}
					    	}
					    	else {
						    	// Get the message list from datastore.
	                    		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

								@SuppressWarnings("deprecation")
								Query query = new Query("MessageHistory")
															.addFilter("user_contact_id", Query.FilterOperator.EQUAL, keyId)
															.addSort("timestamp", Query.SortDirection.ASCENDING);
								PreparedQuery pq = ds.prepare(query);
								
								// Obtain key of the corresponding user-contact.. otherwise add a new one.
								Key key = null;
									
								for (Entity en:pq.asIterable()) {
									String message = en.getProperty("message").toString();
									boolean fromSelf = Boolean.parseBoolean(en.getProperty("from_self").toString());
									Date dateTime = (Date) en.getProperty("timestamp");
	
									if (fromSelf) {	
						%>
						<div style="clear: both; float: right; display: block; position: relative;">
							<br/>
                            <table>
                                <tr>
                                  <td class="tdHistoryMessage" >
                                    <font class="fontHistoryMessageText">
                                        <span style="float:left; margin-left:10px"> <%= message %> </span> <br /> <span style="float:right; margin-right:10px; font-style:italic; font-size:75%;">- <%= dateTime %></span>
                                    </font>
                                  </td>
                                  <td >
                                    <div class="divNameBox"> You </div>
                                  </td>
                                </tr>
                            </table>
                        </div>
						
						<%
									}
									else {
						%>
                        <div style="clear: both; float: left; display: block; position: relative;">
                        	<br/>
                            <table>
                                <tr>
                                  <td >
                                    <div class="divNameBox"> <%= session.getAttribute("contact_name") %> </div>
                                  </td>
                                  <td class="tdHistoryMessage" >
                                    <font class="fontHistoryMessageText" >
                                        <span style="float:left; margin-left:10px"> <%= message %> </span> <br /> <span style="float:right; margin-right:10px; font-style:italic; font-size:75%;">- <%= dateTime %></span>
                                    </font>
                                  </td>
                                </tr>
                            </table>
                        </div>
						
						<%
									}
								}
							}
						}
                    	%>
                    </div> <!-- divMessageHistory -->
                    <!-- -------------------------- Write a New Message -------------------------------------------- -->
                    <div id="divWriteMessage">
                        <table>
                            <tr>
                                <td>
                                    <div id="divMessage">
                                      <textarea cols="50" rows="4" id="messageBox" name="messageToSend" placeholder="Type your message here"></textarea>                
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <div id="divSend" >
                                    	<button  class="btnStandard" value="SendMessage" type="submit" name="submit" style="width:166px;">Send Message</button>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>    
                </div> <!-- divCurrent -->
            </div> <!-- <div id="divInner"> -->
            <div id="divSpaceBottom"></div>
        </div> <!-- divWrapper -->
    </form>

<!--
    <footer>
    <p> We hope you are enjoying your experience here | This website is made by <em>Hatif Sattar</em> and <em>Zohaib Alam</em> as part of Assignment 2 for the course <em>ECE1779 - Intro to Cloud Computing</em> | This website is recommended to be viewed in Google Chrome</p>
    </footer>
-->

</body>
</html>
