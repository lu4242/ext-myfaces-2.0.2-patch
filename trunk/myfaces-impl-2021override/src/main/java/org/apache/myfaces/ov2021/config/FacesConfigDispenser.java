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
package org.apache.myfaces.ov2021.config;

import java.util.Collection;

import javax.el.ELResolver;

import org.apache.myfaces.ov2021.config.element.Behavior;
import org.apache.myfaces.ov2021.config.element.ClientBehaviorRenderer;
import org.apache.myfaces.ov2021.config.element.FacesConfigData;
import org.apache.myfaces.ov2021.config.element.ManagedBean;
import org.apache.myfaces.ov2021.config.element.NavigationRule;
import org.apache.myfaces.ov2021.config.element.Renderer;
import org.apache.myfaces.ov2021.config.element.Converter;
import org.apache.myfaces.ov2021.config.element.NamedEvent;
import org.apache.myfaces.ov2021.config.element.ResourceBundle;
import org.apache.myfaces.ov2021.config.element.SystemEventListener;

/**
 * Subsumes several unmarshalled faces config objects and presents a simple interface
 * to the combined configuration data.
 *
 * @author Manfred Geiler (latest modification by $Author: bommel $)
 * @version $Revision: 1187700 $ $Date: 2011-10-22 14:19:37 +0200 (Sa, 22 Okt 2011) $
 */
public abstract class FacesConfigDispenser extends FacesConfigData
{
    /**
     * 
     */
    private static final long serialVersionUID = 9123062381457766144L;

    /**
     * Add another unmarshalled faces config object.
     * @param facesConfig unmarshalled faces config object
     */
    public abstract void feed(org.apache.myfaces.ov2021.config.element.FacesConfig facesConfig);

    /**
     * Add another ApplicationFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedApplicationFactory(String factoryClassName);

    /**
     * Add another ExceptionHandlerFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedExceptionHandlerFactory(String factoryClassName);

    /**
     * Add another ExternalContextFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedExternalContextFactory(String factoryClassName);

    /**
     * Add another FacesContextFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedFacesContextFactory(String factoryClassName);

    /**
     * Add another LifecycleFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedLifecycleFactory(String factoryClassName);
    
    /**
     * Add another ViewDeclarationLanguageFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedViewDeclarationLanguageFactory(String factoryClassName);

    /**
     * Add another PartialViewContextFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedPartialViewContextFactory(String factoryClassName);

    /**
     * Add another RenderKitFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedRenderKitFactory(String factoryClassName);
    
    /**
     * Add another TagHandlerDelegateFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedTagHandlerDelegateFactory(String factoryClassName);

    /**
     * Add another VisitContextFactory class name
     * @param factoryClassName a class name
     */
    public abstract void feedVisitContextFactory(String factoryClassName);

}
