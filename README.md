# JUnit5-JunitParams
[JUnitParams](https://github.com/Pragmatists/JUnitParams) for [JUnit5](https://github.com/junit-team/junit5).

Use of annotation `@Test` is now deprecated as it is discovered as a standalone test, whereas the `@Parameters` one ships with `@TestTemplate`.
The extension ignores `@Test` execution from a method is marked with both annotations (`@Test` and `@Parameters`).

## Sample:
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
