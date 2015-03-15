Corleone
========

![Corleone image](/art/corleone.png)

Java annotation processor library used to dispatch and concatenate background tasks in a decoupled way through a simple syntax.

Usage
=====

The use of the library is pretty simple. First of all, you will need to know about the available annotations.

Annotations
-----------

* `@Job`: Used on top of your background task classes, it can contain multiple annotations of type `@Rule`.
* `@Rule`: Every job can have multiple Rules. A `@Rule` is used to define an execution context for the job, and an optional `previousJob` class (For concatenation).
* `@Param`: Used on job class attribute fields to allow param injection through autogenerated param binders. As the responsibily of building job instances
will reside on the library, job class constructors will not be allowed. Params will satisfy their corresponding runtime values when the job gets instantiated by Corleone.
Keep reading to know how to provide them.
* `@Execution`: Used to annotate a job method. Only one method can be flagged as the `@Execution` method for a class. This will be the method containning the
background logic, as it will be the one executed automatically by Corleone at the right moment.

Jobs
----

Corleone loves to delegate all the dirty jobs on his beloved family members to avoid worries. `@Job` syntax will be like:
```java
@Job({
    @Rule(context = "ObtainGames", previousJob = CheckNetworkConnection.class)
})
public class GetGamesFromService {

    @Param("RestRepo") GameRepository restGameRepository;
    @Param("PageNum") int pageNumber;
    @Param("RestCallback") Callback callback;
    @Param("MainThread") MainThread mainThread;

    @Execution
    public void run() {
        try {
            List<Game> games = gameRepository.obtainGamesByPage(pageNumber);
            notifyGamesLoaded(games);
        }
        catch (ObtainGamesException exception) {
            notifyPetitionError(exception.getMessage());
        }
    }

    private void notifyGamesLoaded(final List<Game> games) {
      mainThread.post(new Runnable() {
         @Override
         public void run() {
            callback.onGamePageLoaded(games);
         }
      });
      Corleone.context("ObtainGames").provideParam("games", games);
      Corleone.keepGoing();
    }

    private void notifyPetitionError(final String message) {
      mainThread.post(new Runnable() {
         @Override
         public void run() {
            callback.onGettingGamesError(message);
         }
      });
    }
}
```

Job dispatch
---------------

To start the job chain dispatch, this will be your code:

```java
Corleone.context("ObtainGames").dispatchJobs();
```

Param providing
---------------

As you can see in the previous code snippet, `@Job` params are given by some kind of injection technique. You don't pass them as method or constructor arguments,
 but provide them by two different decoupled ways. The first one is into the `@Job` dispatch call arguments.
 
```java
JobParams jobParams = new JobParams()
		.append("ConnectivityManager", connectivityManager);
		.append("RestRepo", restGameRepository)
		.append("PersistenceRepo", persistenceGameRepository)
 		.append("PageNum", pageNumber)
		.append("RestCallback", getRestGameQueryCallback())
		.append("PersistenceCallback", getPersistenceGameQueryCallback())
		.append("MainThread", mainThread);

Corleone.context("ObtainGames").dispatchJobs(jobParams);
```

This example shows how to provide params for the whole `@Job` context execution. All the jobs linked to that context will be able to use them. This is the right 
way to provide params just before starting the job chain execution. The `append()` method **requires a param qualifier** and a param value.

The second way available is being used in the `GetGamesFromService` job code snippet:

```java
Corleone.context("ObtainGames").provideParam("MyParamQualifier", paramValue);
```

Params can be added by `provideParam()` method too. This method can be used to provide params from one `@Job` to another that will be executed later on. Sometimes 
we need to use the output results from one task as the input ones for a new one. This would be the right way to do so.

Dependencies
------------

* [AutoService][dependencies-1]: One of the providers of Google auto library. A metadata/config generator to avoid typical ServiceLoader manual setup.
* [JavaPoet][dependencies-2]: A Square's java source file generation library. It's the successor of JavaWriter.

Testing
-------

* [Compile-testing][testing-libs-1]: Google testing framework to allow testing java sources @ javac compile time.
* [Truth][testing-libs-2]: Google library to "humanize" language of JUnit testing assertions.
* [JUnit][testing-libs-3]: Base for all the project unit tests.

Developed By
------------
* Jorge Castillo Pérez - <jorge.castillo.prz@gmail.com>

<a href="https://www.linkedin.com/in/jorgecastilloprz">
  <img alt="Add me to Linkedin" src="https://github.com/JorgeCastilloPrz/EasyMVP/blob/master/art/linkedin.png" />
</a>

License
-------

    Copyright 2015 Jorge Castillo Pérez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [dependencies-1]: https://github.com/google/auto/tree/master/service
 [dependencies-2]: https://github.com/square/javapoet
 [testing-libs-1]: https://github.com/google/compile-testing
 [testing-libs-2]: https://github.com/google/truth
 [testing-libs-3]: http://junit.org/