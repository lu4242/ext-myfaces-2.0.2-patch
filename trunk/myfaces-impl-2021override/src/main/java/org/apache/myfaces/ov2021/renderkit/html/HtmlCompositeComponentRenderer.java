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
package org.apache.myfaces.ov2021.renderkit.html;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFRenderer;
import org.apache.myfaces.shared_ext202patch.renderkit.html.HtmlRenderer;

/**
 * 
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 1151677 $ $Date: 2011-07-28 02:03:59 +0200 (Do, 28 Jul 2011) $
 */
@JSFRenderer(renderKitId = "HTML_BASIC", family = "javax.faces.NamingContainer", type = "javax.faces.Composite")
public class HtmlCompositeComponentRenderer extends HtmlRenderer
{
    //private static final Log log = LogFactory.getLog(HtmlCompositeComponentRenderer.class);
    private static final Logger log = Logger.getLogger(HtmlCompositeComponentRenderer.class.getName());
    
    public boolean getRendersChildren()
    {
        return true;
    }

    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException
    {
    }

    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException
    {
    }

    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException
    {
        UIComponent compositeFacet = (UIComponent) component.getFacet(UIComponent.COMPOSITE_FACET_NAME);
        
        if (compositeFacet == null)
        {
            if (log.isLoggable(Level.SEVERE))
            {
                log.severe("facet UIComponent.COMPOSITE_FACET_NAME not found when rendering composite component "+
                        component.getClientId(context));
            }
            return;            
        }
        compositeFacet.encodeAll(context);
    }

}
