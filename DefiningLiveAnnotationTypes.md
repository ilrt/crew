# Introduction #
The Live Annotation types and the general settings of the live annotation tool are defined in the [liveannotations.xml](LiveAnnotationsXML.md) file. This file consists of two sections:

  * **properties**
> The properties section contains the general settings for the [live annotation tool](LiveAnnotationTool.md). This includes the size of the annotation type buttons, the size of the thumbnail images inside the chat part of the [live annotation tool](LiveAnnotationTool.md), the size of the annotation specific icons highlighting individual parts of an annotation and a list of colours to be used by different users of the annotation tool.
  * **live annotation types**
> The live annotation types section provides a list that defines the information and appearance of each individual annotation type to be created and used by the [live annotation tool](LiveAnnotationTool.md) and the CrewPlayer.

# Configuring the Live Annotation Tool #

The [liveannotations.xml](LiveAnnotationsXML.md) file consists of two sections:
```
<?xml version="1.0" encoding="UTF-8"?>
<liveAnnotations>
    <properties>
```

> The properties section contains the general settings for the [live annotation tool](LiveAnnotationTool.md).

```
    </properties>
    <liveAnnotationType *...* >
```

> Multiple live annotation type sections provide definitions of the information and appearance of each individual [annotation type](LiveAnnotationTypes.md) to be created and used by the [live annotation tool](LiveAnnotationTool.md) and the CrewPlayer.

```
    </liveAnnotationType>
    <liveAnnotationType *...* >
```

> ...

```
    </liveAnnotationType>
</liveAnnotations>
```

## Property Definitions ##
CrewLiveAnnotationUrl
```
    <properties>
        <button width="100" height="100" />
        <thumbnail width="30" height="30" />
        <itemthumb width="20" height="20" />
        <textColours>
            <colour value="#C11B17"/>
            <colour value="#347235"/>
            <colour value="#0000FF"/>
            <colour value="#800517"/>
            <colour value="#000000"/>
        </textColours>
    </properties>
```

  * **`<button width="..." height="..." />`**
> defines the width and height of the buttons for the annotation types on the bottom of the [live annotation tool](LiveAnnotationTool.md).
  * **`<thumbnail width="..." height="..." />`**
> defines the with and height of the thumbnail images that indicate the annotation type inside the 'chat part' of the [live annotation tool](LiveAnnotationTool.md).
  * **`<itemthumb width="..." height="..." />`**
> defines the with and height of the images used in more complex annotations (i.e. Person) that indicate special parts of the annotation (i.e. email, URL) in the 'chat part' of the [live annotation tool](LiveAnnotationTool.md).
  * **`<textColours> <colour value="..."/> ... </textColours>`**
> defines a list of colours (specified using html-style colour definitions) to be used by the 'chat part' of the [live annotation tool](LiveAnnotationTool.md). Each user is assigned a colour from this list when logging on to the annotation tool.

## Live Annotation Type Definitions ##

```
    <liveAnnotationType name="Linked-Resource" index="6">
        <button image="/img/link.png" visible="true" />
        <thumbnail name="/img/small/link.png" />
        <colour value="#5CD43A" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <contains name="CrewLiveAnnotationTitle" type="text" displayname="Name" />
        <contains name="CrewLiveAnnotationUrl" type="text" image="/img/small/link.png" displayname="Url" />
        <format>
            <input>
                &lt;a href="${CrewLiveAnnotationUrl}"&gt;
                ${CrewLiveAnnotationTitle}
                (&lt;img src="${CrewLiveAnnotationUrl.image}"/&gt;
                {CrewLiveAnnotationUrl})&lt;/a&gt;
                ${CrewLiveAnnotationComment}
            </input>
            <player>
                &lt;b&gt;${CrewLiveAnnotationTitle}&lt;/b&gt;&lt;br/&gt;
                &lt;font color="#0000ff"&gt;${CrewLiveAnnotationUrl}&lt;/font&gt;&lt;br/&gt;
                ${CrewLiveAnnotationComment}
            </player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
        <convertsTo>
            <type name="Reference"/>
            <type name="URL-Resource"/>
            <type name="Comment"/>
        </convertsTo>
    </liveAnnotationType>
```

  * **`<liveAnnotationType name="..." [type="..."] [index="..."]> ... </liveAnnotationType>`**
> defines the name, type and the order in which the CrewPlayer uses the liveAnnotationType.
    * **`name`** – The name of the live annotation type.
    * **`type`** – The type that a live annotation is stored as, if this value is unset the live annotation will be stored under its name.
    * **`index`** – The CrewPlayer shows for each of the annotation types that was entered during the recording a bar in time-slider. These bars are shown in the order provided by the index value.

  * **`<button image="..." visible="`**<tt>{<b>true</b>, <b>false</b>, <b>inline</b>}</tt>_**`" />`**
> defines the image to be used for the button in the [live annotation tool](LiveAnnotationTool.md). The visible value defines how the annotation is used:
    * **`visible="true"`** – the button is shown in the normal selection bar for live annotation types
    * **`visible="false"`** – the button is not show in the selection bar, this is used for annotation types which are exclusively used as "conversions" for other types. The user cannot input this type but it will be used to display as a better match for anntotations that are filled in incompletely (i.e. a person is specified without an email-address).
    * **`visible="inline"`** – the button is shown inside the 'chat tool'. This can be used to create annotations in response to annotations of other users._

  * **`<thumbnail name="..." />`**
> defines the image to be used to indicate the live annotation type in the 'chat part' of the [live annotation tool](LiveAnnotationTool.md).

  * **`<colour value="..." />`**
> the html-style colour value is used by the CrewPlayer for the indicator of the time-stamp of the annotation on the time-slider.

  * **`<contains name="..." type="`**<tt>{<b>text</b>, <b>textarea</b>, <b>messageId</b>}</tt>_**`" [displayname="..."] [`**_<tt><i>user-defined-variable</i></tt>**`="..."]* />`**
> defines the relationship between the user input and the variables defined for the annotations in the CREW databases. The CREW databases define their data structures in the [profiles.xml](LiveAnnotationProfile.md) file in the CrewWeb development tree. Each annotation type can have multiple **`contains`** fields. These fields will be used in the order they are specified in the xml-code.
    * **`name`** – The name of the live annotation type.
> > The name must be one of the **`id`** values of a **`profileEntry`** in the [profiles.xml](LiveAnnotationProfile.md) file in order to go into the annotations database
    * **`type`**
> > defines the input fields the [live annotation tool](LiveAnnotationTool.md) presents to the user:
      * **`text`** – The live annotation tool presents a single line text box to input the value to be assigned.
      * **`textarea`** – The live annotation tool presents a multi-line textarea to input longer messages. This is to store the main body of the annotation (There should only be one **`textarea`** in each annotation type).
      * **`messageId`** – The live annotation tool assigns the annotationId of the selected annotation to the value of that variable. This type should only be used with annotation types that are **`visible="inline"`**.
    * **`displayname`** – The heading for the input-box to be shown in the live annotation tool.
> > If **`displayname`** is not set the **`name`** will be shown.
    * <tt>user-defined-variable</tt>_> > The user can define any number of variables to be used in the **`<format>`** sections. To access these variables the variable-name is used as extension of the **`name`** of the **`contains`** field it was specified in:
> > >_<tt><b>${</b><i>name</i><b>.</b><i>variable-name</i><b>}</b></tt>

> > Example:
```
<contains name="CrewLiveAnnotationUrl" type="text" image="/img/small/link.png" displayname="Url" />
```
> > to use the image:
> > > <tt><i>/img/small/link.png</i></tt>

> > you use:
```
${CrewLiveAnnotationUrl.image}
```

  * **`<format> ... </format>`**

> The format section provides a set of formats that are used to show an annotation of the type. Currently there are 3 formats used:
    * **`<input> ... </input>`** – Escaped-HTML string that is used to print the annotation in the 'chat part' of live annotation tool.
    * **`<player> ... </player>`** – Escaped-HTML string that is used to output the annotation in the tool-tips of the time-slider as well as the annotation bar of the CrewPlayer.
    * **`<output> ... </output>`** plain text string output format  of the annotation
> The format strings are parsed to replace variables <tt><b>${</b><i>variable-name</i><b>}</b></tt> by the value of the variable. This can be used for parts of the annotation using the **`name`** of the **`contains`** field or using the user-defined variables as described above (<tt><b>${</b><i>name</i><b>.</b><i>variable-name</i><b>}</b></tt>).
> Formats that contain HTML-code need to replace angle brackets by their HTML-replacement to not interfere with the XML:
    * **`<` → `&lt;`**
    * **`>` → `&gt;`**
> Example:
```
<contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
<contains name="CrewLiveAnnotationTitle" type="text" displayname="Name" />
<contains name="CrewLiveAnnotationUrl" type="text" image="/img/small/link.png" displayname="Url" />
<format>
    <input>
        &lt;a href="${CrewLiveAnnotationUrl}"&gt;
        ${CrewLiveAnnotationTitle}
        (&lt;img src="${CrewLiveAnnotationUrl.image}"/&gt;
        {CrewLiveAnnotationUrl})&lt;/a&gt;
        ${CrewLiveAnnotationComment}
    </input>
    ...
</format>
```
> will be replaced to:
> > <tt><b><code>&lt;a href="</code></b><i>AnnotationUrl</i><b><code>"&gt;</code></b> <i>AnnotationTitle</i> <b><code>(&lt;img src="</code></b>/img/small/link.png<b><code>"/&gt; </code></b><i>AnnotationUrl</i><b><code>)&lt;/a&gt;  </code></b> <i>AnnotationComment</i></tt>

  * **`<convertsTo> ... </convertsTo>`**

> The convertsTo section provides a list of annotation types that can be used as valid conversions if some of the variables are not set. These conversion types are mainly there to provide formating rules that the annotation tool and player can render the annotation correctly. Most of the conversion types will be invisible to the end user and have the original type specified as the type to store the annotation in. The type of the annotation will be set to the type it converts to.
    * **`<type name="..."/>`** defines the name of another type in this XML file that this annotation type can be converted to. Outputs that use the formating specified in this XML file will do a best match (having the most parameters set) of types an annotation can convert to. The author of this file should provide conversions for all possible cases.
