/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.ov2021.view.facelets.tag.composite;

import java.lang.reflect.Method;
import java.net.URL;

import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.apache.myfaces.ov2021.view.facelets.tag.TagLibrary;

/**
 * This class create composite component tag handlers for "http://java.sun.com/jsf/composite/"
 * namespace. Note that the class that create composite component tag handlers using its own 
 * namespace defined in facelet taglib .xml file see TagLibraryConfig.TagLibraryImpl
 * 
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 1300254 $ $Date: 2012-03-13 18:38:40 +0100 (Di, 13 Mär 2012) $
 */
public class CompositeResourceLibrary implements TagLibrary
{
    public final static String NAMESPACE_PREFIX = "http://java.sun.com/jsf/composite/";
    
    private final ResourceHandler _resourceHandler;
    
    public CompositeResourceLibrary(FacesContext facesContext)
    {
        _resourceHandler = facesContext.getApplication().getResourceHandler();
    }
    
    public boolean containsFunction(String ns, String name)
    {
        // Composite component tag library does not suport functions
        return false;
    }

    public boolean containsNamespace(String ns)
    {
        if (ns != null && ns.startsWith(NAMESPACE_PREFIX))
        {
            if (ns.length() > NAMESPACE_PREFIX.length())
            {
                String libraryName = ns.substring(NAMESPACE_PREFIX.length());
                return _resourceHandler.libraryExists(libraryName);
            }
        }        
        return false;
    }

    public boolean containsTagHandler(String ns, String localName)
    {
        if (ns != null && ns.startsWith(NAMESPACE_PREFIX))
        {
            if (ns.length() > NAMESPACE_PREFIX.length())
            {
                String libraryName = ns.substring(NAMESPACE_PREFIX.length());
                String resourceName = localName + ".xhtml";
                Resource compositeComponentResource = _resourceHandler.createResource(resourceName, libraryName);
                if (compositeComponentResource != null)
                {
                    URL url = compositeComponentResource.getURL();
                    return (url != null);
                }
            }
        }
        return false;
    }

    public Method createFunction(String ns, String name)
    {
        // Composite component tag library does not suport functions
        return null;
    }

    public TagHandler createTagHandler(String ns, String localName,
            TagConfig tag) throws FacesException
    {
        if (ns != null && ns.startsWith(NAMESPACE_PREFIX))
        {
            if (ns.length() > NAMESPACE_PREFIX.length())
            {
                String libraryName = ns.substring(NAMESPACE_PREFIX.length());
                String resourceName = localName + ".xhtml";
                // MYFACES-3308 If a composite component exists, it requires to 
                // be always resolved. In other words, it should always exists a default.
                // The call here for resourceHandler.createResource, just try to get
                // the Resource and if it does not exists, it just returns null.
                // The intention of this code is just create an instance and pass to
                // CompositeComponentResourceTagHandler. Then, its values 
                // (resourceName, libraryName) will be used to derive the real instance
                // to use in a view, based on the locale used.
                Resource compositeComponentResource = new CompositeResouceWrapper(
                    _resourceHandler.createResource(resourceName, libraryName));
                if (compositeComponentResource != null)
                {
                    ComponentConfig componentConfig = new ComponentConfigWrapper(tag,
                            "javax.faces.NamingContainer", null);
                    
                    return new CompositeComponentResourceTagHandler(componentConfig, compositeComponentResource);
                }
            }
        }
        return null;
    }

    private static class ComponentConfigWrapper implements ComponentConfig
    {

        protected final TagConfig parent;

        protected final String componentType;

        protected final String rendererType;

        public ComponentConfigWrapper(TagConfig parent, String componentType,
                                      String rendererType)
        {
            this.parent = parent;
            this.componentType = componentType;
            this.rendererType = rendererType;
        }

        public String getComponentType()
        {
            return this.componentType;
        }

        public String getRendererType()
        {
            return this.rendererType;
        }

        public FaceletHandler getNextHandler()
        {
            return this.parent.getNextHandler();
        }

        public Tag getTag()
        {
            return this.parent.getTag();
        }

        public String getTagId()
        {
            return this.parent.getTagId();
        }
    }
}
