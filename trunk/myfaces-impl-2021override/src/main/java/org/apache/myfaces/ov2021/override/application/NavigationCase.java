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
package org.apache.myfaces.ov2021.override.application;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Fix over bookmarkable url
 * 
 */
public class NavigationCase extends javax.faces.application.NavigationCase
{

    public NavigationCase(String fromViewId, String fromAction, String fromOutcome, String condition, 
            String toViewId, Map<String, List<String>> parameters, boolean redirect, boolean includeViewParams)
    {
        super(fromViewId, fromAction, fromOutcome, condition, toViewId, parameters, redirect, includeViewParams);
    }

    public URL getBookmarkableURL(FacesContext context) throws MalformedURLException
    {
        ExternalContext externalContext = context.getExternalContext();
        return new URL(externalContext.getRequestScheme(),
                externalContext.getRequestServerName(),
                externalContext.getRequestServerPort(),
                context.getApplication().getViewHandler().getBookmarkableURL(context, getToViewId(context), 
                        _NavigationUtils.getEvaluatedNavigationParameters(context,
                             getParameters()), isIncludeViewParams()));
    }

    public URL getRedirectURL(FacesContext context) throws MalformedURLException
    {
        ExternalContext externalContext = context.getExternalContext();
        return new URL(externalContext.getRequestScheme(),
                externalContext.getRequestServerName(),
                externalContext.getRequestServerPort(),
                context.getApplication().getViewHandler().getRedirectURL(context, getToViewId(context), 
                        _NavigationUtils.getEvaluatedNavigationParameters(context,
                             getParameters()), isIncludeViewParams()));
    }

}
