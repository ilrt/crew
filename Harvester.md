# Harvesting Event Data #

It is possible to harvest event data from remote web servers. The data must be in the RDF/XML format and conform to a schema that the system can understand. An example has been provided. After the initial data harvest the harvester will periodically check the remote web servers to see if the data has been updated.

### Adding a new Harvest Source ###

To add a new event into the system you must add a new Harvest Source. Select "Admin" - "Add Harvest Source". Note: you must have Harvester Administrator privileges to add a new harvest source; if you do not have the required privileges the relevant links will not be available.

In the form under "Harvester Source Details" you can add the following information:

  * Location: The URL of the file holding the event data. (REQUIRED)
  * Name: The name of the harvest source, e.g. "JISC Conference 2009". (OPTIONAL)
  * Description: A description of the source, e.g. "The 2009 JISC Conference, held in Edinburgh". (OPTIONAL)
  * Blocked: A checkbox that indicates whether or not you want the harvester to retrieve data from the URL. An unchecked box will allow the harvester to harvest the data.

The "Access Controls" section allows you to specify who can see the event data. This is discussed in more detail later. However, the default permissions allow all authenticated and unauthenticated (anonymous) users of CREW to see the event details.

![http://farm4.static.flickr.com/3601/3384038539_c1e24268b8.jpg](http://farm4.static.flickr.com/3601/3384038539_c1e24268b8.jpg)

When you have added the relevant details click "OK" and you will be directed to a list of sources. Select the source that you have just added and select "Harvest Source Now". After a few seconds the data will now be available in the system. The "Last Visit" should have the current date and time and the "Last Status" should be "200".

![http://farm4.static.flickr.com/3568/3384038351_3839626b32.jpg](http://farm4.static.flickr.com/3568/3384038351_3839626b32.jpg)

### Editing a Harvest Source ###

The edit form is identical to adding a new harvest source and you can edit all details of a harvest source except for the Location. If the location of the data has changed, you will need to delete the harvest source and create a new one with the updated location URL.

### Deleting a Harvest Source ###

Deleting a harvest source will:

  * Delete the harvest source details (Location, Name etc) and the access control information related to that source.
  * Delete the event data that was harvested from the location URL of the harvest source.

To delete a harvest source you need to review the list of available sources by selecting "Admin" then "List Harvest Sources". Choose the relevant source and select "Delete".

![http://farm4.static.flickr.com/3447/3384038269_a301c379d1.jpg](http://farm4.static.flickr.com/3447/3384038269_a301c379d1.jpg)

### Access Controls ###

The access controls specify the roles that can READ, WRITE and DELETE a harvest source.

![http://farm4.static.flickr.com/3432/3384038175_18e917ce41.jpg](http://farm4.static.flickr.com/3432/3384038175_18e917ce41.jpg)

  * READ - this permission defines who can see the event data that will be harvested from the location of the harvest source. By default anyone can see a harvested event.
  * WRITE - this permission defines who can update the information of a harvest source and who can initiate a manual harvest by selecting the "Harvest Source Now" button on the list of available resources.
  * DELETE - this permission defines who can delete the harvest source and its related event data.

The default roles in the system are:

  * ADMIN - the main administrative user. It is recommended not to alter the permissions for this role.
  * ANONYMOUS - unauthenticated (anonymous) users that are browsing the web application.
  * USER - authenticated users that are browsing the web application.
  * HARVESTER\_ADMIN - an administrative role defined for harvester administration. This allows power users or conference organizers to be granted harvester privileges without administrative responsibilities over the rest of the application.

Additional roles can be created by an administrator and these roles can be assigned access rights to harvest sources. This can be useful because you can create special roles that will have administrative responsibilities for specific harvest sources. For example, a JISC programme manager might be responsible for VRE events. If we have a role JISC\_VRE\_MANAGER and this and the ADMIN role are the only ones with WRITE and DELETE privileges, then only those users assigned to a group that is allocated these roles will be able to see the list of source and be able to Edit, Delete and Harvest them.

![http://farm4.static.flickr.com/3627/3384037301_3e23418035.jpg](http://farm4.static.flickr.com/3627/3384037301_3e23418035.jpg)

Further information is available on the relationship between roles, groups and users.