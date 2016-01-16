<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="com.welcome.uptick.UptickServlet" %>
<%@ page import="java.util.*, java.io.*" %>

<!DOCTYPE html>
<html>
  <link rel="stylesheet" type="text/css" href="../stylesheets/main.css">
  
  <script>
  	function showVideoDiv() {
   		document.getElementById('playDiv').style.display = "block";
  	}
  	
  	function showSigninDiv() {
   		document.getElementById('signinDiv').style.display = "block";
   		document.getElementById('memberNotfDiv').style.display = "none";
  	}
  	
  	$("input").keypress(function(event) {
	    if (event.which == 13) {
	        event.preventDefault();
	        $("form").submit();
	    }
	});
  </script>

  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  </head>
  <body>
  	<div class="tinted-image2" width="100%" height="100%">
  		<table style="padding-left:100px; margin-top: 0.67em;">
  			<tr>
  				<td> <img src="../images/icon2.png" alt="logo" height="60" width="60" /> </td>
  				<td> <h1> <span> UpTick! </span> </h1> </td>

<% if (!session.getAttribute("login").equals("inv")) {  %>
 				<td align="right" width="100%" style="padding-right:20%">
 					<form action="/servlet/welcome" method="post">
 						<div style="display: inline-block; padding-right:5px; vertical-align:middle; padding-bottom:18px"> <span class="login"> Welcome "<%= session.getAttribute("login") %>" </span> </div>
 						<input type="image" name="submit" value="logout" src="../images/logout.png" alt="logo" style="height:2em; width:2em"/> 
 					</form>
 				</td>
<% } %>
			</tr>
		</table>
	
		<div align="center">
			<span class="h2"> $0 Commission Stock & ETF Trading </span>
			<span class="h3"> Stop Paying $10 Per Trade </span>
		</div>
	
		<br>
	
<% if (session.getAttribute("login").equals("inv")) { %>
		<br>
		<div id="regDiv" align="center">

<% 		boolean signinFailed = false;
		if (session.getAttribute("registered") != null) {
%>
			<div style="margin-bottom:10px;">
				<span style="font-size:15px; color:rgb(184,0,0); font-weight:bold;"> <%= session.getAttribute("registered") %> </span>
			</div>			

<%			if (session.getAttribute("registered").equals("Invalid login"))
				signinFailed = true;
			session.removeAttribute("registered");      
		}
%>
			<div style="display:inline-block;" align="left">
				<form action="/servlet/welcome" method="post">
					<input type="text" name="request_email" placeholder="Enter your email" class="textbox"/>
				  	<input type="submit" name="submit" value="Get Early Access" class="button"/> <br>
				</form>
				
<%		if (signinFailed) { %>
				<div id="memberNotfDiv" style="margin-top:15px; display:none" align="center"> 
<%      } else { %>
				<div id="memberNotfDiv" style="margin-top:15px;" align="center"> 
<%		} %>
			  		<span style="font-size:15px"> Already a member? </span>
			  		<input type="submit" name="submit" value="Sign in" class="signin" onClick="showSigninDiv()"/> 
				</div>
			</div>
		</div>

		<br>

<% 		if (signinFailed) { %>
		<div id="signinDiv" style="margin-top:10px" align="center">
<%		} else { %>
		<div id="signinDiv" style="display:none; margin-top:10px" align="center">
<%		} %>
			<span class="h4"> Or Login in now to see your membership status. </span> <br>
			<div style="display:inline-block;" align="left">
				<form action="/servlet/welcome" method="post">
				  	<input type="text" name="login_id" placeholder="Enter your email" class="textbox"/><br>
				  	<input type="password" name="password" placeholder="Enter your password" class="textbox"/>
				  	<input type="submit" name="submit" value="Sign in" class="button"/> <br>
				</form>
			</div>
		</div>

		

<% } else { %>

<% } %>

		<br>
		<br>
		
		<div align="center">
			<table style="margin-bottom:10px" align="center">
				<tr>
					<td> <span style="font-size:2em; font-weight:bold; font-family:monospace; float:right"> Watch Intro Video &nbsp </span> </td>
		    		<td> <input type="image" name="play" value="show-div" onClick="showVideoDiv()" src="../images/Play3.png" alt="logo" height="60" width="60" style="float:right"/> </td>
				</tr>
			</table>
			<div id="playDiv" style="display:none; width:450px; height:350px; margin:auto;">
				<iframe width="420" height="315" src="http://www.youtube.com/embed/TTYUYR0KwT0" frameborder="0" allowfullscreen></iframe>
			</div>
		</div>
	</div>
  </body>
</html>
