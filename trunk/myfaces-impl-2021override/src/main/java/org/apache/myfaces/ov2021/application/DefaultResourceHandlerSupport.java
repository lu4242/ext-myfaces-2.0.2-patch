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
package org.apache.myfaces.ov2021.application;

import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import org.apache.myfaces.ov2021.resource.InternalClassLoaderResourceLoader;

import org.apache.myfaces.shared_ext202patch.renderkit.html.util.ResourceUtils;
import org.apache.myfaces.shared_ext202patch.resource.BaseResourceHandlerSupport;
import org.apache.myfaces.shared_ext202patch.resource.ClassLoaderResourceLoader;
import org.apache.myfaces.shared_ext202patch.resource.ExternalContextResourceLoader;
import org.apache.myfaces.shared_ext202patch.resource.ResourceLoader;
import org.apache.myfaces.shared_ext202patch.util.WebConfigParamUtils;

/**
 * A ResourceHandlerSupport implementation for use with standard Java Servlet engines,
 * ie an engine that supports javax.servlet, and uses a standard web.xml file.
 * 
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 1187068 $ $Date: 2011-10-20 23:44:52 +0200 (Do, 20 Okt 2011) $
 */
public class DefaultResourceHandlerSupport extends BaseResourceHandlerSupport
{

    private static final String META_INF_RESOURCES = "META-INF/resources";
    private static final String RESOURCES = "/resources";
    private static final String META_INF_INTERNAL_RESOURCES = "META-INF/internal-resources";

    private ResourceLoader[] _resourceLoaders;
    
    public DefaultResourceHandlerSupport()
    {
        super();
    }

    public ResourceLoader[] getResourceLoaders()
    {
        if (_resourceLoaders == null)
        {
            FacesContext facesContext = FacesContext.getCurrentInstance(); 
            //The ExternalContextResourceLoader has precedence over
            //ClassLoaderResourceLoader, so it goes first.
            String renderedJSFJS = WebConfigParamUtils.getStringInitParameter(facesContext.getExternalContext(),
                    InternalClassLoaderResourceLoader.MYFACES_JSF_MODE,
                    ResourceUtils.JSF_MYFACES_JSFJS_NORMAL);

            if (facesContext.isProjectStage(ProjectStage.Development) ||
                 !renderedJSFJS.equals(ResourceUtils.JSF_MYFACES_JSFJS_NORMAL))
            {
                _resourceLoaders = new ResourceLoader[] {
                        new ExternalContextResourceLoader(RESOURCES),
                        new InternalClassLoaderResourceLoader(META_INF_INTERNAL_RESOURCES),
                        new ClassLoaderResourceLoader(META_INF_RESOURCES)
                };
            }
            else
            {
                _resourceLoaders = new ResourceLoader[] {
                        new ExternalContextResourceLoader(RESOURCES),
                        new ClassLoaderResourceLoader(META_INF_RESOURCES)
                };
            }
        }
        return _resourceLoaders;
    }
}
