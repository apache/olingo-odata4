/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.data;

/**
 * OData error.
 */
public interface ODataError {

    /**
     * Gets error code.
     *
     * @return error code.
     */
    String getCode();

    /**
     * Gets error message language.
     *
     * @return error message language.
     */
    String getMessageLang();

    /**
     * Gets error message.
     *
     * @return error message.
     */
    String getMessageValue();

    /**
     * Gets inner error message.
     *
     * @return inner error message.
     */
    String getInnerErrorMessage();

    /**
     * Gets inner error type.
     *
     * @return inner error type.
     */
    String getInnerErrorType();

    /**
     * Gets inner error stack-trace.
     *
     * @return inner error stack-trace
     */
    String getInnerErrorStacktrace();
}
