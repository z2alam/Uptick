<!DOCTYPE html>
<html>

<head>
<title>CloudMessenger</title>
<script>   
    function confirmDelete() {
        var x;
        if (confirm("Are you sure you want to Delete All User Data?") == true) {
            x = "You pressed OK!";
        } else {
            x = "You pressed Cancel!";
        }
        document.getElementById("demo").innerHTML = x;
    }
</script>
<style>
    #divHeader {
        background:Teal;
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
        background:Teal;
        color:white;
        clear:both;
        text-align:center;	
    	width: 100%;
    	position: absolute; 	 
    }
    body 
    {
        background-color:#d9fedf
    }
    button 
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
        width: 166px;
    }
    button:hover {
        background: #FF9900;
        text-decoration: none;
    } 
    td{
        padding:0px 0px 0px 0px; 
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
        padding:90px 0px 0px 0px;
        text-align:center;   
    	width: 100%;
    	height:700px;
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
        width:500px;
        height:200px;  
        border: solid 1px red;
        background: white;
        padding:40px 0px 20px 0px;
        margin:auto;
        border-radius:.7em;
    }
    {
    	display:block;
    	width:100%;
        padding:10px 0px 10px 0px;
        font-size:large;
        font-family:oblique;
        text-decoration:none;
    }
    #divDeleteAllUsers
    {
    	padding: 5px;
    }
    	

</style>
</head>

<!-- ------------ BODY --------------------------------------------------------------------------------- -->

<body>

    <div id="divHeader">
        <table id="tableHead">
            <tr>
                <td class="tdHeader">
                    <div id="divLogo">
                        <img id="CMlogo" src="CM.jpg" style="height:60px; width:60px;">
                    </div>
                </td>
                <td class="tdHeader">
                    <div id="divHeaderMsg">
                        <h2>Welcome to CloudMessenger Manager!</h2>
                    </div>
                </td>
                <td class="tdHeader">
                    <div id="divLogoutBtn">
                        <a href="<%= session.getAttribute("logout_url") %>" >
			                <button value="Logout" type="submit" name="submit"> Logout</button>
		                </a>
                    </div>
                </td>
            </tr>
        </table>
    </div>

    <form action="/servlet/ManagerUI" method="post">   
        <div id="divWrapper">
            <div id="divErrMsg" style="padding:5px">
                <font style="margin-left:10px" size="3" color="red">
                    Error message!
                </font>
            </div>
            <div id="divInner">
                <div id="ctrlHead" >
                     <span style="font-size: larger; font-style:normal">
                         Do you want to delete All data of All Users in the CloudMessenger?
                     </span>
                </div>                    
                <!-- ------------------- Write a New Message --------------------------------- -->
                <div id="divDeleteAllUsers" >
                	<button value="SendMessage" type="submit" name="submit" onclick="confirmDelete()">Delete All User Data</button>
                </div>    
            </div> <!-- <div id="divInner"> -->
            <div id="divSpaceBottom"></div>
        </div> <!-- divWrapper -->
    </form>


    <footer>
    <p> We hope you are enjoying your experience here | This website is made by <em>Hatif Sattar</em> and <em>Zohaib Alam</em> as part of Assignment 2 for the course <em>ECE1779 - Intro to Cloud Computing</em> | This website is recommended to be viewed in Google Chrome</p>
    </footer>

</body>
</html>
