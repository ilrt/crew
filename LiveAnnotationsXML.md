
```
<?xml version="1.0" encoding="UTF-8"?>
<liveAnnotations>
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
    <liveAnnotationType name="Question" index="2">
        <button image="/img/question.png" visible="true" />
        <thumbnail name="/img/small/question.png" />
        <colour value="#61C4DC" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <format>
            <input>${CrewLiveAnnotationComment}</input>
            <player>${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
    </liveAnnotationType>
    <liveAnnotationType name="Answer" index="3">
        <button image="/img/answer.png" visible="true" />
        <thumbnail name="/img/small/answer.png" />
        <colour value="#FFFF99" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <format>
            <input>${CrewLiveAnnotationComment}</input>
            <player>${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
    </liveAnnotationType>
    <liveAnnotationType name="Response">
        <button image="/img/response.png" visible="inline" />
        <thumbnail name="/img/small/response.png" />
        <colour value="#FFFF99" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <contains name="CrewLiveAnnotationRelatesTo" type="messageId" />
        <format>
            <relatesTo>${CrewLiveAnnotationRelatesTo}</relatesTo>
            <input>${CrewLiveAnnotationComment}</input>
            <player>${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
    </liveAnnotationType>
    <liveAnnotationType name="Comment" index="4">
        <button image="/img/comment.png" visible="true" />
        <thumbnail name="/img/small/comment.png" />
        <colour value="#FFFFFF" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <format>
            <input>${CrewLiveAnnotationComment}</input>
            <player>${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
    </liveAnnotationType>
    <liveAnnotationType name="Important-Point" index="5">
        <button image="/img/important.png" visible="true" />
        <thumbnail name="/img/small/important.png" />
        <colour value="#CD3610" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <format>
            <input>${CrewLiveAnnotationComment}</input>
            <player>${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
    </liveAnnotationType>
    <liveAnnotationType name="Linked-Resource" index="6">
        <button image="/img/link.png" visible="true" />
        <thumbnail name="/img/small/link.png" />
        <colour value="#5CD43A" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <contains name="CrewLiveAnnotationTitle" type="text" displayname="Name" />
        <contains name="CrewLiveAnnotationUrl" type="text" image="/img/small/link.png" displayname="Url" />
        <format>
            <input>&lt;a href="${CrewLiveAnnotationUrl}"&gt;${CrewLiveAnnotationTitle} (&lt;img src="${CrewLiveAnnotationUrl.image}" /&gt; ${CrewLiveAnnotationUrl})&lt;/a&gt; ${CrewLiveAnnotationComment}</input>
            <player>&lt;b&gt;${CrewLiveAnnotationTitle}&lt;/b&gt;&lt;br/&gt;&lt;font color="#0000ff"&gt;${CrewLiveAnnotationUrl}&lt;/font&gt;&lt;br/&gt;${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
        <convertsTo>
            <type name="Reference"/>
            <type name="URL-Resource"/>
            <type name="Comment"/>
        </convertsTo>
    </liveAnnotationType>
    <liveAnnotationType name="Reference" type="Linked-Resource">
        <button image="/img/link.png" visible="false" />
        <thumbnail name="/img/small/link.png" />
        <colour value="#5CD43A" />
        <contains name="CrewLiveAnnotationComment" type="textarea" /> 
        <contains name="CrewLiveAnnotationTitle" type="text"/>
        <format>
            <input>&lt;em&gt;${CrewLiveAnnotationTitle}&lt;/em&gt; ${CrewLiveAnnotationComment}</input>
            <player>&lt;b&gt;${CrewLiveAnnotationTitle}&lt;/b&gt;&lt;br/&gt;${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
        <convertsTo>
            <type name="Comment"/>
        </convertsTo>
    </liveAnnotationType>
    <liveAnnotationType name="URL-Resource" type="Linked-Resource">
        <button image="/img/link.png" visible="false" />
        <thumbnail name="/img/small/link.png" />
        <colour value="#5CD43A" />
        <contains name="CrewLiveAnnotationComment" type="textarea" /> 
        <contains name="CrewLiveAnnotationUrl" type="text" image="/img/small/link.png"/>
        <format>
            <input>&lt;a href="${CrewLiveAnnotationUrl}"&gt;${CrewLiveAnnotationUrl}&lt;/a&gt; ${CrewLiveAnnotationComment}</input>
            <player>&lt;b&gt;&lt;font color="#0000ff"&gt;${CrewLiveAnnotationUrl}&lt;/font&gt;&lt;/b&gt;&lt;br/&gt;${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
        <convertsTo>
            <type name="Comment"/>
        </convertsTo>
    </liveAnnotationType>
    <liveAnnotationType name="Person" index="7">
        <button image="/img/person.png" visible="true" />
        <thumbnail name="/img/small/person.png" />
        <colour value="#CDA781" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <contains name="CrewLiveAnnotationTitle" type="text" displayname="Name" />
        <contains name="CrewLiveAnnotationUrl" type="text" image="/img/small/link.png" displayname="Url" />
        <contains name="CrewLiveAnnotationEmail" type="text" image="/img/small/email.png" displayname="Email" />
        <format>
            <input>${CrewLiveAnnotationTitle} (&lt;a href="mailto:${CrewLiveAnnotationEmail}"&gt;&lt;img src="${CrewLiveAnnotationEmail.image}"&gt; ${CrewLiveAnnotationEmail} /&lt;/a&gt;, &lt;a href="${CrewLiveAnnotationUrl}"&gt;&lt;img src="${CrewLiveAnnotationUrl.image}" /&gt; ${CrewLiveAnnotationUrl}&lt;/a&gt;) ${CrewLiveAnnotationComment}</input>
            <player>&lt;b&gt;${CrewLiveAnnotationTitle}&lt;/b&gt;&lt;br/&gt;&lt;font color="#0000ff"&gt;${CrewLiveAnnotationUrl}&lt;br/&gt;${CrewLiveAnnotationEmail}&lt;/font&gt;&lt;br/&gt;${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
        <convertsTo>
            <type name="URL-Resource"/>
            <type name="Person2"/>
            <type name="Person3"/>
            <type name="Person4"/>
            <type name="Comment"/>
        </convertsTo>
    </liveAnnotationType>
    <liveAnnotationType name="Person2" type="Person" >
        <button image="/img/person.png" visible="false" />
        <thumbnail name="/img/small/person.png" />
        <colour value="#CDA781" />
        <contains name="CrewLiveAnnotationComment" type="textarea" /> 
        <contains name="CrewLiveAnnotationTitle" type="text"/>
        <contains name="CrewLiveAnnotationEmail" type="text" image="/img/small/email.png"/>
        <format>
            <input>${CrewLiveAnnotationTitle} (&lt;a href="mailto:${CrewLiveAnnotationEmail}"&gt;&lt;img src="${CrewLiveAnnotationEmail.image}" /&gt; ${CrewLiveAnnotationEmail}&lt;/a&gt;) ${CrewLiveAnnotationComment}</input>
            <player>&lt;b&gt;${CrewLiveAnnotationTitle}&lt;/b&gt;&lt;br/&gt;&lt;font color="#0000ff"&gt;${CrewLiveAnnotationEmail}&lt;/font&gt;&lt;br/&gt;${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
        <convertsTo>
            <type name="Comment"/>
        </convertsTo>
    </liveAnnotationType>
    <liveAnnotationType name="Person3" type="Person">
        <button image="/img/person.png" visible="false" />
        <thumbnail name="/img/small/person.png" />
        <colour value="#CDA781" />
        <contains name="CrewLiveAnnotationComment" type="textarea" /> 
        <contains name="CrewLiveAnnotationTitle" type="text"/>
        <contains name="CrewLiveAnnotationUrl" type="text" image="/img/small/link.png"/>
        <format>
            <input>${CrewLiveAnnotationTitle} (&lt;a href="${CrewLiveAnnotationUrl}"&gt;&lt;img src="${CrewLiveAnnotationUrl.image}" /&gt; ${CrewLiveAnnotationUrl}&lt;/a&gt;) ${CrewLiveAnnotationComment}</input>
            <player>&lt;b&gt;${CrewLiveAnnotationTitle}&lt;/b&gt;&lt;br/&gt;&lt;font color="#0000ff"&gt;${CrewLiveAnnotationUrl}&lt;/font&gt;&lt;br/&gt;${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
        <convertsTo>
            <type name="Comment"/>
        </convertsTo>
    </liveAnnotationType>
    <liveAnnotationType name="Person4" type="Person" >
        <button image="/img/person.png" visible="false" />
        <thumbnail name="/img/small/person.png" />
        <colour value="#CDA781" />
        <contains name="CrewLiveAnnotationComment" type="textarea" /> 
        <contains name="CrewLiveAnnotationTitle" type="text" />
        <format>
            <input>&lt;em&gt;${CrewLiveAnnotationTitle}&lt;/em&gt; ${CrewLiveAnnotationComment}</input>
            <player>&lt;b&gt;${CrewLiveAnnotationTitle}&lt;/b&gt; &lt;br/&gt;${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
        <convertsTo>
            <type name="Comment"/>
        </convertsTo>
    </liveAnnotationType>
    <liveAnnotationType name="Slide" index="1">
        <button image="/img/slide.png" visible="true" />
        <thumbnail name="/img/small/slide.png" />
        <contains name="CrewLiveAnnotationComment" type="textarea" displayname="Comment" /> 
        <format>
            <input>${CrewLiveAnnotationComment}</input>
            <player>${CrewLiveAnnotationComment}</player>
            <output>${CrewLiveAnnotationComment}</output>
        </format>
    </liveAnnotationType>
</liveAnnotations>
```