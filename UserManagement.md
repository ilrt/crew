# User Management #

The user management system uses Spring Security and comprises of users, groups and roles. One or more roles can be assigned to a group and one or more groups can be assigned to a user.

## Roles ##

A role is the authority mechanism used to determine whether or not a user can perform an administrative activity or view data within the CREW application.

The system has four predefined roles:-

  * ANONYMOUS - a role that is allocated to the anonymous user, i.e. those people who browse the CREW web application without logging in with credentials.
  * USER - a role that is allocated to registered users.
  * ADMIN - a role that is allocated to the administrative user.
  * HARVESTER\_ADMIN - a role that allows harvester administrative privileges but not full administrative privileges.

To create new roles select "Admin" -> "List Roles" -> "Create Role" and fill in the details in the form and select "Add Role". The form has the following fields:

  * ROLEID - the unique key used to represent the role within the security system. The convention is to use alpha-numeric characters without spaces, e.g. "ROLE\_ONE".
  * Name - a name for the role, e.g. "Role One".
  * Description - a brief description of the role, e.g. "An example Role".

![http://farm4.static.flickr.com/3598/3390212556_9767525f06_o.jpg](http://farm4.static.flickr.com/3598/3390212556_9767525f06_o.jpg)

## Groups ##

To provide the greatest flexibility the roles are assigned to groups rather than directly to the users. The system has three predefined groups:-

  * ADMIN\_GROUP - the group assigned to users will full administrative privileges.
  * HARVESTER\_GROUP - the group assigned to users with harvester administrative privileges. This allows a user to add and delete event data from the CREW system.
  * USER\_GROUP - the group automatically assigned to users when their account is created.

To create a new group select "Admin" -> "List Groups" -> "Create Group" and fill in the details in the form and select "Add Group". The form has the following field:

  * GROUP ID - the unique key used to represent the role within the security system. The convention is to use alpha-numeric characters without spaces, e.g. "GROUP\_ONE".
  * Name - a name for the role, e.g. "Group One".
  * Description - a brief description of the role, e.g. "An example Group".

![http://farm4.static.flickr.com/3565/3390224546_ef92c60927_o.jpg](http://farm4.static.flickr.com/3565/3390224546_ef92c60927_o.jpg)

After selecting "Add Group" you will then be able to add roles to the group:

![http://farm4.static.flickr.com/3459/3389450283_06b3fdd6a3.jpg](http://farm4.static.flickr.com/3459/3389450283_06b3fdd6a3.jpg)

You can then add additional roles, or remove roles assigned to the group.

![http://farm4.static.flickr.com/3455/3389461133_b7ca7f1a04.jpg](http://farm4.static.flickr.com/3455/3389461133_b7ca7f1a04.jpg)

## Users ##

The system has one pre-generated users, ADMIN. Others users must register with the system via the registration form:

![http://farm4.static.flickr.com/3457/3390283892_c22fac835c.jpg](http://farm4.static.flickr.com/3457/3390283892_c22fac835c.jpg)

An administrative user can then assign groups to a user. Select "List Users" -> "Edit User":

![http://farm4.static.flickr.com/3610/3389480017_cd6505b94e_o.jpg](http://farm4.static.flickr.com/3610/3389480017_cd6505b94e_o.jpg)

You can then add or remove groups available to the user:

![http://farm4.static.flickr.com/3641/3389488363_6252bdca38_o.jpg](http://farm4.static.flickr.com/3641/3389488363_6252bdca38_o.jpg)