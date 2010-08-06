<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Greening Events - Log In</title>

<link rel="shortcut icon" href="http://economicsnetwork.ac.uk/favicon.ico" type="image/x-icon" />
<link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/drupal_style_main.css" type="text/css" media="screen" />
<link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/style_print.css" type="text/css" media="print" />
<!-- <style type="text/css" media="screen">@import "./style.css";</style> -->
<meta http-equiv="Content-Style-Type" content="text/css" />
<meta http-equiv="Content-Language" content="en-uk" />
<meta name="date" content="" />
<link rel="alternate" type="application/rss+xml" title="RSS" href="http://www.economicsnetwork.ac.uk/news/econltsn.xml" />
<link rel="Help" href="http://economicsnetwork.ac.uk/tenways" />
</head>

<body class="not-front not-logged-in page-user one-sidebar sidebar-left">
<div id="doc2" class="yui-t2">
   <div id="hd"><div id="header"><a href="http://economicsnetwork.ac.uk/" name="top" id="top" accesskey="1"><img src="http://economicsnetwork.ac.uk/nav/acadlogo.gif" id="logo" alt="Economics Network of the Higher Education Academy" title="Home Page of the Economics Network" style="width:292px;height:151px;border:0" /></a>
<ul id="navlist">
<li class="toabout"><a href="http://economicsnetwork.ac.uk/about" accesskey="2">About Us</a></li>

<li class="topubs"><a href="http://economicsnetwork.ac.uk/journals" accesskey="3">Lecturer Resources</a></li>
<li class="tores"><a href="http://economicsnetwork.ac.uk/resources" accesskey="4">Learning Materials</a></li>
<li class="tofunds"><a href="http://economicsnetwork.ac.uk/projects" accesskey="5">Projects&nbsp;&amp; Funding</a></li>
<li class="tonews"><a href="http://economicsnetwork.ac.uk/news" accesskey="6">News&nbsp;&amp; Events</a></li>
<li class="tothemes"><a href="http://economicsnetwork.ac.uk/subjects/" accesskey="7">Browse by Topic</a></li>
<li id="help"><a href="http://economicsnetwork.ac.uk/tenways" style="color:#036" accesskey="?">Help</a></li>
</ul></div>
<div id="homelink"><a href="http://economicsnetwork.ac.uk/">Home</a></div></div>
   <div id="bd">
	<div id="yui-main">
	<div class="yui-b"><div class="yui-gc">
    <div class="yui-u first" id="content" style="width: 98%">
      <div id="content-header">
        <h1 class="title">Greening Events user account</h1>
          <div class="breadcrumb"><a href="./">Greening Events Home</a></div>
      </div> <!-- /#content-header -->

    <div id="content-area" style="margin-top: 1em">
    <fieldset style="width: 400px">

        <form method="POST" action="j_spring_security_check">

            <% if (request.getParameter("login_error") != null) { %>
            <p>Sorry, your login attempt failed. Please try again.</p>
            <% } %>

            <table>
                <tr>
                    <td>Username:</td>
                    <td><input type="text" name="j_username"/></td>
                </tr>
                <tr>
                    <td>Password:</td>
                    <td><input type="password" name="j_password"/></td>
                </tr>
            </table>

            <p><input type="submit" value="Log In"/></p>

            <%-- <p>If you don't have an account, please <a href="registration.do">register</a>.</p> --%>

            <p><a href="forgottenPassword.do">Forgotten your password?</a></p>

        </form>
    </fieldset>
        </div>
        </div>



    <div class="yui-u">


	    </div>
</div>
</div>
	</div>
	<div class="yui-b">


<!--googleoff: all--><div id="snav">
<div class="snavtop"></div><form method="get" action="http://search2.openobjects.com/kbroker/hea/economics/search.lsim" class="sform"><fieldset>
<input type="text" name="qt" size="15" maxlength="1000" value="" class="sbox" style="width:100px" /><input id="submit" type="submit" value="Search" class="gobutton" style="width:4em" /><input type="hidden" name="sr" value="0" /><input type="hidden" name="nh" value="10" /><input type="hidden" name="cs" value="iso-8859-1" /><input type="hidden" name="sc" value="hea" /><input type="hidden" name="sm" value="0" /><input type="hidden" name="mt" value="1" /><input type="hidden" name="ha" value="1022" /></fieldset></form>
    <div class="snavbtm"></div>
<div class="qjumptop"></div><form action="http://economicsnetwork.ac.uk/quickjump.asp" method="get" id="quickjump"><fieldset>
    <label for="quickjump"><b>Quickjump to:</b></label><br />
    <select name="jumpto" size="1" class="quickjumpmenu" style="font-size:95%" id="jumpto">
		<option value="http://economicsnetwork.ac.uk/awards/">Awards</option>
		<option value="http://economicsnetwork.ac.uk/books/">Books</option>
		<option value="http://economicsnetwork.ac.uk/teaching/casestudy.htm">Case Studies (Economics)</option>
		<option value="http://economicsnetwork.ac.uk/showcase/">Case Studies (Teaching)</option>
		<option value="http://economicsnetwork.ac.uk/contact/">Contact Us</option>
		<option value="http://economicsnetwork.ac.uk/cheer/">CHEER Journal</option>
		<option value="http://economicsnetwork.ac.uk/links/tl.htm">Economics Education</option>
		<option value="http://economicsnetwork.ac.uk/events/">Events</option>
		<option value="http://economicsnetwork.ac.uk/externals/">External Examiners</option>
		<option value="http://economicsnetwork.ac.uk/projects/">Funding</option>
		<option value="http://economicsnetwork.ac.uk/handbook/">Handbook for Lecturers</option>
		<option value="http://economicsnetwork.ac.uk/iree/">IREE Journal</option>
		<option value="http://economicsnetwork.ac.uk/news/centre.htm">News</option>
		<option value="http://economicsnetwork.ac.uk/links/reference.htm">Official Documents</option>
		<option value="http://economicsnetwork.ac.uk/links/othertl.htm">Online L&amp;T Materials</option>
		<option value="http://economicsnetwork.ac.uk/links/sources.htm">Online Sources</option>
		<option value="http://economicsnetwork.ac.uk/qnbank/">Question Bank</option>
		<option value="http://economicsnetwork.ac.uk/showcase/">Reflections on Teaching</option>
		<option value="http://economicsnetwork.ac.uk/pds/">Regional Contacts</option>
		<option value="http://economicsnetwork.ac.uk/software.htm">Software Guide</option>
		<option value="http://economicsnetwork.ac.uk/books/">Textbook Guide</option>
		<option value="http://economicsnetwork.ac.uk/subjects">Themes</option>
		<option value="http://economicsnetwork.ac.uk/links/depts.htm">UK Departments</option>
	</select><input type="submit" value="Go" class="gobutton" />
</fieldset></form><div class="qjumpbtm"></div>

            <div id="sidebar-left"><div id="sidebar-left-inner" class="region region-left">
          <div id="block-block-7" class="">



  <div class="content">
    <form style="padding: 1px; margin-top: 1.5em;" method="post" action="http://www.jiscmail.ac.uk/cgi-bin/webadmin">
    <fieldset><input type="hidden" value="ECON-NETWORK" name="SUBED2" /><input type="hidden" value="1" name="A" /><h4>Monthly Email Updates</h4><span style="font-size: smaller;">from the Economics Network</span><br />
    <input type="text" onclick="if (this.form.s.value='Your Email Address'){this.form.s.value=''}" style="width: 167px;" name="s" value="Your Email Address" /> <input type="submit" class="gobutton" value="Join" name="b" /> <input type="submit" class="gobutton" value="Leave" name="a" /> </fieldset>
</form>  </div>

</div> <!-- /block -->
        </div></div><!-- /#sidebar-left-inner, /#sidebar-left -->
      <p class="user_link"><a href="http://economicsnetwork.ac.uk/user">Team member? Log in</a></p>
</div>

</div>
	</div>
   <div id="ft">
<p id="footer"><span id="footer-message"><a href="http://economicsnetwork.ac.uk/copyright">&copy; 2010</a>, <a href="http://www.bris.ac.uk/">University of Bristol</a></span>
		<span id="footnav"><a href="http://economicsnetwork.ac.uk/">Home</a> | <a href="#top">Top</a> | <b>Share this page:</b> <a title="Send an email with this web address" href="#" onclick="javascript:location.href='mailto:?SUBJECT='+document.title+'&amp;BODY='+escape(location.href)">Email</a>, <a href="#" title="Share this link on the Delicious bookmarking service" onclick="javascript:location.href='http://delicious.com/post?v=4;url='+encodeURIComponent(location.href)+';title='+encodeURIComponent(document.title)">Delicious</a>, <a title="Share this page on the Google Bookmarks service" href="#" onclick="javascript:location.href='http://www.google.com/bookmarks/mark?op=add&amp;bkmk='+encodeURIComponent(location.href)+'&amp;title='+encodeURIComponent(document.title)">Google</a>, <a href="http://www.addthis.com/bookmark.php" title="Share this page on your preferred bookmarking service via AddThis.com">more</a></span></p>
</div>
</div>
<script src="http://economicsnetwork.ac.uk/gatag.js" type="text/javascript"></script>
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try{
var pageTracker = _gat._getTracker("UA-1171701-1");
pageTracker._trackPageview();
} catch(err) {}</script>
</body>
</html>
            
