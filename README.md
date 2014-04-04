ext-myfaces-2.0.2-patch
=======================

Ext202Patch-Extension Patch for MyFaces Core 2.0.2

Instructions

To use the override for 2.0.2 with the new 2.0.21 code, compile the code in your local maven repo and add the resulting jar into the project:

    <dependency>
		<groupId>org.apache.myfaces.ext202patch</groupId>
		<artifactId>myfaces-impl-2021override</artifactId>		
		<version>1.0-SNAPSHOT</version>
	</dependency>

and these lines into web.xml:


    <context-param>
        <param-name>org.apache.myfaces.FACES_INITIALIZER</param-name>
        <param-value>org.apache.myfaces.ov2021.webapp.FaceletsInitilializer</param-value>
    </context-param>
    
    <context-param>
        <param-name>org.apache.myfaces.EXPRESSION_FACTORY</param-name>
        <param-value>the.choosen.implementation.of.EL.ExpressionFactoryImpl</param-value>
    </context-param>
    
That's it.
