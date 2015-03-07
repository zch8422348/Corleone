/*
 * Copyright (C) 2015 Jorge Castillo Pérez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jorgecastilloprz.corleone;

import com.github.jorgecastilloprz.corleone.annotations.Job;
import com.github.jorgecastilloprz.corleone.annotations.Rule;

/**
 * Corleone gateway for external apps. Used to manage job dispatching and to connect jobs to
 * autogenerated param providers.
 *
 * @author Jorge Castillo Pérez
 */
public class Corleone {

  public static void dispatchJobsWithContext(String context, JobParams jobParams) {

  }

  public static void dispatchJobsWithContext(String context) {

  }

  /**
   * Gives the user the capability of providing new params for a given context.
   */
  public static void provideParamForContext(String context, String qualifier, Class<?> paramValue) {
    if (context == null || context.equals("")) {
      throw new IllegalArgumentException("Context must not be null or empty.");
    }
    if (qualifier == null || qualifier.equals("")) {
      throw new IllegalArgumentException("Param qualifier must not be null or empty.");
    }
    if (paramValue == null) {
      throw new IllegalArgumentException("Param value must not be null.");
    }
    ParamBinderHelper.addProvidedParam(context, qualifier, paramValue);
  }

  /**
   * Gives the user the capability of providing new params for all the contexts of the given job.
   */
  public static void provideParamForAllContexts(Class<?> job, String qualifier,
      Class<?> paramValue) {
    Job jobAnnotation = job.getAnnotation(Job.class);
    if (jobAnnotation == null) {
      throw new IllegalArgumentException(
          "You need a Job annotated class to provide params for all it's contexts.");
    }

    Rule[] jobRules = jobAnnotation.value();
    for (Rule rule : jobRules) {
      provideParamForContext(rule.context(), qualifier, paramValue);
    }
  }

  public static void keepGoing() {

  }
}
