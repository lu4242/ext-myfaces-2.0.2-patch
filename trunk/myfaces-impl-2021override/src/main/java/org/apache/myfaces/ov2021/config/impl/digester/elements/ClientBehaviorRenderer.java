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
package org.apache.myfaces.ov2021.config.impl.digester.elements;

import java.io.Serializable;

/**
 * Config holder for <client-behavior-renderer> element.
 */

public class ClientBehaviorRenderer extends org.apache.myfaces.ov2021.config.element.ClientBehaviorRenderer implements Serializable
{
    private String rendererType;
    private String rendererClass;
    
    public String getRendererClass ()
    {
        return rendererClass;
    }
    
    public String getRendererType ()
    {
        return rendererType;
    }
    
    public void setRendererClass (String clazz)
    {
        rendererClass = clazz;
    }
    
    public void setRendererType (String type)
    {
        rendererType = type;
    }
}