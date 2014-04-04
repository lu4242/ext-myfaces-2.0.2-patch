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
package org.apache.myfaces.ov2021.view.facelets.tag.jsf;

import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;

/**
 * This interface provide a way to delegate component creation
 * from ComponentTagHandlerDelegate. This is necessary because
 * CompositeComponentResourceTagHandler should call 
 * Application.createComponent(FacesContext context, Resource componentResource)
 * and ComponentTagHandlerDelegate does not have the required
 * Resource dependency.
 * 
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 802920 $ $Date: 2009-08-10 22:00:36 +0200 (Mo, 10 Aug 2009) $
 */
public interface ComponentBuilderHandler
{
    public UIComponent createComponent(FaceletContext ctx);
}