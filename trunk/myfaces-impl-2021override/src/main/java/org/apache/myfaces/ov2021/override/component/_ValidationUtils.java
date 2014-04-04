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
package org.apache.myfaces.ov2021.override.component;

import javax.validation.Validation;

/**
 * Utility class that isolates javax.validation, to prevent ClassNotFoundException
 * 
 * @since 2.0
 * @author Leonardo Uribe (latest modification by $Author: jakobk $)
 * @version $Revision: 924566 $ $Date: 2010-03-18 01:11:36 +0100 (Do, 18 Mär 2010) $
 */
final class _ValidationUtils
{

    public static void tryBuildDefaultValidatorFactory()
    {
        Validation.buildDefaultValidatorFactory().getValidator();
    }
}
