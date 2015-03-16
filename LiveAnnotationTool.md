# Introduction #
The LiveAnnotationTool is a web based user interface that is started by the [event recorder](Recording.md) to allow the event audience to annotate a presentation. The annotations are recorded with the time-stamp of the annotation-author starting to write the annotation. This allows to connect the annotation to the recording and the time when the author thought of it. As the annotation tool is run on the same machine as the recording application, these time-stamps also avoids time differences between the recording machine and the machine taking the annotations.

The LiveAnnotationTool appears like a standard "chat" tool. When the user connects to the LiveAnnotationTool he should provide an e-Mail address and a "nickname" that is used in the current session.

![http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/session.png](http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/session.png)

The name is used in the roster of the chat tool so other people can see who else is annotating sessions otherwise this information is not stored. The email address is used when the annotations are uploaded to the server to allow the annotation author to
confirm the live annotations.

Once the user provided the name and email address the Annotation tool is shown:

![http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/annotations.png](http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/annotations.png)

The annotation tool provides three main areas:
  * **chat** (top left)
> the chat provides the user a view of annotations done. this is a scrolling box where the user can read all the annotations of the current session. As long as a user is connected to a session this box also provides a button ![http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/edit.png](http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/edit.png) to edit his own comments.
> The second button in the "chat" part is to respond  ![http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/response.png](http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/response.png) to a comment another user has provided.
  * **roster** (top right)
> the roster provides the list of users currently logged on to the annotation tool. This is purely informational and not stored in the live annotation system. The list shows the nicknames of the users. If a user does not provide a nickname the email address is shown.
  * **[list of annotation types](LiveAnnotationTypes.md)** (bottom)
> the button box on the bottom provides the selection of [annotation types](LiveAnnotationTypes.md) the user can choose from. Clicking on one of the buttons changes this selector into a set of input boxes to create the annotation:
> ![http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/annotatePerson.png](http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/annotatePerson.png)



