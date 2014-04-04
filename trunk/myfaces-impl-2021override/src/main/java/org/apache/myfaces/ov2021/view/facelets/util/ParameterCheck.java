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
package org.apache.myfaces.ov2021.view.facelets.util;

/**
 * @author Jacob Hookom
 * @version $Id: ParameterCheck.java 1187700 2011-10-22 12:19:37Z bommel $
 */
public final class ParameterCheck
{

    public final static void notNull(String name, Object value) throws NullPointerException
    {
        if (value == null)
        {
            throw new NullPointerException("Parameter '" + name + "' cannot be null");
        }
    }

}
