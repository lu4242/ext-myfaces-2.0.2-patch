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

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

import org.apache.myfaces.shared_ext202patch.context.AjaxExceptionHandlerImpl;
import org.apache.myfaces.shared_ext202patch.context.ExceptionHandlerImpl;
import org.apache.myfaces.shared_ext202patch.context.SwitchAjaxExceptionHandlerWrapperImpl;

/**
 * DOCUMENT ME!
 * 
 * @author Leonardo Uribe (latest modification by $Author: lu4242 $)
 * @version $Revision: 1151677 $ $Date: 2011-07-28 02:03:59 +0200 (Do, 28 Jul 2011) $
 * 
 * @since 2.0
 */
public class ExceptionHandlerFactoryImpl extends ExceptionHandlerFactory
{

    @Override
    public ExceptionHandler getExceptionHandler()
    {
        return new SwitchAjaxExceptionHandlerWrapperImpl(
                new MyFacesExceptionHandlerWrapperImpl(new ExceptionHandlerImpl()) , 
                new AjaxExceptionHandlerImpl());
    }
}
