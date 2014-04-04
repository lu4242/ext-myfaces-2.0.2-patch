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

import javax.faces.context.FacesContext;
import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFRenderer;
import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFRenderers;
import org.apache.myfaces.shared_ext202patch.renderkit.html.HtmlMenuRendererBase;


/**
 *   
 * @author Manfred Geiler (latest modification by $Author: lu4242 $)
 * @version $Revision: 1230370 $ $Date: 2012-01-12 03:07:58 +0100 (Do, 12 Jän 2012) $
 */
@JSFRenderers(renderers={
    @JSFRenderer(
        renderKitId="HTML_BASIC",
        family="javax.faces.SelectOne",
        type="javax.faces.Menu"),    
    @JSFRenderer(
        renderKitId="HTML_BASIC",
        family="javax.faces.SelectMany",
        type="javax.faces.Menu")
})
public class HtmlMenuRenderer
        extends HtmlMenuRendererBase
{
    //private static final Log log = LogFactory.getLog(HtmlMenuRenderer.class);
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
