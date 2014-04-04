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
package org.apache.myfaces.ov2021.view.facelets.tag.composite;

import javax.faces.view.ValueHolderAttachedObjectTarget;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;

import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFFaceletTag;

/**
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 808706 $ $Date: 2009-08-28 02:58:48 +0200 (Fr, 28 Aug 2009) $
 */
@JSFFaceletTag(name="composite:valueHolder")
public class ValueHolderHandler extends AttachedObjectTargetHandler<ValueHolderAttachedObjectTarget>
{

    public ValueHolderHandler(TagConfig config)
    {
        super(config);
    }

    @Override
    protected ValueHolderAttachedObjectTarget createAttachedObjectTarget(
            FaceletContext ctx)
    {
        ValueHolderAttachedObjectTargetImpl target = new ValueHolderAttachedObjectTargetImpl();
        
        if (_name != null)
        {
            target.setName(_name.getValueExpression(ctx, String.class));
        }
        if (_targets != null)
        {
            target.setTargets(_targets.getValueExpression(ctx, String.class));
        }
        return target;
    }

}
