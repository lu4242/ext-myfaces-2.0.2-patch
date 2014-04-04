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
package org.apache.myfaces.ov2021.lifecycle;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

/**
 * Implements the apply request values phase (JSF Spec 2.2.2)
 * 
 * @author Nikolay Petrov (latest modification by $Author: lu4242 $)
 * @version $Revision: 1141327 $ $Date: 2011-06-30 01:32:34 +0200 (Do, 30 Jun 2011) $
 */
class ApplyRequestValuesExecutor extends PhaseExecutor
{
    public boolean execute(FacesContext facesContext)
    {
        if (facesContext.getViewRoot() == null)
        {
            throw new ViewNotFoundException("A view is required to execute "+facesContext.getCurrentPhaseId());
        }
        facesContext.getViewRoot().processDecodes(facesContext);
        return false;
    }

    public PhaseId getPhase()
    {
        return PhaseId.APPLY_REQUEST_VALUES;
    }
}
