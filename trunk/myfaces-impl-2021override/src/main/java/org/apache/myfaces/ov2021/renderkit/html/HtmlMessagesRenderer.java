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
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;

import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFRenderer;
import org.apache.myfaces.shared_ext202patch.renderkit.html.HtmlMessagesRendererBase;
import org.apache.myfaces.shared_ext202patch.renderkit.html.util.ResourceUtils;

/**
 * 
 * 
 * @author Manfred Geiler (latest modification by $Author: lu4242 $)
 * @author Thomas Spiegl
 * @version $Revision: 1230370 $ $Date: 2012-01-12 03:07:58 +0100 (Do, 12 Jän 2012) $
 */
@JSFRenderer(renderKitId = "HTML_BASIC", family = "javax.faces.Messages", type = "javax.faces.Messages")
public class HtmlMessagesRenderer extends HtmlMessagesRendererBase
{
    // private static final Log log = LogFactory.getLog(HtmlMessagesRenderer.class);

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException
    {
        super.encodeEnd(facesContext, component); // check for NP�
        
        Map<String, List<ClientBehavior>> behaviors = null;
        if (component instanceof ClientBehaviorHolder)
        {
            behaviors = ((ClientBehaviorHolder) component).getClientBehaviors();
            if (!behaviors.isEmpty())
            {
                ResourceUtils.renderDefaultJsfJsInlineIfNecessary(facesContext, facesContext.getResponseWriter());
            }
        }
        
        renderMessages(facesContext, component, false, true);
    }

    @Override
    protected String getSummary(FacesContext facesContext, UIComponent message, FacesMessage facesMessage,
                                String msgClientId)
    {
        return facesMessage.getSummary();
    }

    @Override
    protected String getDetail(FacesContext facesContext, UIComponent message, FacesMessage facesMessage,
                               String msgClientId)
    {
        return facesMessage.getDetail();
    }

    @Override
    protected boolean isCommonPropertiesOptimizationEnabled(FacesContext facesContext)
    {
        return false;
    }

    @Override
    protected boolean isCommonEventsOptimizationEnabled(FacesContext facesContext)
    {
        return false;
    }

}
