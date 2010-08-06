<%@ include file="includes/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="crew" uri="http://www.crew_vre.net/taglib" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="true" contentType="text/html;charset=UTF-8" language="java" %>

<head>
    <title><fmt:message key="profile.link.updateDetails"/></title>
    <style type="text/css"
           media="screen">@import "${pageContext.request.contextPath}/style.css";</style>
    <style type="text/css" media="screen">@import "./style.css";</style>
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/drupal_style_main.css" type="text/css" media="screen" />
    <link rel="stylesheet" href="http://economicsnetwork.ac.uk/style/style_print.css" type="text/css" media="print" />
<c:if test="${not empty event}">
    <meta name="caboto-annotation" content="${event.id}"/>
</c:if>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <meta http-equiv="Content-Language" content="en-uk" />
    <link rel="Help" href="http://economicsnetwork.ac.uk/tenways" />
    <c:if test="${not empty feedList}">
        <c:forEach var="feed" items="${feedList}">
    <link rel="alternate" href="${feed.feedUrl}" type="${feed.contentType}" title="${feed.title}"/>
        </c:forEach>
    </c:if>
</head>
<body>
    <div id="doc2" class="yui-t2">
        <div id="hd">
            <div id="header">
                <a href="http://economicsnetwork.ac.uk/" name="top" id="top" accesskey="1">
                        <img src="http://economicsnetwork.ac.uk/nav/acadlogo.gif" id="logo" alt="Economics Network of the Higher Education Academy" title="Home Page of the Economics Network" style="width:292px;height:151px;border:0" />
                </a>
                <ul id="navlist">
                    <li class="toabout">
                            <a href="http://economicsnetwork.ac.uk/about" accesskey="2">About Us</a>
                    </li>
                    <li class="topubs">
                            <a href="http://economicsnetwork.ac.uk/journals" accesskey="3">Lecturer Resources</a>
                    </li>
                    <li class="tores">
                            <a href="http://economicsnetwork.ac.uk/resources" accesskey="4">Learning Materials</a>
                    </li>
                    <li class="tofunds">
                            <a href="http://economicsnetwork.ac.uk/projects" accesskey="5">Projects&nbsp;&amp; Funding</a>
                    </li>
                    <li class="tonews">
                            <a href="http://economicsnetwork.ac.uk/news" accesskey="6">News&nbsp;&amp; Events</a>
                    </li>
                    <li class="tothemes">
                            <a href="http://economicsnetwork.ac.uk/subjects/" accesskey="7">Browse by Topic</a>
                    </li>
                    <li id="help">
                            <a href="http://economicsnetwork.ac.uk/tenways" style="color:#036" accesskey="?">Help</a>
                    </li>
                </ul>
            </div>
            <div id="homelink">
                    <a href="http://economicsnetwork.ac.uk/">Home</a>
            </div>
        </div>
        <div id="bd">
            <div id="yui-main">
                <div class="yui-b">
                    <div class="yui-gc">
                        <div class="yui-u first" id="content" style="width: 98%"> <!-- width: 66%; float: left -->
                            <div id="content-header">
                                <h1 class="title"><fmt:message key="profile.link.updateDetails"/></h1> <%-- Mid column main page title --%>
                                  <div class="breadcrumb"><a href="${pageContext.request.contextPath}">Greening Events Home</a></div>

                                  <div class="tabs" style="width:100%">
                                      <ul class="tabs primary">
                                            <li><a href="displayProfile.do"><fmt:message key="profile.link.display"/></a></li>
                                            <li><a href="changePassword.do"><fmt:message key="profile.link.changePassword"/></a></li>
                                        </ul>
                                        <div style="position:relative"></div>
                                  </div>

                                  </div> <!-- /#content-header -->

    <div id="content-area" style="margin-top: 2em">

            <form:form action="./updateUserDetails.do" method="post" commandName="userForm">

                <%-- USER DETAILS --%>

                <fieldset>
                    <h4>Details for ${userForm.name}</h4>

                    <p><fmt:message key="user.edit.details"/></p>

                    <table class="details-table">
                        <tr>
                            <td><strong><fmt:message key="user.username"/></strong></td>
                            <td><form:input path="username" readonly="true"/></td>
                            <td><form:errors path="username"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="user.name"/></strong></td>
                            <td><form:input path="name"/></td>
                            <td><form:errors path="name"/></td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="user.postcode"/></strong></td>
                            <td><form:input path="postcode"/></td>
                            <td><form:errors path="postcode"/></td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td colspan="2">
                                <strong><fmt:message key="user.postcode.warning"/></strong>
                            </td>
                        </tr>
                        <tr>
                            <td><strong><fmt:message key="user.email"/></strong></td>
                            <td><form:input path="email"/></td>
                            <td><form:errors path="email"/></td>
                        </tr>
                    </table>
                    <p>
                        <input type="submit" name="updateUser" value="Update"/>
                        <input type="submit" value="Cancel"/>
                    </p>
                </fieldset>

            </form:form>

        </div>
        </div>



    <div class="yui-u"> </div>

                    </div>
                </div>
            </div>

<%-- LEFT NAV --%>
            <div class="yui-b">		<!--googleoff: all-->
                <div id="snav">
                    <div class="snavtop"></div>
                    <form method="get" action="http://search2.openobjects.com/kbroker/hea/economics/search.lsim" class="sform">
                        <fieldset>
                            <input type="text" name="qt" size="15" maxlength="1000" value="" class="sbox" style="width:100px" />
                            <input id="submit" type="submit" value="Search" class="gobutton" style="width:4em" />
                            <input type="hidden" name="sr" value="0" />
                            <input type="hidden" name="nh" value="10" />
                            <input type="hidden" name="cs" value="iso-8859-1" />
                            <input type="hidden" name="sc" value="hea" />
                            <input type="hidden" name="sm" value="0" />
                            <input type="hidden" name="mt" value="1" />
                            <input type="hidden" name="ha" value="1022" />
                        </fieldset>
                    </form>
                    <%-- Left nav links --%>
                    <div class="content">
                        <ul>
                            <%@ include file="includes/headerLinks.jsp" %>
                        </ul>
                    </div>
                </div>
                <div class="snavbtm"></div>
                <div class="qjumptop"></div>
                <form action="http://economicsnetwork.ac.uk/quickjump.asp" method="get" id="quickjump">
                    <fieldset>
                        <label for="quickjump">
                            <b>Quickjump to:</b>
                        </label>
                        <br />
                        <select name="jumpto" size="1" class="quickjumpmenu" style="font-size:95%" id="jumpto">
                            <option value="http://www.economicsnetwork.ac.uk/awards/">Awards</option>
                            <option value="http://www.economicsnetwork.ac.uk/books/">Books</option>
                            <option value="http://www.economicsnetwork.ac.uk/teaching/casestudy.htm">Case Studies (Economics)</option>
                            <option value="http://www.economicsnetwork.ac.uk/showcase/">Case Studies (Teaching)</option>
                            <option value="http://www.economicsnetwork.ac.uk/contact/">Contact Us</option>
                            <option value="http://www.economicsnetwork.ac.uk/cheer/">CHEER Journal</option>
                            <option value="http://www.economicsnetwork.ac.uk/links/tl.htm">Economics Education</option>
                            <option value="http://www.economicsnetwork.ac.uk/events/">Events</option>
                            <option value="http://www.economicsnetwork.ac.uk/externals/">External Examiners</option>
                            <option value="http://www.economicsnetwork.ac.uk/projects/">Funding</option>
                            <option value="http://www.economicsnetwork.ac.uk/handbook/">Handbook for Lecturers</option>
                            <option value="http://www.economicsnetwork.ac.uk/iree/">IREE Journal</option>
                            <option value="http://www.economicsnetwork.ac.uk/news/centre.htm">News</option>
                            <option value="http://www.economicsnetwork.ac.uk/links/reference.htm">Official Documents</option>
                            <option value="http://www.economicsnetwork.ac.uk/links/othertl.htm">Online L&amp;T Materials</option>
                            <option value="http://www.economicsnetwork.ac.uk/links/sources.htm">Online Sources</option>
                            <option value="http://www.economicsnetwork.ac.uk/qnbank/">Question Bank</option>
                            <option value="http://www.economicsnetwork.ac.uk/showcase/">Reflections on Teaching</option>
                            <option value="http://www.economicsnetwork.ac.uk/pds/">Regional Contacts</option>
                            <option value="http://www.economicsnetwork.ac.uk/software.htm">Software Guide</option>
                            <option value="http://www.economicsnetwork.ac.uk/books/">Textbook Guide</option>
                            <option value="http://www.economicsnetwork.ac.uk/subjects">Themes</option>
                            <option value="http://www.economicsnetwork.ac.uk/links/depts.htm">UK Departments</option>
                        </select>
                        <input type="submit" value="Go" class="gobutton" />
                    </fieldset>
                </form>
                <div class="qjumpbtm"></div>
                <div id="sidebar-left">
                    <form style="padding: 1px; margin-top: 1.5em;" method="post" action="http://www.jiscmail.ac.uk/cgi-bin/webadmin">
                        <fieldset>
                            <input type="hidden" value="ECON-NETWORK" name="SUBED2" />
                            <input type="hidden" value="1" name="A" />
                            <h4>Monthly Email Updates</h4>
                            <span style="font-size: smaller;">from the Economics Network</span>
                            <br />
                            <input type="text" onclick="if (this.form.s.value='Your Email Address'){this.form.s.value=''}" style="width: 167px;" name="s" value="Your Email Address" />
                            <input type="submit" class="gobutton" value="Join" name="b" />
                            <input type="submit" class="gobutton" value="Leave" name="a" />
                        </fieldset>
                    </form>
                    <p class="user_link">
                        <a href="http://economicsnetwork.ac.uk/user">Team member? Log in</a>
                    </p>
                </div>
                </div>
            </div>

 <%@ include file="includes/footer.jsp" %>

    </div>
    <script src="http://www.economicsnetwork.ac.uk/gatag.js" type="text/javascript"></script>
    <script type="text/javascript">var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));</script>
    <script type="text/javascript">try{var pageTracker = _gat._getTracker("UA-1171701-1");pageTracker._trackPageview();} catch(err) {}</script>



</body>
</html>

