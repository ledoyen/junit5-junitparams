# JUnit5-JunitParams
[JUnitParams](https://github.com/Pragmatists/JUnitParams) for [JUnit5 Jupiter](https://github.com/junit-team/junit5).



![uncheck](http://www.iconsdb.com/icons/download/soylent-red/x-mark-4-16.png) Annotation `@Test` is ignored by the extension as **JUnit5 Jupiter** discovers it as a standalone test, whereas the `@Parameters` one ships with concurrent `@TestTemplate`.

![check](http://www.iconsdb.com/icons/download/royal-blue/ok-16.png) Classic tests annotated with `@Test` only are discovered and run as usual.

## Samples
```java
@Parameters({
        "17, false",
        "22, true"})
public void isAdultAgeDirect(int age, boolean valid) throws Exception {
    assertThat(new Person(age).isAdult()).isEqualTo(valid);
}

@Test // annotation is ignored, method is only called twice as there is two set of parameters
@Parameters(method = "adultValues")
public void isAdultAgeDefinedMethod(int age, boolean valid) throws Exception {
    assertThat(new Person(age).isAdult()).isEqualTo(valid);
}

private Object[] adultValues() {
    return new Object[]{new Object[]{17, false}, new Object[]{22, true}};
}

@Parameters
public void isAdultAgeDefaultMethod(int age, boolean valid) throws Exception {
    assertThat(new Person(age).isAdult()).isEqualTo(valid);
}

@SuppressWarnings("unused")
private Object[] parametersForIsAdultAgeDefaultMethod() {
    return adultValues();
}
```

## JUnit5 Jupiter current limitations

* There is no proper way to retrieve the type of test from the `ExtensionContext` (isContainer, isTemplate)
  * `JUnitParamsExtension` extension currently use _**breakable**_ internal class comparison

  ```java
  private static Class<?> TEMPLATE_CONTEXT_CLASS =
    ReflectionUtils.loadClass(
        "org.junit.jupiter.engine.descriptor.TestTemplateContainerExtensionContext").get();

  boolean parentContextIsTemplateOne = TEMPLATE_CONTEXT_CLASS.equals(context.getParent().get().getClass());
  ```
  This is breakable as internal class are package-protected, and are not intended to be use from outside the framework

* It is not possible to invoke non-static methods on the test instance from extensions
  * As **JUnit5 Jupiter** supports constructor parameter injection, all extensions except `TestInstancePostProcessor` run before test class instantiation
  * `JUnitParamsExtension` extension currently use _**breakable**_ test class instantiation with default JUnit5 extensions (see `ExtensionRegistry#DEFAULT_EXTENSIONS`)

  ```java
  Constructor<?> constructor = ReflectionUtils.getDeclaredConstructor(testClass);
  Object instance = executableInvoker
    .invoke(constructor,
            extensionContext,
            ExtensionRegistry.createRegistryWithDefaultExtensions(new EmptyConfigurationParameters()));
  ```
  This is breakable as both explicit and registered (by ServiceLoader mechanism) extensions cannot be used for constructor parameter injection for classes containing `@Parameters` tests.
