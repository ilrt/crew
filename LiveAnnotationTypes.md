# Introduction #
The default configuration file for the live annotation types provides the following types
<table>
<tr><td><img src='http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/question.png' /></td><td><b>Question</b>

The question annotates a question in the presentation or during a question and answer session. This only provides a single multi-line input box for the question.</td></tr>
<tr><td><img src='http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/answer.png' /> </td><td> <b>Answer</b>

The answer type is there to capture the answers to a previous question. This type again only provides a single multi-line input box for the answer.</td></tr>
<tr><td> <img src='http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/comment.png' /> </td><td> <b>Comment</b>

The comment type is for general comments made during the presentation.<br>
</td></tr>
<tr><td><img src='http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/important.png' /> </td><td><b>Important Point</b>

The important point can be used to highlight special remarks or show that the presenter put emphasis on certain issues.<br>
</td></tr>
<tr><td><img src='http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/link.png' /> </td><td><b>Linked Resource</b>

A linked resources is a more complex annotation. This provides input boxes for a <b><i>name</i></b> and a <b><i>URL</i></b> of the resource. It also provides the multi-line input box for the user to give a comment on the resource that is annotated. If the user does not specify the <b><i>name</i></b> or the <b><i>URL</i></b> the annotation will be converted into a <b>Comment</b>.<br>
</td></tr>
<tr><td><img src='http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/person.png' /> </td><td><b>Person</b>

A person annotation provides input boxes for the <b><i>name</i></b>, an <b><i>email</i></b> address and a <b><i>URL</i></b>. It also provides the multi-line input box for the user to give a comment.<br>
If all the text fields are left blank the annotation will be converted into a <b>Comment</b>.<br>
</td></tr>
<tr><td><img src='http://crew.googlecode.com/svn/wiki/images/LiveAnnotations/types/slide.png' /> </td><td> <b>Slide</b>

The slide annotation is to define slide titles or the general content summary of a slide. These annotations are assigned to the timestamp of the previous slide change that is calculated by the recording tool.<br>
</td></tr></table>

[Other types](DefiningLiveAnnotationTypes.md) can be defined by the user through the modification of the [liveannotations.xml](LiveAnnotationsXML.md) file.