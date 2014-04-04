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
 * @author <a href="mailto:oliver@rossmueller.com">Oliver Rossmueller</a>
 */
public class Component extends org.apache.myfaces.ov2021.config.element.Component implements Serializable
{

    private String componentType;
    private String componentClass;


    public void setComponentType(String componentType)
    {
        this.componentType = componentType;
    }


    public void setComponentClass(String componentClass)
    {
        this.componentClass = componentClass;
    }


    public String getComponentType()
    {
        return componentType;
    }


    public String getComponentClass()
    {
        return componentClass;
    }
}
