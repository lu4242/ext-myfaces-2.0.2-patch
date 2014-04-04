ext-myfaces-2.0.2-patch
=======================

Ext202Patch-Extension Patch for MyFaces Core 2.0.2

Instructions

To use the override for 2.0.2 with the new 2.0.21 code, add the jar and these lines into web.xml:


    <context-param>
        <param-name>org.apache.myfaces.FACES_INITIALIZER</param-name>
        <param-value>org.apache.myfaces.ov2021.webapp.FaceletsInitilializer</param-value>
    </context-param>
    
    <context-param>
        <param-name>org.apache.myfaces.EXPRESSION_FACTORY</param-name>
        <param-value>the.choosen.implementation.of.EL.ExpressionFactoryImpl</param-value>
    </context-param>
    
That't it.
