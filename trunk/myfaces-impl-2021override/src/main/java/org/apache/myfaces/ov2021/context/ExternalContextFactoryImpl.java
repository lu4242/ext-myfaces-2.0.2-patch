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
package org.apache.myfaces.ov2021.context;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.myfaces.ov2021.context.servlet.ServletExternalContextImpl;

/**
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 799765 $ $Date: 2009-08-01 00:55:49 +0200 (Sa, 01 Aug 2009) $
 * 
 * @since 2.0
 */
public class ExternalContextFactoryImpl extends ExternalContextFactory
{

    public static final String EXTERNAL_CONTEXT_KEY = 
        "org.apache.myfaces.context.servlet.ServletExternalContextImpl";
    
    @Override
    public ExternalContext getExternalContext(Object context, Object request,
            Object response) throws FacesException
    {
        if (context == null)
        {
            throw new NullPointerException("context");
        }
        if (request == null)
        {
            throw new NullPointerException("request");
        }
        if (response == null)
        {
            throw new NullPointerException("response");
        }

        if (context instanceof ServletContext)
        {
            ExternalContext externalContext = new ServletExternalContextImpl(
                    (ServletContext) context, (ServletRequest) request, (ServletResponse) response);
            
            externalContext.getRequestMap().put(EXTERNAL_CONTEXT_KEY, externalContext);
            
            return externalContext;
        }
        
        throw new FacesException("Unsupported context type " + context.getClass().getName());
    }
}
