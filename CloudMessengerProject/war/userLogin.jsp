<!DOCTYPE html>
<html>
<head>
    <title>CloudMessenger</title>
    <link rel="icon" 
      type="image/png" 
      href="../images/CM-icon.png">
    
    <!-------------------STYLE -------------------------------------------------------- -->
    <style>
    footer {
        background:clear;
        color:white;
        clear:both;
        text-align:center;	
    	width: 100%;
        bottom:0;
    	position: absolute; 
    	padding:0px;	
    }
    body {
        overflow-x:hidden;
        overflow-y:hidden;
    }
    span {
        font-family: Georgia;
        font-size: 17px;
        background-color:clear;
    }
    p
    {
        font-family: Georgia;
        font-size: small;
        background-color:clear;
    }
    #divSpaceTop{
        height:100px;
    }
    #divSpaceBottom{
        height:10px;
    }
    #divLoginOuter
    {
    	margin:0 auto;
    	text-align:center;
    	top: 0;	 
    	width: 100%;
    	position: absolute; 
    	
    }
    #divCover
    {
    	border: solid 1px #002040;
    	background: #002c59;
    	color:Aqua;
    	padding:30px;
        border-radius:2em;
    	margin:0 auto;
        width:45%;
    }
    #divLoginInner
    {
        width:70%;     
    	border: solid 1px #002040;
    	background: white;
    	padding: 15px;
    	margin:0 auto;
        border-radius:.7em;
        -moz-box-shadow:    inset 0 0 12px #000000;
        -webkit-box-shadow: inset 0 0 12px #000000;
        box-shadow:         inset 0 0 12px #000000;
    }

    
    </style>
</head>
<body background="../images/login-bg2-1.jpg" no-repeat center center fixed; 
  -webkit-background-size: contain;
  -moz-background-size: contain;
  -o-background-size: contain;
  background-size: contain;
  >
<!------------ BODY --------------------------------------------------------------- -->

    <div id="wrap">
    <form action="/servlet/GoogleLogin" method="post">      
        <div id="divLoginOuter" class="myForms">
            <div id="divSpaceTop"></div>
                <div id="divCover">
                    <div id="divIntro">
                        <h2>Welcome to Cloud Messenger - Where Your Messages Live</h2>
                        <h3><em>ECE1779 PROJECT</em> developed by <em>Zohaib Alam - Hatif Sattar</em><br></h3>
                    </div>
                    <div id="divLoginInner">
                        <div style="margin-top: 5px; margin-bottom: 5px ">
                            <span style="font-size: larger; font-style: oblique; color:Teal">Please Sign up or Sign in with Gmail Account!</span>
                        </div>
                        <hr />
                        <div style="text-align: center">
                            <div>
                                <a href='<%= session.getAttribute("login_url") %>'>
                                    <img id="g-img" src="../images/google_logo.jpg" style="height:60px; width:300px;">
                                </a>    
                            </div>
                            <div style="color:Teal;">
                            	
                            </div>
                        </div>
                    </div><!-- divLoginInner --> 
                </div>
            <div id="divSpaceBottom"></div>
        </div><!-- divLoginOuter -->  

    </form> 
    </div> <!-- wrap -->
    <footer>
        <h3> We hope you enjoy your experience here! </h3>
        <h3> This website is made by <em>Hatif Sattar</em> and <em>Zohaib Alam</em> as part of Assignment 2 for the course <em>ECE1779 - Intro to Cloud Computing</em> |
        This website is recommended to be viewed in Google Chrome</h3>
    </footer>
</body>
</html>